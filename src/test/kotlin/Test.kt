import org.junit.jupiter.api.Test
import kotlin.reflect.KClass

class Test {

    open class TestException(error: String) : Exception(error)

    class FailException : TestException("fail")

    lateinit var clazz: KClass<out Exception>

    @Test
    fun class_test() {
        clazz = TestException::class

        println(clazz.simpleName)
    }
}