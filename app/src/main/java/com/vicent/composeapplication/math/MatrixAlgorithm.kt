package com.vicent.composeapplication.math

import java.lang.StringBuilder
import kotlin.random.Random

/**
 * <pre>
 *     author: Vincent
 *     date  :  2021/11/29 11:41
 *     desc  : 数独生成算法
 * </pre>
 */
object MatrixAlgorithm {

    fun create():String{
        val data = arrayOfNulls<ByteArray>(9)
        data[4] = createNineRandomArray()
        val horizontalArray = createHorizontalArray(data[4]!!)
        // 减少一处随机
        data[3] = horizontalArray.first
        data[5] = horizontalArray.second
        val v1 = createVerticalArray(data[4]!!)
        data[1] = v1.first
        data[7] = v1.second
        val v2 = createVerticalArray(horizontalArray.first)
        data[0] = v2.first
        data[6] = v2.second
        val v3 = createVerticalArray(horizontalArray.second)
        data[2] = v3.first
        data[8] = v3.second
        return twoDimensionalConversionString(data)
    }


    /**
     * 水平置换
     * 将一个目标小宫格转换为两个满足数独规则的小宫格
     */
    private fun createHorizontalArray(source:ByteArray):Pair<ByteArray,ByteArray>{
        val horizontalRandom = true
        val first:ByteArray
        val second:ByteArray
        if(horizontalRandom){
            first = zipHorizontalArray(horizontalTwo(source),horizontalThree(source),horizontalOne(source))
            second = zipHorizontalArray(horizontalThree(source),horizontalOne(source),horizontalTwo(source))
        }else{
            first = zipHorizontalArray(horizontalThree(source),horizontalOne(source),horizontalTwo(source))
            second = zipHorizontalArray(horizontalTwo(source),horizontalThree(source),horizontalOne(source))
        }
        return Pair(first,second)
    }

    /**
     * 垂直置换
     * 将一个目标小宫格转换为两个满足数独规则的小宫格
     */
    private fun createVerticalArray(source:ByteArray):Pair<ByteArray,ByteArray>{
        val horizontalRandom = true
        val first:ByteArray
        val second:ByteArray
        if(horizontalRandom){
            first = zipVerticalArray(verticalTwo(source),verticalThree(source),verticalOne(source))
            second = zipVerticalArray(verticalThree(source),verticalOne(source),verticalTwo(source))
        }else{
            first = zipVerticalArray(verticalThree(source),verticalOne(source),verticalTwo(source))
            second = zipVerticalArray(verticalTwo(source),verticalThree(source),verticalOne(source))
        }
        return Pair(first,second)
    }


    /**
     * 生成1-9不重复的随机数
     */
    private fun createNineRandomArray():ByteArray{
        val result = byteArrayOf(0,0,0,0,0,0,0,0,0)
        for (i in 0 until 9){
            while (true){
                val code = Random.nextInt(1,10).toByte()
                if(!result.contains(code)){
                    result[i] = code
                    break
                }
            }
        }
        return result
    }

    /**
     * 摘取小宫格第一排元素
     */
    private fun horizontalOne(array:ByteArray):ByteArray{
        return byteArrayOf(array[0],array[1],array[2])
    }

    /**
     * 摘取小宫格第二排元素
     */
    private fun horizontalTwo(array:ByteArray):ByteArray{
        return byteArrayOf(array[3],array[4],array[5])
    }
    /**
     * 摘取小宫格第三排元素
     */
    private fun horizontalThree(array:ByteArray):ByteArray{
        return byteArrayOf(array[6],array[7],array[8])
    }
    /**
     * 摘取小宫格第一列元素
     */
    private fun verticalOne(array:ByteArray):ByteArray{
        return byteArrayOf(array[0],array[3],array[6])
    }
    /**
     * 摘取小宫格第二列元素
     */
    private fun verticalTwo(array:ByteArray):ByteArray{
        return byteArrayOf(array[1],array[4],array[7])
    }
    /**
     * 摘取小宫格第三列元素
     */
    private fun verticalThree(array:ByteArray):ByteArray{
        return byteArrayOf(array[2],array[5],array[8])
    }

    /**
     * 将三排元素合并成一个小宫格
     */
    private fun zipHorizontalArray(arrayA:ByteArray,arrayB:ByteArray,arrayC:ByteArray):ByteArray{
        return byteArrayOf(arrayA[0],arrayA[1],arrayA[2],arrayB[0],arrayB[1],arrayB[2],arrayC[0],arrayC[1],arrayC[2])
    }

    /**
     * 将三列元素合并成一个小宫格
     */
    private fun zipVerticalArray(arrayA:ByteArray,arrayB:ByteArray,arrayC:ByteArray):ByteArray{
        return byteArrayOf(arrayA[0],arrayB[0],arrayC[0],arrayA[1],arrayB[1],arrayC[1],arrayA[2],arrayB[2],arrayC[2])
    }

    /**
     * 二维数组转字符串
     */
    private fun twoDimensionalConversionString(source:Array<ByteArray?>):String{
        val result = StringBuilder()
        for (i in 0 until 9){
            when(i){
                0 -> {
                    result.append(source[0]?.get(0))
                    result.append(source[0]?.get(1))
                    result.append(source[0]?.get(2))
                    result.append(source[1]?.get(0))
                    result.append(source[1]?.get(1))
                    result.append(source[1]?.get(2))
                    result.append(source[2]?.get(0))
                    result.append(source[2]?.get(1))
                    result.append(source[2]?.get(2))

                }
                1 -> {
                    result.append(source[0]?.get(3))
                    result.append(source[0]?.get(4))
                    result.append(source[0]?.get(5))
                    result.append(source[1]?.get(3))
                    result.append(source[1]?.get(4))
                    result.append(source[1]?.get(5))
                    result.append(source[2]?.get(3))
                    result.append(source[2]?.get(4))
                    result.append(source[2]?.get(5))
                }
                2 -> {
                    result.append(source[0]?.get(6))
                    result.append(source[0]?.get(7))
                    result.append(source[0]?.get(8))
                    result.append(source[1]?.get(6))
                    result.append(source[1]?.get(7))
                    result.append(source[1]?.get(8))
                    result.append(source[2]?.get(6))
                    result.append(source[2]?.get(7))
                    result.append(source[2]?.get(8))
                }
                3 -> {
                    result.append(source[3]?.get(0))
                    result.append(source[3]?.get(1))
                    result.append(source[3]?.get(2))
                    result.append(source[4]?.get(0))
                    result.append(source[4]?.get(1))
                    result.append(source[4]?.get(2))
                    result.append(source[5]?.get(0))
                    result.append(source[5]?.get(1))
                    result.append(source[5]?.get(2))
                }
                4 -> {
                    result.append(source[3]?.get(3))
                    result.append(source[3]?.get(4))
                    result.append(source[3]?.get(5))
                    result.append(source[4]?.get(3))
                    result.append(source[4]?.get(4))
                    result.append(source[4]?.get(5))
                    result.append(source[5]?.get(3))
                    result.append(source[5]?.get(4))
                    result.append(source[5]?.get(5))
                }
                5 -> {
                    result.append(source[3]?.get(6))
                    result.append(source[3]?.get(7))
                    result.append(source[3]?.get(8))
                    result.append(source[4]?.get(6))
                    result.append(source[4]?.get(7))
                    result.append(source[4]?.get(8))
                    result.append(source[5]?.get(6))
                    result.append(source[5]?.get(7))
                    result.append(source[5]?.get(8))
                }
                6 -> {
                    result.append(source[6]?.get(0))
                    result.append(source[6]?.get(1))
                    result.append(source[6]?.get(2))
                    result.append(source[7]?.get(0))
                    result.append(source[7]?.get(1))
                    result.append(source[7]?.get(2))
                    result.append(source[8]?.get(0))
                    result.append(source[8]?.get(1))
                    result.append(source[8]?.get(2))
                }
                7 -> {
                    result.append(source[6]?.get(3))
                    result.append(source[6]?.get(4))
                    result.append(source[6]?.get(5))
                    result.append(source[7]?.get(3))
                    result.append(source[7]?.get(4))
                    result.append(source[7]?.get(5))
                    result.append(source[8]?.get(3))
                    result.append(source[8]?.get(4))
                    result.append(source[8]?.get(5))
                }
                8 -> {
                    result.append(source[6]?.get(6))
                    result.append(source[6]?.get(7))
                    result.append(source[6]?.get(8))
                    result.append(source[7]?.get(6))
                    result.append(source[7]?.get(7))
                    result.append(source[7]?.get(8))
                    result.append(source[8]?.get(6))
                    result.append(source[8]?.get(7))
                    result.append(source[8]?.get(8))
                }
            }
        }
        return result.toString()
    }
}