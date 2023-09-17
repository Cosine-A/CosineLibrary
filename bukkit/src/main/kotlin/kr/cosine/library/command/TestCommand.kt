package kr.cosine.library.command

import kr.cosine.library.kommand.KommandExecutor
import kr.cosine.library.kommand.annotation.Kommand
import kr.cosine.library.kommand.annotation.SubKommand
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

@Kommand("테스트")
class TestCommand(plugin: JavaPlugin) : KommandExecutor(plugin) {

    @SubKommand("일", "", isOp = true)
    fun one(player: Player, text: String, value: Int?) {
        player.sendMessage("$text, $value")
    }
}