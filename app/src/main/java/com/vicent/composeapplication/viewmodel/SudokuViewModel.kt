package com.vicent.composeapplication.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.vicent.composeapplication.MyApplication
import com.vicent.composeapplication.dao.AppDataBase
import com.vicent.composeapplication.dao.SudokuLevel
import com.vicent.composeapplication.math.LevelMath
import com.vicent.composeapplication.math.MatrixAlgorithm
import com.vicent.composeapplication.model.*
import com.vicent.composeapplication.mvi.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

/**
 * <pre>
 *     author: Vincent
 *     date  :  2021/11/29 12:01
 *     desc  :
 * </pre>
 */
@SuppressLint("SimpleDateFormat")
class SudokuViewModel : BaseViewModel<SudokuState, SudokuEvent, SudokuEffect>() {

    /******************* 私有成员变量 ************************/


    /**
     * 页面是否可见
     */
    private var mVisible = false

    /**
     * 错误次数
     */
    private var errorCount = 0

    /**
     * 可输入次数
     * 当输入正确后，可输入次数等于0则代表游戏结束，保存游戏相关数据
     */
    private var inputEnableCount = -1

    /**
     * 操作步骤
     */
    private val operationList = Stack<ItemOperation>()

    /**
     * 游戏时间
     */
    private var currentTime:Long = 0


    /*********************************** UiEffect 页面交互 *********************************/

    /**
     * ui页面数据
     * 需要页面返回，故而使用SudokuEvent
     */
    private val _pageData = MutableStateFlow(SudokuData(mutableListOf()))
    val pageData = _pageData.asStateFlow()

    /**
     * 删除按钮是否可用
     * 需要组装参数，故而使用SudokuEvent
     */
    private val _deleteEnable = MutableStateFlow(DeleteEnable(false,0))
    val deleteEnable = _deleteEnable.asStateFlow()

    /**
     * 页面是否可以输入
     * 需要组装参数，故而使用SudokuEvent
     */
    private val _inputEnable = MutableStateFlow(InputEnable(false,0))
    val inputEnable = _inputEnable.asStateFlow()

    /**
     * 是否可以撤回操作
     * <p 不需要页面返回也不需要组装参数，故而直接使用StateFlow，减少声明对象/>
     */
    private val _backEnable = MutableStateFlow(false)
    val backEnable = _backEnable.asStateFlow()

    /**
     * 显示时间
     * <p 不需要页面返回也不需要组装参数，故而直接使用StateFlow，减少声明对象/>
     */
    private val _time = MutableStateFlow("")
    val time = _time.asStateFlow()

    private val _title = MutableStateFlow("主页")
    val title = _title.asStateFlow()
    /**
     * 关卡数据
     */
    private val _levelDats = MutableStateFlow(mutableListOf<SudokuLevel>())
    val levelDats = _levelDats.asStateFlow()
    /**
     * 过关弹窗
     * <p 不需要页面返回也不需要组装参数，故而直接使用StateFlow，减少声明对象/>
     */
    private val _showDone = MutableStateFlow(false)
    val showDone = _showDone.asStateFlow()

    /**
     * 失败弹窗
     */
    private val _showError = MutableStateFlow(false)
    val showError = _showError.asStateFlow()
    /**
     * 页面初始化状态
     */
    override fun createInitialState(): SudokuState {
        return SudokuLoading
    }

    /**
     * 页面交互
     */
    override fun handleEvent(event: SudokuEvent) {
        when(event){
            is SudokuClickEvent -> {
                sudokuClick(event.position)
            }
            is InLifeEvent -> {
                mVisible = event.isVisible
            }
            is DeleteClickEvent -> {
                deleteItem(event.position)
                inputEnableCount++
            }
            is InputValue -> {
                inputItem(event)
            }
            is BackClickEvent -> {
                val item = operationList.pop()
                if(item is DeleteItem){
                    inputItem(InputValue(item.position,item.item.value),false)
                }else if(item is InputItem){
                    deleteItem(item.position,false)
                    inputEnableCount--
                }
                _backEnable.tryEmit(operationList.isNotEmpty())
            }
        }
    }

    /**
     * 填数
     * @param event 输入的参数
     * @param notBack 是否属于撤回操作
     */
    private fun inputItem(event: InputValue,notBack:Boolean = true) {
        val item = _pageData.value.data[event.position]
        if(notBack){
            operationList.push(InputItem(event.position, item.clone()))
        }
        item.value = event.value
        val error = item.code != item.value
        if (error) {
            item.state = ErrorTarget
            errorCount++
            if(errorCount >= 3){
                // 关闭计时
                _showError.tryEmit(true)
                sendEvent(InLifeEvent(false))
            }
        } else {
            item.state = NotEmptyTarget
            inputEnableCount--
            if(inputEnableCount == 0){
                _showDone.tryEmit(true)
                sendEvent(InLifeEvent(false))
            }
        }
        // 弱关联
        _pageData.value.data.forEach { gridUnit ->
            if(gridUnit.x == item.x && gridUnit.y == item.y){

            }else if (gridUnit.x == item.x || gridUnit.y == item.y || isTheSameBox(gridUnit, item)) {
                if (isTheSameBox(gridUnit, item) && error && gridUnit.value == item.value) {
                    gridUnit.state = ErrorAssociation
                } else {
                    gridUnit.state = WeakAssociation
                }
            }else if (gridUnit.value == item.value) {
                gridUnit.state = TheSameAssociation
            }
        }

        _inputEnable.tryEmit(InputEnable(false, 0))
        _deleteEnable.tryEmit(DeleteEnable(true, event.position))
        if(notBack){
            _backEnable.tryEmit(true)
        }
        _pageData.tryEmit(SudokuData(_pageData.value.data))
    }

    /**
     * 删除元素操作
     * @param position 删除元素的下标
     * @param notBack 是否属于返回操作
     */
    private fun deleteItem(position: Int,notBack: Boolean = true) {
        val item = _pageData.value.data[position].clone()
        if(notBack){
            operationList.push(DeleteItem(position, item))
        }
        _pageData.value.data[position].value = 0
        sudokuClick(position)
        if(notBack){
            _backEnable.tryEmit(true)
        }
    }

    /**
     * 响应数独点击事件
     * @param position 下标
     */
    private fun sudokuClick(position: Int) {
        val table = _pageData.value.data
        val item = table[position]
        // 只要按下就存在弱关联
        table.forEach { gridUnit ->
            if (gridUnit.x == item.x || gridUnit.y == item.y || isTheSameBox(gridUnit, item)) {
                if(gridUnit.state != ErrorTarget){
                    gridUnit.state = WeakAssociation
                }
            }else{
                if(gridUnit.state != ErrorTarget){
                    gridUnit.state = NormalTarget
                }
            }
        }
        if (item.value == 0) {
            item.state = WeakTarget
            _inputEnable.tryEmit(InputEnable(true, position))
            _deleteEnable.tryEmit(DeleteEnable(false, position))
        } else {
            table.forEach {
                if (it.value == item.value) {
                    it.state = TheSameAssociation
                }
            }
            item.state = NotEmptyTarget
            _inputEnable.tryEmit(InputEnable(false, position))
            if (item.onlyRead.not()) {
                _deleteEnable.tryEmit(DeleteEnable(true, position))
            }

        }
        _pageData.tryEmit(SudokuData(table))
    }

    /**
     * 判断两个单元格是否属于同一单元格
     */
    private fun isTheSameBox(first:GridUnit,second:GridUnit):Boolean{
        return (first.x-1)/3 == (second.x-1)/3 && (first.y-1)/3 == (second.y-1)/3
    }

    /**
     * 渲染九宫格数据
     */
    fun createTable(level:Int) = viewModelScope.launch(Dispatchers.IO) {
        val table = initTable()
        getData().forEachIndexed { index: Int, c: Char ->
            table[index].code = c.digitToInt()
            table[index].value = table[index].code
        }
        inputEnableCount = LevelMath.setLevelData(LevelMath.getLevelName(level),table)
        _pageData.tryEmit(SudokuData(table))
        setState {SudokuPlay }
        _title.tryEmit("第${level}关")
        errorCount = 0
    }

    /**
     * 初始化页面数据
     */
    private fun initTable(): MutableList<GridUnit> {
        val table = mutableListOf<GridUnit>()
        for (i in 1 until 82) {
            val x = (i % 9)
            val y = ceil(i / 9.0).toInt()
            table.add(GridUnit(if (x == 0) 9 else x, y))
        }
        return table
    }


    /**
     * 获取九宫格数据
     */
    private suspend fun getData(): String {
        val data = MatrixAlgorithm.create()
        val result = dao.queryLevel(data)
        return if (result.isNullOrEmpty()) {
            data
        } else {
            getData()
        }
    }

    /**
     * 查询关卡数据
     */
    fun getLevelData(){
        viewModelScope.launch(Dispatchers.IO) {
            val result = mutableListOf<SudokuLevel>()
            val levels = dao.loadAllSudokuLevels()
            if(levels.isEmpty()){
                result.addAll((1..30).map {
                    SudokuLevel(it,"","",0).also { it.isPlay = false }
                })
                result.first().isPlay = true
            }else{
                (0..29).forEach {
                    if(it >= levels.size){

                        result.add(SudokuLevel(it+1,"","",0).also { it.isPlay = false })
                    }else{
                        result.add(levels[it])
                    }
                }
                result[levels.size].isPlay = true
            }
            _levelDats.tryEmit(result)
            setState {SudokuHome }
        }

    }


    /**
     * 计算闯关时间
     */
    fun runTimer() = viewModelScope.launch(Dispatchers.Main) {
        currentTime = 0
        _time.tryEmit(timeFormat.format(Date(currentTime)))
        while (true) {
            delay(1000)
            if(mVisible){
                currentTime+=1000
                _time.tryEmit(timeFormat.format(Date(currentTime)))
            }

        }
    }

    fun getTime(time:Long):String{
        return if(time == 0L) ""
        else  timeFormat.format(Date(time))
    }

    private val timeFormat by lazy {
        val result = SimpleDateFormat("mm:ss",Locale.ENGLISH)
        result.also { it.timeZone = TimeZone.getTimeZone("GMT+00:00") }
    }

    private val dao by lazy {
        Room.databaseBuilder(
            MyApplication.application,
            AppDataBase::class.java, "database-name"
        ).build().SudokuDao()
    }
}