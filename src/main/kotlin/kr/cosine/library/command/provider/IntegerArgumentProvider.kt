package kr.cosine.library.command.provider

import kr.cosine.library.CosineLibrary
import kr.cosine.library.kommand.argument.ArgumentProvider
import kr.cosine.library.kommand.exception.ArgumentMismatch
import org.bukkit.Location
import org.bukkit.command.CommandSender

class IntegerArgumentProvider(
    private val plugin: kr.cosine.library.CosineLibrary
) : ArgumentProvider<Int> {

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