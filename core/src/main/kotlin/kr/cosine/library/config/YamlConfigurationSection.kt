package kr.cosine.library.config

interface YamlConfigurationSection {

    fun getSection(path: String): YamlConfigurationSection?

    fun getKeys(): List<String>

    fun getString(path: String): String?

    fun getInt(path: String): Int

    fun getLong(path: String): Long

    fun getFloat(path: String): Float

    fun getDouble(path: String): Double

    fun getBoolean(path: String): Boolean

    fun <T> getList(path: String, clazz: Class<T>): List<T>

    fun getStringList(path: String): List<String>

    fun getIntegerList(path: String): List<Int>

    fun getLongList(path: String): List<Long>

    fun getFloatList(path: String): List<Float>

    fun getDoubleList(path: String): List<Double>

    fun set(path: String, value: Any?)
}