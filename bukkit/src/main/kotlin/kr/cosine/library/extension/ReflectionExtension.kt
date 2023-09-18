package kr.cosine.library.extension

import kotlin.reflect.KClass

private val regex by lazy { Regex("(\\w+)\\.(\\w+)") }

val KClass<*>.packageName get() = regex.find(java.name)?.groups?.get(0)?.value