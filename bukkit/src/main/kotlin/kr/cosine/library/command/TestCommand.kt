package kr.cosine.library.command

import kotlinx.coroutines.delay
import kr.cosine.library.CosineLibrary
import kr.cosine.library.kommand.KommandExecutor
import kr.cosine.library.kommand.annotation.BukkitAsync
import kr.cosine.library.kommand.annotation.Kommand
import kr.cosine.library.kommand.annotation.SubKommand
import kr.cosine.library.reflection.ClassNameRegistry
import org.bukkit.entity.Player

@Kommand("처벌")
class TestCommand(
    private val plugin: CosineLibrary
) : KommandExecutor(plugin) {

    @BukkitAsync
    @SubKommand("제재", priority = 1)
    fun test(player: Player, target: Player, code: Int) {
        player.sendMessage("입력: ${target.name}, $code")
    }

    @SubKommand("테스트", isOp = true, priority = 2)
    suspend fun test2(player: Player, target: Player, code: String, code2: String, code3: String, args: Array<String>) {
        delay(1000)
        player.sendMessage("입력: ${target.name}, $code, $code2, $code3, ${args.toList()}")
    }

    @SubKommand("확인", priority = 3)
    fun show(player: Player) {
        plugin.classNameRegistry.getAll().forEach {
            player.sendMessage("class registry: $it")
        }
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