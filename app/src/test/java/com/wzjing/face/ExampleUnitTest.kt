package com.wzjing.face

import kotlinx.coroutines.experimental.*
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.doAsync
import org.junit.Test

import org.junit.Assert.*
import java.math.BigDecimal

class ExampleUnitTest {

    @Test
    fun array_test() {
        val array = arrayListOf<Int>(1, 2, 3, 4)
        for (i in array.indices) {
            print("First is ${if (array.size > 0) array[0].toString() else "null"}\n")
            array.removeAt(0)
        }
    }

    @Test
    fun bytes_convert() {
        print("0xFC | 3 = ${Integer.toHexString(0xFC or 3)}");
    }

    @Test
    fun list_sort() {
        val data = hashMapOf<Int, String>(
                1 to "a",
                26 to "z",
                25 to "y",
                20 to "q",
                3 to "c",
                2 to "b",
                4 to "d")
        val keyList = data.keys.toList()
        val key = keyList.find {
            val index = keyList.indexOf(it)
            Math.abs(it - 21) <= Math.abs(keyList[if (index + 1 > keyList.size - 1) index else index + 1] - 21) &&
                    Math.abs(it - 21) <= Math.abs(keyList[if (index - 1 < 0) index else index - 1] - 21)
        }
        print("Result [$key, ${data.get(key)}]")
    }

    @Test
    fun thread() = runBlocking(CommonPool) {
        val start = System.currentTimeMillis()
        var n = 2
        val job = bg {
            for (i in 1..8) {
                n += n
                println("${System.currentTimeMillis() - start}ms Current n: $n")
            }

            println("${System.currentTimeMillis() - start}ms job: n is $n")
        }
        println("${System.currentTimeMillis() - start}ms Outer: n is $n")
        job.join()
    }

    val number = IntNumber(0)
    @Test
    fun syncTest() = runBlocking(CommonPool) {
        val computeA = launch(newSingleThreadContext("computeA")) {
            for (i in 1..20) {
                calculate("A: $i")
//                delay(10)
                yield()
            }
        }
        val computeB = launch(newSingleThreadContext("computeB")) {
            for (i in 1..20) {
                calculate("B: $i")
//                delay(10)
                yield()
            }
        }
        computeA.join()
        computeB.join()
    }

    suspend fun calculate(name: String) {
        synchronized(number) {
            for (j in 1..100000000)
                number.set(number.value()+1)
            println("%-10s: ${number.value()}".format(name))

        }
    }

    inner class IntNumber(value: Long) {
        private var number: Long = 0

        init {
            number = value
        }

        fun set(value: Long) {
            number = value
        }

        fun value(): Long {
            return number
        }
    }
}