package kr.cosine.library.extension

import org.bukkit.ChatColor

fun String.applyColor(): String {
    return ChatColor.translateAlternateColorCodes('&', this)
}

fun List<String>.applyColor(): MutableList<String> {
    return map(String::applyColor).toMutableList()
}

fun String.removeColor(): String {
    return ChatColor.stripColor(this)!!
}

fun List<String>.removeColor(): MutableList<String> {
    return map(String::removeColor).toMutableList()
}