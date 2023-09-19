package kr.cosine.library.kommand.argument

import kotlin.reflect.KClassifier
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure

internal class ArgumentProviderRegistry {

    private val argumentProviderMap = mutableMapOf<KClassifier, ArgumentProvider<*>>()

    fun get(classifier: KClassifier): ArgumentProvider<*>? {
        return argumentProviderMap[classifier]
    }

    fun register(argumentProvider: ArgumentProvider<*>) {
        val classifier = argumentProvider::class.supertypes.first {
            it.isSubtypeOf(ArgumentProvider::class.starProjectedType)
        }.arguments.first().type!!.jvmErasure
        println("[ArgumentRegistry] ${classifier.simpleName}")
        argumentProviderMap[classifier] = argumentProvider
    }
}