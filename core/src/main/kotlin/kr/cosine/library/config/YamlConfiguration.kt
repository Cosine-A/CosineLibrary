package kr.cosine.library.config

import kr.cosine.library.config.impl.YamlConfigurationImpl
import java.io.File

interface YamlConfiguration : YamlConfigurationSection {

    companion object {
        fun loadConfiguration(file: File): YamlConfiguration {
            return YamlConfigurationImpl().also { it.load(file) }
        }
    }

    fun load(file: File)

    fun save(file: File)

    fun reload()
}