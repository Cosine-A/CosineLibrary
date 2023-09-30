package kr.cosine.library.kommand.language

import kr.cosine.library.extension.applyColor
import kr.cosine.library.kommand.page.PageHelper
import org.bukkit.configuration.file.YamlConfiguration

class Language(
    private val config: YamlConfiguration
) {

    fun getMessage(path: String): String {
        return getString(path).applyColor()
    }

    fun getDescription(root: String, argument: String): String {
        return getString(getPath(root, argument, "description")).applyColor()
    }

    fun getArguments(root: String, argument: String): String {
        return getString(getPath(root, argument, "arguments")).applyColor()
    }

    fun getErrorMessage(path: String): String {
        return getString("error.$path").applyColor()
    }

    fun getArgumentErrorMessage(root: String, argument: String, path: String): String {
        return getString(getPath(root, argument, "error.$path")).applyColor()
    }

    private fun getPath(root: String, argument: String, path: String): String {
        return "command.$root.$argument.$path"
    }

    fun getPageHelper(): PageHelper {
        return PageHelper(config).also { it.load() }
    }

    private fun getString(path: String): String {
        return config.getString(path) ?: throw NullPointerException("$path is not exists in language.")
    }

    /*
    // [플레이어] [코드] -> [[플레이어], [코드]]
    fun findArguments(root: String, argument: String): List<String> {
        return config.getString(getPath(root, argument, "arguments")).split(" ")
    }

    fun findArgument(arguments: List<String>, index: Int): String {
        return arguments[index].run { substring(1, length - 1) }
    }
    */
}