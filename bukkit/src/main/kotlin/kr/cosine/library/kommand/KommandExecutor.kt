package kr.cosine.library.kommand

import kr.cosine.library.config.YamlConfiguration
import kr.cosine.library.config.extension.yml
import kr.cosine.library.kommand.annotation.Kommand
import kr.cosine.library.kommand.annotation.SubKommand
import kr.cosine.library.kommand.argument.ArgumentProvider
import kr.cosine.library.kommand.argument.ArgumentRegistry
import org.bukkit.command.*
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.lang.NullPointerException
import kotlin.reflect.KFunction
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure

abstract class KommandExecutor(
    private val plugin: JavaPlugin
) : CommandExecutor, TabCompleter {

    private val languageFolder = File(plugin.dataFolder, "language")
    private val languageFileMap = mutableMapOf<String, YamlConfiguration>()

    private fun getLanguage(sender: CommandSender): YamlConfiguration {
        return (sender as? Player)?.let { languageFileMap[sender.locale] } ?: languageFileMap["en_us"]!!
    }

    private var pluginCommand: PluginCommand
    private val arguments = mutableMapOf<String, CommandArgument>()

    init {
        val kommand = this::class.annotations.filterIsInstance<Kommand>().firstOrNull()
            ?: throw NullPointerException("Kommand Annotation is not registered.")

        kommand.let {
            val command = it.command
            pluginCommand = plugin.getCommand(command)
                ?: throw NullPointerException("$command is not registered in plugin.yml.")
        }
        this::class.memberFunctions.filterIsInstance<KFunction<Unit>>().forEach { function ->
            function.annotations.filterIsInstance<SubKommand>().forEach { subKommand ->
                arguments[subKommand.argument] = CommandArgument(subKommand, function)
            }
        }
        languageFolder.listFiles()?.forEach { file ->
            val name = file.name.removeSuffix(".yml")
            languageFileMap[name] = file.yml
        }
    }

    fun register() {
        pluginCommand.setExecutor(this)
        pluginCommand.tabCompleter = this
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val argument = arguments[args[0]] ?: run {
            sender.sendMessage("§c존재하지 않는 명령어입니다.")
            return true
        }
        argument.runArgument(sender, args.copyOfRange(1, args.size))
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String>? {
        return emptyList()
    }

    private inner class CommandArgument(
        val subKommand: SubKommand,
        val function: KFunction<Unit>
    ) {

        private val parameters = ArrayList<Pair<ArgumentProvider<*>, Boolean>>()

        init {
            function.valueParameters.forEachIndexed { index, parameter ->
                println("[Before] $index. ${parameter.type.jvmErasure.simpleName}")
                if (index < 1) return@forEachIndexed
                println("[After] $index. ${parameter.type.jvmErasure.simpleName}")

                val type = parameter.type
                val className = type.jvmErasure.simpleName!!//!!.replace("Int", "Integer")
                val argument = ArgumentRegistry.getArgument(className) ?: return@forEachIndexed

                parameters.add(argument to !type.isMarkedNullable)
            }
        }

        fun runArgument(sender: CommandSender, args: Array<out String>) {
            val firstParameter = function.valueParameters.firstOrNull() ?: return
            val languageFile = getLanguage(sender)
            if (sender is ConsoleCommandSender && firstParameter.type.classifier == Player::class) {
                sender.sendMessage(languageFile.getString("error.command-only-player"))
                return
            }
            if (subKommand.isOp && !sender.isOp || subKommand.permission != "" && !sender.hasPermission(subKommand.permission)) {
                sender.sendMessage(languageFile.getString("error.has-not-permission"))
                return
            }
            val arguments = parameters.mapIndexed { index, pair ->
                val parameter = if (args.size > index) pair.first.cast(sender, args[index]) else null
                if (pair.second && parameter == null) {
                    sender.sendMessage("입력해주셈")
                    return
                } else {
                    parameter
                }
            }
            println("arguments: $arguments")
            function.javaMethod?.invoke(this@KommandExecutor, sender, *arguments.toTypedArray())
        }
    }
}