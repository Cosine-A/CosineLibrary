package kr.cosine.library.config.extension

import kr.cosine.library.config.YamlConfiguration
import kr.cosine.library.config.impl.YamlConfigurationImpl
import java.io.File

val File.yml: YamlConfiguration get() = YamlConfigurationImpl().also { it.load(this) }