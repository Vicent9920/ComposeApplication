package com.vicent.composeapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vicent.composeapplication.mvi.UiEffect
import com.vicent.composeapplication.mvi.UiEvent
import com.vicent.composeapplication.mvi.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * <pre>
 *     author: Vincent
 *     date  :  2021/12/1 9:35
 *     desc  :
 * </pre>
 */
abstract class BaseViewModel<State : UiState, Event : UiEvent, Effect : UiEffect> :ViewModel() {
      val TAG = "BaseViewModel"
    /**
     * 初始状态
     * stateFlow区别于LiveData必须有初始值
     */
    private val initialState: State by lazy { createInitialState() }

    abstract fun createInitialState(): State

    /**
     * uiState聚合页面的全部UI 状态
     */
    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)

    val currentState = _uiState.asStateFlow()


    /**
     * event包含用户与ui的交互（如点击操作），也有来自后台的消息（如切换自习模式）
     */
    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()

    val event = _event.asSharedFlow()


    /**
     * effect用作 事件带来的副作用，通常是 一次性事件 且 一对一的订阅关系
     * 例如：弹Toast、导航Fragment等
     */
    private val _effect: Channel<Effect> = Channel()

    val effect = _effect.receiveAsFlow()


    init {
        subscribeEvents()
    }

    private fun subscribeEvents() {
        viewModelScope.launch {
            event.collect {
                handleEvent(it)
            }
        }
    }

    protected abstract fun handleEvent(event: Event)

    fun sendEvent(event: Event) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    protected fun setState(reduce: State.() -> State) {
       viewModelScope.launch(Dispatchers.Main) {
           val newState = initialState.reduce()
           _uiState.value = newState
       }
    }

    protected fun setEffect(builder: () -> Effect) {
        val newEffect = builder()
        viewModelScope.launch {
            _effect.send(newEffect)
        }
    }
}