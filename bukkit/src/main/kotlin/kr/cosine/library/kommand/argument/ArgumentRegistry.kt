package kr.cosine.library.kommand.argument

import kotlin.reflect.KClassifier
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure

internal object ArgumentRegistry {

    private val argumentMap = mutableMapOf<KClassifier, ArgumentProvider<*>>()

    fun getArgument(classifier: KClassifier): ArgumentProvider<*>? {
        return argumentMap[classifier]
    }

    fun registerArgument(argumentProvider: ArgumentProvider<*>) {
        val classifier = argumentProvider::class.supertypes.first {
            it.isSubtypeOf(ArgumentProvider::class.starProjectedType)
        }.arguments.first().type!!.jvmErasure
        println("[ArgumentRegistry] ${classifier.simpleName}")
        argumentMap[classifier] = argumentProvider
    }
}