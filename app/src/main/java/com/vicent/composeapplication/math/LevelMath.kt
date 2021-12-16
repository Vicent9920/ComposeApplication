package com.vicent.composeapplication.math

import com.vicent.composeapplication.model.GridUnit
import kotlin.random.Random

/**
 * <pre>
 *     author: Vincent
 *     date  :  2021/11/30 19:03
 *     desc  :  级别算法
 * </pre>
 */
object LevelMath {

    private val levelList = mutableListOf(16,16,17,17,18,18,19,19,20,20,21,21,22,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38)

    fun getLevelName(name:Int):Int{
        return levelList.size+1-name
    }

    /**
     * 设置关卡数据，并返回可输入单元格数量
     */
    fun setLevelData(level:Int, source:MutableList<GridUnit>):Int{
        // 确定每行镂空数量
        val emptyBlank = createNineRandomArray(81-levelList[level-1],81)
        // 将每行指定元素镂空
        emptyBlank.forEach {
            source[it].value = 0
            source[it].onlyRead = false
        }
        return emptyBlank.size
    }

    /**
     * 生成指定大小且内容为1-9不重复的随机数组
     * @param size 数组大小
     */
    private fun createNineRandomArray(size:Int,max: Int):IntArray{
        val result = IntArray(size)
        for (i in 0 until size){
            while (true){
                val code = Random.nextInt(1,max)
                if(!result.contains(code)){
                    result[i] = code
                    break
                }
            }
        }
        return result
    }
}