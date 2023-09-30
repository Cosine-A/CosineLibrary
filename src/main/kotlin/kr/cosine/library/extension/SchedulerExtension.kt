package kr.cosine.library.extension

import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask

fun Plugin.sync(actionFunction: () -> Unit): BukkitTask {
    return server.scheduler.runTask(this, actionFunction)
}

fun Plugin.async(actionFunction: () -> Unit): BukkitTask {
    return server.scheduler.runTaskAsynchronously(this, actionFunction)
}

fun Plugin.later(delay: Long = 1L, async: Boolean = false, actionFunction: () -> Unit = {}): BukkitTask {
    return server.scheduler.run {
        if (async) {
            runTaskLaterAsynchronously(this@later, actionFunction, delay)
        } else {
            runTaskLater(this@later, actionFunction, delay)
        }
    }
}