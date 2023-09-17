package kr.cosine.library.kommand.language

import kr.cosine.library.config.YamlConfiguration
import kr.cosine.library.kommand.page.PageHelper

class Language(
    private val config: YamlConfiguration
) {

    fun getMessage(path: String): String {
        return config.getString(path)
    }

    fun getDescription(root: String, argument: String): String {
        return config.getString("command.$root.$argument.description")
    }

    fun getArguments(root: String, argument: String): String {
        return config.getString("command.$root.$argument.arguments")
    }

    fun getGlobalErrorMessage(path: String): String {
        return config.getString("global-error.$path")
    }

    fun getArgumentErrorMessage(root: String, argument: String, path: String): String {
        return config.getString(getPath(root, argument, "error.$path"))
    }

    private fun getPath(root: String, argument: String, path: String): String {
        return "command.$root.$argument.$path"
    }

    fun getPageHelper(root: String): PageHelper {
        return PageHelper(config).also { it.load(root) }
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