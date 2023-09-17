package kr.cosine.library.kommand

import kr.cosine.library.extension.applyColor
import kr.cosine.library.extension.async
import kr.cosine.library.kommand.annotation.Kommand
import kr.cosine.library.kommand.annotation.SubKommand
import kr.cosine.library.kommand.argument.ArgumentProvider
import kr.cosine.library.kommand.argument.ArgumentRegistry
import kr.cosine.library.kommand.exception.ArgumentMismatch
import kr.cosine.library.kommand.language.Language
import kr.cosine.library.kommand.language.LanguageRegistry
import org.bukkit.command.*
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.reflect.KFunction
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure

abstract class KommandExecutor(
    private val plugin: JavaPlugin
) : CommandExecutor, TabCompleter {

    private var pluginCommand: PluginCommand

    private val arguments = mutableMapOf<String, CommandArgument>()

    private val languageRegistry: LanguageRegistry

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
        val languageFolder = File(plugin.dataFolder, "language")
        languageRegistry = LanguageRegistry(languageFolder)
        languageRegistry.load()
    }

    fun register() {
        pluginCommand.setExecutor(this)
        pluginCommand.tabCompleter = this
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        println("label: $label")
        if (args.isEmpty()) {
            if (sender is Player) {
                runDefaultCommand(sender, label)
            } else {
                runDefaultCommand(sender, label)
            }
            return true
        }
        val language = languageRegistry.getLanguage(sender)
        val argument = arguments[args[0]] ?: run {
            val message = language.getGlobalErrorMessage("not-exist-command").applyColor()
            sender.sendMessage(message)
            return true
        }
        argument.runArgument(sender, args.copyOfRange(1, args.size), language)
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String>? {
        return emptyList()
    }

    open fun runDefaultCommand(sender: CommandSender, label: String) {
        printHelp(sender, label)
    }

    open fun runDefaultCommand(player: Player, label: String) {
        printHelp(player, label)
    }

    private fun printHelp(sender: CommandSender, label: String) {
        sender.sendMessage("─────────────────────────")
        arguments.values.filter {
            it.hasPermission(sender) && !it.isHide()
        }.sortedBy {
            it.subKommand.priority
        }.forEach {
            it.printDescription(sender, label)
        }
        sender.sendMessage("─────────────────────────")
    }

    private inner class CommandArgument(
        val subKommand: SubKommand,
        val function: KFunction<Unit>
    ) {

        private val argumentParameters = ArrayList<Pair<ArgumentProvider<*>, Boolean>>()

        init {
            function.valueParameters.forEachIndexed { index, parameter ->
                println("[Before] $index. ${parameter.type.jvmErasure.simpleName}")
                if (index < 1) return@forEachIndexed
                println("[After] $index. ${parameter.type.jvmErasure.simpleName}")

                val type = parameter.type
                val className = type.jvmErasure.simpleName!!//!!.replace("Int", "Integer")
                val argument = ArgumentRegistry.getArgument(className) ?: return@forEachIndexed

                argumentParameters.add(argument to !type.isMarkedNullable)
            }
        }

        fun isHide(): Boolean = subKommand.hide

        fun hasPermission(sender: CommandSender): Boolean {
            return sender.isOp || subKommand.permission != "" && sender.hasPermission(subKommand.permission)
        }

        fun printDescription(sender: CommandSender, label: String) {
            val language = languageRegistry.getLanguage(sender)
            val rootCommandLabel = pluginCommand.name
            val argumentLabel = subKommand.argument
            val arguments = language.getArguments(rootCommandLabel, argumentLabel).applyColor()
            val argumentDescription = language.getDescription(rootCommandLabel, argumentLabel).applyColor()
            val description = "§6/${label} $argumentLabel §7$arguments §8- §f$argumentDescription"
            sender.sendMessage(description)
        }

        fun runArgument(sender: CommandSender, args: Array<out String>, language: Language) {
            val functionParameters = function.valueParameters
            val firstParameter = functionParameters.firstOrNull() ?: return
            if (sender is ConsoleCommandSender && firstParameter.type.classifier == Player::class) {
                val message = language.getGlobalErrorMessage("command-only-player").applyColor()
                sender.sendMessage(message)
                return
            }
            if (hasPermission(sender)) {
                val message = language.getGlobalErrorMessage("has-not-permission").applyColor()
                sender.sendMessage(message)
                return
            }
            val parameters = argumentParameters.mapIndexed { index, pair ->
                val input = if (args.size > index) args[index] else null
                // NonNull일 때
                if (pair.second) {
                    try {
                        pair.first.cast(sender, input)
                    } catch (e: ArgumentMismatch) {
                        val message = language.getArgumentErrorMessage(pluginCommand.name, subKommand.argument, e.path)
                            .applyColor()
                        sender.sendMessage(message)
                        return
                    }
                } else { // Nullable일 때
                    if (input == null) {
                        null
                    } else {
                        pair.first.cast(sender, input)
                    }
                }
            }.toMutableList()
            val lastParameter = functionParameters.lastOrNull()
            if (lastParameter?.type?.jvmErasure == Array<String>::class) {
                println("functionParameters: ${functionParameters.map { it.type.jvmErasure.simpleName }}")
                println("args: ${args.toList()}")
                val copyArgs = args.copyOfRange(functionParameters.size - 2, args.size)
                println("copyArgs: ${copyArgs.toList()}")
                parameters.add(copyArgs)
            }
            println("parameters: ${parameters.map { if (it is Array<*>) it.toList() else it.toString() }}")
            if (subKommand.async) {
                plugin.async { function.javaMethod?.invoke(this@KommandExecutor, sender, *parameters.toTypedArray()) }
            } else {
                function.javaMethod?.invoke(this@KommandExecutor, sender, *parameters.toTypedArray())
            }
        }
    }
}