package com.wzjing.face

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.yield
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.doAsync
import org.junit.Test

import org.junit.Assert.*
import java.math.BigDecimal

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
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

    @Volatile var number: BigDecimal = BigDecimal(0.0.toString())
    @Test
    fun syncTest() = runBlocking(CommonPool) {
        val computeA = bg {
            for (i in 1..20) {
                calculate("A: $i")
            }
        }
        val computeB = bg {
            for (i in 1..20) {
                calculate("B: $i")
            }
        }
//        computeA.join()
//        computeB.join()
        delay(5000)
    }

    fun calculate(name: String) {
        synchronized(number) {
            for (j in 1..10000)
                number = number.add(BigDecimal(0.0001.toString()))
            println("$name: $number")
        }
    }
}