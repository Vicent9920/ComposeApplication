package com.vicent.composeapplication.dao

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * <pre>
 *     author: Vincent
 *     date  :  2021/11/29 12:15
 *     desc  : 游戏关卡
 * </pre>
 * @param id 实体主键 自增
 * @param data 数独数据 81个字符串
 * @param createDate 过关时间、
 * @param time 关卡耗时
 */
@Entity()
data class SudokuLevel(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var data: String,
    var createDate: String,
    var time:Int
){

    var isPlay = false
}
