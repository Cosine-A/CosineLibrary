package kr.cosine.library.test

import kr.cosine.library.kommand.annotation.Argument
import org.junit.jupiter.api.Test
import org.reflections.Reflections
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
    fun package_test() {
        println("package: ${"org.jetbrains".getPackage()}")
    }

    private fun String.getPackage(): String {
        val regex = Regex("(\\w+)\\.(\\w+)")
        val matchResult = regex.find(this)
        val group = matchResult?.groups!!
        return group[0]!!.value
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