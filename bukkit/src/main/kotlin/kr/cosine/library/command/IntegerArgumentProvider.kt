package kr.cosine.library.command

import kr.cosine.library.kommand.argument.ArgumentProvider
import org.bukkit.Location
import org.bukkit.command.CommandSender

class IntegerArgumentProvider : ArgumentProvider<Int>(Int::class) {

    override fun cast(sender: CommandSender, argument: String?): Int {
        return argument?.toIntOrNull() ?: throw NullPointerException("값 입력해주셈")
    }

    override fun getTabComplete(sender: CommandSender, location: Location?): List<String>? {
        return emptyList()
    }
}