package com.vicent.composeapplication.mvi

/**
 * <pre>
 *     author: Vincent
 *     date  :  2021/12/1 9:36
 *     desc  : MVI架构下的定义事件
 * </pre>
 */

/**
 * 页面状态
 */
interface UiState

/**
 * 页面事件
 */
interface UiEvent

/**
 * 页面响应
 * 例如：弹Toast、导航Fragment等
 */
interface UiEffect