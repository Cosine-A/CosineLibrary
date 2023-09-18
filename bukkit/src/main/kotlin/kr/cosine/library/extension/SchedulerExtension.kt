package kr.cosine.library.extension

import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import org.reflections.Reflections

val reflections = Reflections("kr.cosine.library.command")

fun Plugin.sync(block: () -> Unit): BukkitTask {
    return server.scheduler.runTask(this, block)
}

fun Plugin.async(block: () -> Unit): BukkitTask {
    return server.scheduler.runTaskAsynchronously(this, block)
}

fun Plugin.later(delay: Long = 1L, async: Boolean = false, block: () -> Unit = {}): BukkitTask {
    return server.scheduler.run {
        if (async) {
            runTaskLaterAsynchronously(this@later, block, delay)
        } else {
            runTaskLater(this@later, block, delay)
        }
    }
}