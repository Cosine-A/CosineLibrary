package kr.cosine.library.command.provider

import kr.cosine.library.kommand.argument.ArgumentProvider
import org.bukkit.Location
import org.bukkit.command.CommandSender

class StringArgumentProvider : ArgumentProvider<String> {

    override fun cast(sender: CommandSender, argument: String?): String {
        return argument ?: throw NullPointerException("값 입력해주셈")
    }

    override fun getTabComplete(sender: CommandSender, location: Location?): List<String>? {
        return emptyList()
    }
}