package kr.cosine.library.command

import kr.cosine.library.kommand.KommandExecutor
import kr.cosine.library.kommand.annotation.Kommand
import kr.cosine.library.kommand.annotation.SubKommand
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

@Kommand("처벌")
class TestCommand(plugin: JavaPlugin) : KommandExecutor(plugin) {

    @SubKommand("제재", "", isOp = true)
    fun one(player: Player, target: Player, code: Int?) {
        player.sendMessage("입력: ${target.name}, $code")
    }

    @SubKommand("테스트", "", isOp = true)
    fun one(player: Player, target: Player, code: String, code2: String, code3: String, args: Array<String>) {
        player.sendMessage("입력: ${target.name}, $code, $code2, $code3, ${args.toList()}")
    }
}