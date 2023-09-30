package kr.cosine.library.kommand.argument

import kr.cosine.library.kommand.KommandExecutor

internal class ArgumentRegistry {

    private val argumentMap = mutableMapOf<String, KommandExecutor.CommandArgument>()

    fun get(argument: String): KommandExecutor.CommandArgument? = argumentMap[argument]

    fun set(argument: String, commandArgument: KommandExecutor.CommandArgument) {
        argumentMap[argument] = commandArgument
    }

    fun getValues(): List<KommandExecutor.CommandArgument> = argumentMap.values.toList()
}