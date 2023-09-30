package kr.cosine.library.command.provider

import kr.cosine.library.CosineLibrary
import kr.cosine.library.kommand.argument.ArgumentProvider
import kr.cosine.library.kommand.exception.ArgumentMismatch
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PlayerArgumentProvider(
    private val plugin: kr.cosine.library.CosineLibrary
) : ArgumentProvider<Player> {

    override fun cast(sender: CommandSender, argument: String?): Player {
        if (argument == null) {
            throw ArgumentMismatch("input-player")
        }
        return plugin.server.getPlayer(argument) ?: throw ArgumentMismatch("player-offline")
    }

    override fun getTabComplete(sender: CommandSender, location: Location?): List<String> {
        return emptyList()
    }
}