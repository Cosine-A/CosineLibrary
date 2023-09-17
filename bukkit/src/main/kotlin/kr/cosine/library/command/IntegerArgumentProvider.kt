package kr.cosine.library.command

import kr.cosine.library.kommand.argument.ArgumentProvider
import kr.cosine.library.kommand.exception.ArgumentMismatch
import org.bukkit.Location
import org.bukkit.command.CommandSender

class IntegerArgumentProvider : ArgumentProvider<Int>(Int::class) {

    override fun cast(sender: CommandSender, argument: String?): Int {
        if (argument == null) {
            throw ArgumentMismatch("input-code")
        }
        return argument.toIntOrNull() ?: throw ArgumentMismatch("only-integer")
    }

    override fun getTabComplete(sender: CommandSender, location: Location?): List<String>? {
        return emptyList()
    }
}