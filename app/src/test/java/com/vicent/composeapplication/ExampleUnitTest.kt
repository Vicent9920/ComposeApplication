package com.vicent.composeapplication

import com.vicent.composeapplication.math.LevelMath
import org.junit.Test

import org.junit.Assert.*
import kotlin.random.Random

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

//        check()


        for (i in 1 until 31){
            println(getLevelName(i))
        }

    }

    fun getLevelName(name:Int):Int{
        return 30+1-name
    }

    private fun check():Boolean{
        val max = 43
        val array = IntArray(9)
        for (i in 0..7){
            array[i] = Random.nextInt(0,max)
        }
        array.sort()
        val source = array.toMutableList()
        while (true){
            if(source.size == 1){
                if(max-source[0]>9){
                    return check()
                }
                array.forEach { println(it) }
                return true
            }
            val end = source.removeLast()
            if(end-source.last()>9){
                return check()
            }
        }
    }


}