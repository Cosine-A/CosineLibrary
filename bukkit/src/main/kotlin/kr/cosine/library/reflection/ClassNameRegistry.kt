package kr.cosine.library.reflection

class ClassNameRegistry {

    private val registeredClass = mutableListOf<String>()

    fun contains(name: String): Boolean = registeredClass.contains(name)

    fun add(name: String) {
        registeredClass.add(name)
    }

    fun getAll(): List<String> = registeredClass
}