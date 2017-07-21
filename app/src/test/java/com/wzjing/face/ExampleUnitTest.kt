package com.wzjing.face

import org.jetbrains.anko.custom.async
import org.jetbrains.anko.doAsync
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun array_test() {
        val array = arrayListOf<Int>(1, 2, 3, 4)
        for (i in array.indices) {
            print("First is ${if (array.size > 0) array[0] else "null"}\n")
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
    fun thread() {

        println("start")

        backgroundTask {
            blockMethod()
        }
        println("end")
    }

    fun<T> backgroundTask(work: suspend () -> T){
    }

    suspend fun blockMethod(){
        for (i in 0..10 step 2) {
            println("number $i")
        }
    }
}
