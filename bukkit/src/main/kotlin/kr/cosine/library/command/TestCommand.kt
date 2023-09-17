package kr.cosine.library.command

import kr.cosine.library.kommand.KommandExecutor
import kr.cosine.library.kommand.annotation.Kommand
import kr.cosine.library.kommand.annotation.SubKommand
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

@Kommand("처벌")
class TestCommand(plugin: JavaPlugin) : KommandExecutor(plugin) {

    @SubKommand("제재", "", isOp = true, priority = 1)
    fun test(player: Player, target: Player, code: Int?) {
        player.sendMessage("입력: ${target.name}, $code")
    }

    @SubKommand("테스트", "", isOp = true, priority = 2)
    fun test2(player: Player, target: Player, code: String, code2: String, code3: String, args: Array<String>) {
        player.sendMessage("입력: ${target.name}, $code, $code2, $code3, ${args.toList()}")
    }

    @SubKommand("a", "", isOp = true, priority = 3)
    fun a(player: Player, target: Player) {}

    @SubKommand("b", "", isOp = true, priority = 4)
    fun b(player: Player, target: Player) {}

    @SubKommand("c", "", isOp = true, priority = 5)
    fun c(player: Player, target: Player) {}

    @SubKommand("d", "", isOp = true, priority = 6)
    fun d(player: Player, target: Player) {}

    @SubKommand("e", "", isOp = true, priority = 7)
    fun e(player: Player, target: Player) {}

    @SubKommand("f", "", isOp = true, priority = 8)
    fun f(player: Player, target: Player) {}

    @SubKommand("g", "", isOp = true, priority = 9)
    fun g(player: Player, target: Player) {}

    @SubKommand("h", "", isOp = true, priority = 10)
    fun h(player: Player, target: Player) {}

    @SubKommand("i", "", isOp = true, priority = 11)
    fun i(player: Player, target: Player) {}
}