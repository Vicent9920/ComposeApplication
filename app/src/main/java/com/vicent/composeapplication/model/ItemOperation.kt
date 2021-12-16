package com.vicent.composeapplication.model

/**
 * <pre>
 *     author: Vincent
 *     date  :  2021/12/9 15:00
 *     desc  : 项目操作
 * </pre>
 */
sealed class ItemOperation(val item:GridUnit)
class DeleteItem(val position:Int,item: GridUnit):ItemOperation(item)
class InputItem(val position:Int,item: GridUnit):ItemOperation(item)