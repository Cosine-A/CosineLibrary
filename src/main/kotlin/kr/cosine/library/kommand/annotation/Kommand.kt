package kr.cosine.library.kommand.annotation

import kotlin.reflect.KClass
import java.lang.Exception

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Kommand(
    val command: String,
    val exception: KClass<out Exception> = Exception::class
)
