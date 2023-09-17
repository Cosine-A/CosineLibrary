package kr.cosine.library.kommand.argument

object ArgumentRegistry {

    private val argumentMap = mutableMapOf<String, ArgumentProvider<*>>()

    fun getArgument(key: String): ArgumentProvider<*>? {
        return argumentMap[key]
    }

    fun registerArgument(argumentProvider: ArgumentProvider<*>) {
        val name = argumentProvider.genericName
        println("[ArgumentRegistry] $name")
        argumentMap[name] = argumentProvider
    }
}