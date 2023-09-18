package kr.cosine.library.reflection

import io.github.classgraph.ClassInfo
import kotlin.reflect.KClass

internal object ClassRegistry {

    private val classes = mutableSetOf<ClassInfo>()

    fun <T : Annotation> getAnnotatedClasses(clazz: KClass<T>): List<ClassInfo> {
        return classes.filter { it.hasAnnotation(clazz.java.name) }
    }

    fun <T : Any> getInheritedClasses(clazz: KClass<T>): List<ClassInfo> {
        return classes.filter {
            val name = clazz.java.name
            it.extendsSuperclass(name) || it.implementsInterface(name)
        }
    }

    fun add(clazz: ClassInfo) {
        classes.add(clazz)
    }

    fun clear() {
        classes.clear()
    }
}