package kr.cosine.library.kommand.argument

import org.bukkit.Location
import org.bukkit.command.CommandSender
import kotlin.reflect.KClass

abstract class ArgumentProvider<T : Any>(
    private val clazz: KClass<T>
) {

    val genericName get() = clazz.simpleName!!.replace("Integer", "Int")

    abstract fun cast(sender: CommandSender, argument: String?): T

    abstract fun getTabComplete(sender: CommandSender, location: Location?): List<String>?
}