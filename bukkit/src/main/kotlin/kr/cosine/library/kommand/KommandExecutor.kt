package kr.cosine.library.kommand

import kotlinx.coroutines.*
import kr.cosine.library.extension.*
import kr.cosine.library.kommand.annotation.BukkitAsync
import kr.cosine.library.kommand.annotation.Kommand
import kr.cosine.library.kommand.annotation.SubKommand
import kr.cosine.library.kommand.argument.ArgumentProvider
import kr.cosine.library.kommand.argument.ArgumentProviderRegistry
import kr.cosine.library.kommand.argument.ArgumentRegistry
import kr.cosine.library.kommand.exception.ArgumentMismatch
import kr.cosine.library.kommand.language.Language
import kr.cosine.library.kommand.language.LanguageRegistry
import kr.cosine.library.kommand.page.PageType
import kr.cosine.library.plugin.BukkitPlugin
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.command.*
import org.bukkit.entity.Player
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KFunction
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure

abstract class KommandExecutor(
    private val plugin: BukkitPlugin
) : CommandExecutor, TabCompleter, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = CoroutineName("${this::class.simpleName}") + Dispatchers.IO

    private var pluginCommand: PluginCommand

    private val argumentProviderRegistry: ArgumentProviderRegistry

    private val argumentRegistry: ArgumentRegistry

    private val languageRegistry: LanguageRegistry

    init {
        val kommand = this::class.findAnnotation<Kommand>()
            ?: throw NullPointerException("Kommand Annotation is not registered.")
        kommand.also {
            val command = it.command
            pluginCommand = plugin.getCommand(command)
                ?: throw NullPointerException("$command is not registered in plugin.yml.")
        }
        argumentProviderRegistry = ArgumentProviderRegistry()
        val classRegistry = plugin.classRegistry
        val classNameRegistry = plugin.classNameRegistry
        classRegistry.getInheritedClasses(ArgumentProvider::class).forEach { clazz ->
            val qualifiedName = clazz.qualifiedName!!
            if (classNameRegistry.contains(qualifiedName)) return@forEach
            val constructor = clazz.primaryConstructor
            val argumentProvider = try {
                constructor?.call(plugin) as? ArgumentProvider<*>
            } catch (_: IllegalArgumentException) {
                constructor?.call() as? ArgumentProvider<*>
            } ?: run {
                plugin.logger.info("${clazz.simpleName} class's primary constructor call failed.", LogColor.RED)
                return@forEach
            }
            classNameRegistry.add(qualifiedName)
            argumentProviderRegistry.register(argumentProvider)
        }
        argumentRegistry = ArgumentRegistry()
        this::class.memberFunctions.filterIsInstance<KFunction<Unit>>().forEach { function ->
            val subKommand = function.findAnnotation<SubKommand>() ?: return@forEach
            val commandArgument = CommandArgument(subKommand, function)
            argumentRegistry.set(subKommand.argument, commandArgument)
        }
        val languageFolder = File(plugin.dataFolder, "language").also { if (!it.exists()) it.mkdirs() }
        languageRegistry = LanguageRegistry(languageFolder)
        languageRegistry.load(plugin)
    }

    fun register() {
        pluginCommand.setExecutor(this)
        pluginCommand.tabCompleter = this
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            if (sender is Player) {
                runDefaultCommand(sender, label)
                sender.attack(sender)
            } else {
                runDefaultCommand(sender, label)
            }
            return true
        }
        val language = languageRegistry.get(sender)
        val input = args[0]
        if (input == "help") {
            if (args.size == 1) {
                val message = language.getGlobalErrorMessage("input-page").applyColor()
                sender.sendMessage(message)
                return true
            }
            val page = args[1].toIntOrNull() ?: run {
                val message = language.getGlobalErrorMessage("only-integer").applyColor()
                sender.sendMessage(message)
                return true
            }
            if (!printHelp(sender, label, page)) {
                val message = language.getGlobalErrorMessage("not-exist-page").applyColor()
                sender.sendMessage(message)
                return true
            }
            return true
        }
        val argument = argumentRegistry.get(input) ?: run {
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
        printHelp(sender, label, 1)
    }

    open fun runDefaultCommand(player: Player, label: String) {
        printHelp(player, label, 1)
    }

    private fun printHelp(sender: CommandSender, label: String, page: Int): Boolean {
        val arguments = argumentRegistry.getValues().filter {
            it.hasPermission(sender) && !it.isHide()
        }.sortedBy {
            it.subKommand.priority
        }
        val chunkedArguments = arguments.chunked(5)

        val maxPage = chunkedArguments.size
        val realPage = page - 1
        val nowPageArguments = chunkedArguments.getOrNull(realPage) ?: return false

        val language = languageRegistry.get(sender)
        sender.sendMessage("")
        nowPageArguments.forEach {
            it.printDescription(sender, label, language)
        }
        val pageHelper = language.getPageHelper(pluginCommand.name)

        val beforePageElement = pageHelper.getPageElement(PageType.BEFORE)
        val currentPageElement = pageHelper.getPageElement(PageType.CURRENT)
        val nextPageElement = pageHelper.getPageElement(PageType.NEXT)

        val pageComponent = if (maxPage > 1 &&
            beforePageElement != null && currentPageElement != null && nextPageElement != null
        ) {
            val beforePageComponent = createPageComponent(
                beforePageElement.display,
                beforePageElement.showText,
                "/$label help ${page - 1}"
            )
            val currentPageComponent = createPageComponent(
                currentPageElement.display
                    .replace("%current%", "$page")
                    .replace("%max%", "$maxPage"),
                currentPageElement.showText
                    .replace("%current%", "$page")
            )
            val nextPageComponent = createPageComponent(
                nextPageElement.display,
                nextPageElement.showText,
                "/$label help ${page + 1}"
            )
            TextComponent().apply {
                addExtra(beforePageComponent)
                addExtra(currentPageComponent)
                addExtra(nextPageComponent)
            }
        } else {
            null
        }
        sender.sendMessage("")
        if (pageComponent != null) {
            sender.spigot().sendMessage(pageComponent)
        }
        return true
    }

    private fun createPageComponent(display: String, showText: String, command: String? = null): TextComponent {
        return TextComponent(display).apply {
            hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(showText))
            if (command != null) {
                clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
            }
        }
    }

    inner class CommandArgument(
        val subKommand: SubKommand,
        private val function: KFunction<Unit>
    ) {

        private var isBukkitAsync = function.hasAnnotation<BukkitAsync>()

        private val argumentParameters = ArrayList<Pair<ArgumentProvider<*>, Boolean>>()

        init {
            function.valueParameters.forEachIndexed { index, parameter ->
                if (index < 1) return@forEachIndexed

                val type = parameter.type
                val clazz = type.jvmErasure
                val argument = argumentProviderRegistry.get(clazz) ?: return@forEachIndexed

                argumentParameters.add(argument to !type.isMarkedNullable)
            }
        }

        fun isHide(): Boolean = subKommand.hide

        fun hasPermission(sender: CommandSender): Boolean {
            if (!subKommand.isOp) return true
            if (sender.isOp) return true
            if (subKommand.permission != "" && sender.hasPermission(subKommand.permission)) return true
            return false
        }

        fun printDescription(sender: CommandSender, label: String, language: Language) {
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
            if (!hasPermission(sender)) {
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
                        val message = language.getArgumentErrorMessage(
                            pluginCommand.name,
                            subKommand.argument,
                            e.path
                        ).applyColor()
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
            if (function.isSuspend) {
                println("function call suspend")
                launch { function.callSuspend(this@KommandExecutor, sender, *parameters.toTypedArray()) }
                return
            }
            if (isBukkitAsync) {
                println("function call bukkit async")
                plugin.async { function.call(this@KommandExecutor, sender, *parameters.toTypedArray()) }
                return
            }
            println("function call")
            function.call(this@KommandExecutor, sender, *parameters.toTypedArray())
        }
    }
}