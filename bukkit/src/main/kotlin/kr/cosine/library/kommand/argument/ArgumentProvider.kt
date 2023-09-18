package kr.cosine.library.kommand.argument

import org.bukkit.Location
import org.bukkit.command.CommandSender

interface ArgumentProvider<T : Any> {

    fun cast(sender: CommandSender, argument: String?): T

    fun getTabComplete(sender: CommandSender, location: Location?): List<String>?
}