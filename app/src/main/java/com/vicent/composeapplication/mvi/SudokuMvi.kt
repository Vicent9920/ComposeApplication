package com.vicent.composeapplication.mvi

import com.vicent.composeapplication.model.GridUnit

/**
 * <pre>
 *     author: Vincent
 *     date  :  2021/12/1 9:47
 *     desc  : MVI 架构下的状态等交互数据声明
 * </pre>
 */

sealed class SudokuState:UiState
// 初始化
object SudokuLoading : SudokuState()
// 游戏主页
object SudokuHome : SudokuState()
// 开始
object SudokuPlay : SudokuState()


sealed class SudokuEvent:UiEvent
// 点击数独
class SudokuClickEvent(val position:Int):SudokuEvent()
// 点击删除
class DeleteClickEvent(val position: Int):SudokuEvent()
// 输入数字
class InputValue(val position: Int,val value:Int):SudokuEvent()
// 点击撤回
object BackClickEvent:SudokuEvent()
// 页面是否可见
class InLifeEvent(val isVisible:Boolean):SudokuEvent()
// 错误弹窗点击事件
class ErrorDialogClick(val back:Boolean):SudokuEvent()
// 过关弹窗点击事件
class DoneDialogClick(val back:Boolean):SudokuEvent()




sealed class SudokuEffect:UiEffect
// 页面渲染数据
class SudokuData(val data:MutableList<GridUnit>):SudokuEffect()
// 删除状态
class DeleteEnable(val enable:Boolean,val position: Int):SudokuEffect()
// 输入状态
class InputEnable(val enable:Boolean,val position: Int):SudokuEffect()

