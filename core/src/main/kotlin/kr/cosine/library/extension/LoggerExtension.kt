package kr.cosine.library.extension

import java.util.logging.Logger

enum class LogColor {
    NONE,
    RED,
    GREEN,
    YELLOW,
    BLUE,
}

fun Logger.info(message: String, logColor: LogColor = LogColor.NONE) {
    val finalMessage = when (logColor) {
        LogColor.RED -> "\u001B[31m$message\u001B[0m"
        LogColor.GREEN -> "\u001B[32m$message\u001B[0m"
        LogColor.YELLOW -> "\u001B[33m$message\u001B[0m"
        LogColor.BLUE -> "\u001B[34m$message\u001B[0m"
        else -> message
    }
    info(finalMessage)
}