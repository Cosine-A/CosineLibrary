package kr.cosine.library.reflection

internal object ClassNameRegistry {

    private val registeredClass = mutableListOf<String>()

    fun contains(name: String): Boolean = registeredClass.contains(name)

    fun add(name: String) {
        registeredClass.add(name)
    }

    fun getAll(): List<String> = registeredClass

    fun clear() {
        registeredClass.clear()
    }
}