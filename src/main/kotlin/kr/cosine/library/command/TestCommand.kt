package kr.cosine.library.command

import kotlinx.coroutines.delay
import kr.cosine.library.CosineLibrary
import kr.cosine.library.exception.BukkitException
import kr.cosine.library.exception.TestException
import kr.cosine.library.kommand.KommandExecutor
import kr.cosine.library.kommand.annotation.BukkitAsync
import kr.cosine.library.kommand.annotation.Kommand
import kr.cosine.library.kommand.annotation.SubKommand
import org.bukkit.entity.Player

@Kommand("처벌", BukkitException::class)
class TestCommand(
    private val plugin: CosineLibrary
) : KommandExecutor(plugin) {

    @BukkitAsync
    @SubKommand("제재", priority = 1)
    fun test(player: Player, target: Player, code: Int) {
        player.sendMessage("입력: ${target.name}, $code")
        throw TestException("test2")
    }

    private fun test2() {
        throw TestException("test")
    }

    @SubKommand("테스트", isOp = true, priority = 2)
    suspend fun test2(player: Player, target: Player, code: String, code2: String, code3: String, vararg args: String) {
        test2()
        delay(1000)
        test2()
        player.sendMessage("입력: ${target.name}, $code, $code2, $code3, ${args.toList()}")
    }

    @SubKommand("확인", priority = 3)
    fun show(player: Player) {
        plugin.classRegistry.getAll().forEach {
            player.sendMessage("class: ${it.simpleName}")
        }
    }

    @SubKommand("가변", priority = 3)
    fun show2(player: Player, text: String, vararg args: String) {
        player.sendMessage("입력: $text, ${args.toList()}")
    }

    @SubKommand("a", priority = 3)
    fun a(player: Player, target: Player) {}

    @SubKommand("b", isOp = true, priority = 4)
    fun b(player: Player, target: Player) {}

    @SubKommand("c", priority = 5)
    fun c(player: Player, target: Player) {}

    @SubKommand("d", isOp = true, priority = 6)
    fun d(player: Player, target: Player) {}

    @SubKommand("e", priority = 7)
    fun e(player: Player, target: Player) {}

    @SubKommand("f", isOp = true, priority = 8)
    fun f(player: Player, target: Player) {}

    @SubKommand("g", priority = 9)
    fun g(player: Player, target: Player) {}

    @SubKommand("h", isOp = true, priority = 10)
    fun h(player: Player, target: Player) {}

    @SubKommand("i", priority = 11)
    fun i(player: Player, target: Player) {}
}