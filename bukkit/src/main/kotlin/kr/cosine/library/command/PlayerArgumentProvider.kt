package kr.cosine.library.command

import kr.cosine.library.kommand.annotation.Argument
import kr.cosine.library.kommand.argument.ArgumentProvider
import kr.cosine.library.kommand.exception.ArgumentMismatch
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Argument
class PlayerArgumentProvider : ArgumentProvider<Player> {

    override fun cast(sender: CommandSender, argument: String?): Player {
        if (argument == null) {
            throw ArgumentMismatch("input-player")
        }
        return Bukkit.getPlayer(argument) ?: throw ArgumentMismatch("player-offline")
    }

    override fun getTabComplete(sender: CommandSender, location: Location?): List<String> {
        return emptyList()
    }
}