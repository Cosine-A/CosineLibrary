package kr.cosine.library.reflection

import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSuperclassOf

class ClassRegistry {

    private val classes = mutableSetOf<KClass<*>>()

    /*fun <T : Annotation> getAnnotatedClasses(clazz: KClass<T>): List<ClassInfo> {
        return classes.filter { it.hasAnnotation(clazz.java.name) }
    }

    fun <T : Any> getInheritedClasses(clazz: KClass<T>): List<ClassInfo> {
        return classes.filter {
            val name = clazz.java.name
            it.extendsSuperclass(name) || it.implementsInterface(name)
        }
    }*/

    fun <T : Any> getInheritedClasses(clazz: KClass<T>): List<KClass<*>> {
        return classes.filter { it.isSubclassOf(clazz) && it != clazz }
    }

    fun add(clazz: KClass<*>) {
        classes.add(clazz)
    }

    fun getAll(): Set<KClass<*>> = classes
}