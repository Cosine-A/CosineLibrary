package kr.cosine.library.kommand.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Kommand(
    val command: String,
    val exception: KClass<Exception> = Exception::class
)
