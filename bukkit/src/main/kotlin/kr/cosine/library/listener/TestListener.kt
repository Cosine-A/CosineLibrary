package kr.cosine.library.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class TestListener : Listener {

    @EventHandler
    fun onJoin(event: BlockBreakEvent) {
        event.player.sendMessage("블럭 부숨")
    }
}