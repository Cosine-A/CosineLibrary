package kr.cosine.library.extension

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

fun File.createFolder(): File {
    if (!exists()) {
        mkdirs()
    }
    return this
}

val File.yml: YamlConfiguration get() = YamlConfiguration.loadConfiguration(this)