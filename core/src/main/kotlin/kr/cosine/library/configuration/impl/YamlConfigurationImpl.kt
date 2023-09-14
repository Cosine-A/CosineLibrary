package kr.cosine.library.configuration.impl

import kr.cosine.library.configuration.YamlConfiguration
import kr.cosine.library.configuration.YamlConfigurationSection
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.File

class YamlConfigurationImpl : YamlConfiguration {

    private var cachedFile: File? = null
    private var loader: YamlConfigurationLoader? = null
    private var rootSection: YamlConfigurationSectionImpl? = null

    override fun load(file: File) {
        val newLoader = YamlConfigurationLoader.builder().file(file).build()
        loader = newLoader
        rootSection = YamlConfigurationSectionImpl(newLoader.load())
        if (cachedFile == null) cachedFile = file
    }

    override fun save(file: File) {
        rootSection?.let {
            YamlConfigurationLoader.builder().nodeStyle(NodeStyle.BLOCK).file(file).build().save(it.rootNode)
        }
    }

    override fun reload() {
        loader = null
        rootSection = null
        cachedFile?.apply(::load)
    }

    override fun getSection(path: String): YamlConfigurationSection? = rootSection?.getSection(path)

    override fun getKeys(): List<String> = rootSection?.getKeys() ?: emptyList()

    override fun getString(path: String): String? = rootSection?.getString(path)

    override fun getInt(path: String): Int = rootSection?.getInt(path) ?: 0

    override fun getLong(path: String): Long = rootSection?.getLong(path) ?: 0L

    override fun getFloat(path: String): Float = rootSection?.getFloat(path) ?: 0f

    override fun getDouble(path: String): Double = rootSection?.getDouble(path) ?: 0.0

    override fun getBoolean(path: String): Boolean = rootSection?.getBoolean(path) ?: false

    override fun <T> getList(path: String, clazz: Class<T>): List<T> = rootSection?.getList(path, clazz) ?: emptyList()

    override fun getStringList(path: String): List<String> = rootSection?.getStringList(path) ?: emptyList()

    override fun getIntegerList(path: String): List<Int> = rootSection?.getIntegerList(path) ?: emptyList()

    override fun getLongList(path: String): List<Long> = rootSection?.getLongList(path) ?: emptyList()

    override fun getFloatList(path: String): List<Float> = rootSection?.getFloatList(path) ?: emptyList()

    override fun getDoubleList(path: String): List<Double> = rootSection?.getDoubleList(path) ?: emptyList()

    override fun set(path: String, value: Any?) {
        rootSection?.set(path, value)
    }
}