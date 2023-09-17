package kr.cosine.library.kommand.language

import kr.cosine.library.config.YamlConfiguration
import kr.cosine.library.config.extension.yml
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File

class LanguageRegistry(
    private val languageFolder: File
) {

    private val languageFileMap = mutableMapOf<String, Language>()

    fun load() {
        languageFolder.listFiles()?.forEach { file ->
            val name = file.name.removeSuffix(".yml")
            languageFileMap[name] = Language(file.yml)
        }
    }

    fun getLanguage(sender: CommandSender): Language {
        return (sender as? Player)?.let { languageFileMap[sender.locale] } ?: languageFileMap["en_us"]!!
    }
}