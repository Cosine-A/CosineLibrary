package kr.cosine.main

import kr.cosine.library.database.annotation.Table
import org.junit.jupiter.api.Test
import org.reflections.Reflections

@Table
class AnnotationTest {

    @Test
    fun test() {
        val reflections = Reflections()
        val annotatedClasses = reflections.getTypesAnnotatedWith(Table::class.java).map { it.kotlin }
        for (clazz in annotatedClasses) {
            println(clazz.simpleName)
        }
    }
}