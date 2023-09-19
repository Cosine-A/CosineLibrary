package kr.cosine.library.kommand.language

import kr.cosine.library.config.extension.yml
import kr.cosine.library.plugin.BukkitPlugin
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File

class LanguageRegistry(
    private val languageFolder: File
) {

    private val languageMap = mutableMapOf<String, Language>()

    fun load(plugin: BukkitPlugin) {
        val languageFiles = languageFolder.listFiles()?.filter { it.name.endsWith(".yml") }
        if (languageFiles?.any { it.name == "en_us.yml" } == false) {
            plugin.createResourceFile("language/en_us.yml")
        }
        languageFiles?.forEach { file ->
            val name = file.name.removeSuffix(".yml")
            languageMap[name] = Language(file.yml.also { it.reload() })
        }
    }

    fun get(sender: CommandSender): Language {
        return (sender as? Player)?.let { languageMap[sender.locale] } ?: languageMap["en_us"]!!
    }
}