package kr.cosine.library.test

import com.google.common.reflect.ClassPath
import kr.cosine.library.kommand.annotation.Argument
import org.junit.jupiter.api.Test
import org.reflections.Reflections
import java.io.IOException
import kotlin.reflect.full.functions
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure

class CommandTest {

    @Argument
    class A() {}

    @Argument
    class B() {}

    @Argument
    class C() {}

    @Argument
    class D() {}

    fun vararg(vararg amount: Double) {

    }

    fun amount(amount: Int) {}

    fun amount2(text: String, amount: Int) {}

    fun text(text: String) {}

    @Test
    fun class_test() {
        findClasses("kr.cosine.library.test").forEach {
            println("class: ${it.simpleName}")
        }
    }

    @Throws(IOException::class)
    fun findClasses(packageName: String): List<Class<*>> {
        return ClassPath.from(ClassLoader.getSystemClassLoader())
            .allClasses.filter { clazz ->
                clazz.packageName.contains(packageName)
            }.map { clazz -> clazz.load() }
    }

    @Test
    fun annotation_test() {
        val reflections = Reflections("kr.cosine.library.command")
        reflections.getTypesAnnotatedWith(Argument::class.java).forEach {
            println("class: ${it.simpleName}")
        }
    }

    @Test
    fun function_test() {
        CommandTest::class.functions.forEach { function ->
            val type = function.valueParameters.firstOrNull()?.type ?: return@forEach
            val text = if (type.jvmErasure == Int::class) {
                "Int"
            } else {
                "None"
            }
            println("[$text] ${function.name} : $type")
        }
    }
}