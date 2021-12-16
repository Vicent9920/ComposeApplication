package com.vicent.composeapplication.model

/**
 * <pre>
 *     author: Vincent
 *     date  :  2021/11/29 19:07
 *     desc  :  数独单元格实体
 * </pre>
 * @param x 水平方向
 * @param y 垂直方向
 * @property code 数独声明值
 * @property value 用户填写值
 * @property state 响应状态
 * @property onlyRead 只读
 */
data class GridUnit(val x:Int,val y:Int) {
    var code:Int = 0
    var value:Int = 0
    var state:ItemStatus = NormalTarget
    var onlyRead = true

    fun clone():GridUnit{
        return GridUnit(x,y).also {
            it.state = state
            it.onlyRead = onlyRead
            it.value = value
            it.code = code
        }
    }
}

/**
 * 单元格状态
 */
sealed class ItemStatus

/**
 * 弱关联
 */
object WeakAssociation : ItemStatus()

/**
 * 弱目标
 */
object WeakTarget:ItemStatus()

/**
 * 非空目标
 */
object NotEmptyTarget:ItemStatus()

/**
 * 同值关联区
 */
object TheSameAssociation:ItemStatus()

/**
 * 错误目标区
 */
object ErrorTarget:ItemStatus()

/**
 * 错误关联区
 */
object ErrorAssociation : ItemStatus()

/**
 * 默认状态（）
 */
object NormalTarget:ItemStatus()
