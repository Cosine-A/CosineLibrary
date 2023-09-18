package kr.cosine.library.kommand.argument

import org.bukkit.Location
import org.bukkit.command.CommandSender

/**
 * 이 인터페이스를 상속 받는 클래스는 자동으로 등록됩니다.
 * (Classes that inherit this interface are automatically registered.)
 *
 * BukkitPlugin을 상속받는 클래스를 생성자에서 주입 받을 수 있습니다.
 * (A class that inherits BukkitPlugin can be injected from the constructor.)
**/
interface ArgumentProvider<T : Any> {

    fun cast(sender: CommandSender, argument: String?): T

    fun getTabComplete(sender: CommandSender, location: Location?): List<String>?
}