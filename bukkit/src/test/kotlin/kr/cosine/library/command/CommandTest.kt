package kr.cosine.library.command

import org.junit.jupiter.api.Test
import kotlin.reflect.full.functions
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure

class CommandTest {

    fun vararg(vararg amount: Double) {

    }

    fun amount(amount: Int) {}

    fun amount2(text: String, amount: Int) {}

    fun text(text: String) {}

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