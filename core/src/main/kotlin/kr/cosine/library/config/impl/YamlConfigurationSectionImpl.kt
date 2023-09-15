package kr.cosine.library.config.impl

import kr.cosine.library.config.YamlConfigurationSection
import org.spongepowered.configurate.ConfigurationNode

class YamlConfigurationSectionImpl(
    val rootNode: ConfigurationNode
) : YamlConfigurationSection {

    override fun getSection(path: String): YamlConfigurationSection? {
        val node = getNode(path)
        return if (node.virtual()) null else YamlConfigurationSectionImpl(node)
    }

    override fun getKeys(): List<String> {
        return rootNode.childrenMap().keys.map(Any::toString)
    }

    override fun getString(path: String): String? = getNode(path).string

    override fun getInt(path: String): Int = getNode(path).int

    override fun getLong(path: String): Long = getNode(path).long

    override fun getFloat(path: String): Float = getNode(path).float

    override fun getDouble(path: String): Double = getNode(path).double

    override fun getBoolean(path: String): Boolean = getNode(path).boolean

    override fun <T> getList(path: String, clazz: Class<T>): List<T> {
        val node = getNode(path)
        return if (!node.isList) {
            emptyList()
        } else {
            node.getList(clazz) ?: emptyList()
        }
    }

    override fun getStringList(path: String): List<String> = getList(path, String::class.java)

    override fun getIntegerList(path: String): List<Int> = getList(path, Int::class.javaObjectType)

    override fun getLongList(path: String): List<Long> = getList(path, Long::class.java)

    override fun getFloatList(path: String): List<Float> = getList(path, Float::class.java)

    override fun getDoubleList(path: String): List<Double> = getList(path, Double::class.java)

    override fun set(path: String, value: Any?) {
        getNode(path).set(value)
    }

    private fun getNode(path: String): ConfigurationNode {
        val keys = path.split(".")
        var currentNode = rootNode
        keys.forEach { key ->
            currentNode = currentNode.node(key)
        }
        return currentNode
    }
}