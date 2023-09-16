package kr.cosine

import org.junit.jupiter.api.Test

class PackageTest {

    @Test
    fun package_test() {
        println(this::class.java.`package`.name.substringBeforeLast('.'))
    }
}