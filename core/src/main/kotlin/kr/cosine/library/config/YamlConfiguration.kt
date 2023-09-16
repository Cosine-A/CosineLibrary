package kr.cosine.library.config

import java.io.File

interface YamlConfiguration : YamlConfigurationSection {

    fun load(file: File)

    fun save(file: File)

    fun reload()
}