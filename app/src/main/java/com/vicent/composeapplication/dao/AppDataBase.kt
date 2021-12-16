package com.vicent.composeapplication.dao

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * <pre>
 *     author: Vincent
 *     date  :  2021/11/29 18:34
 *     desc  : 数据库实例
 * </pre>
 */
@Database(entities = [SudokuLevel::class], version = 1,exportSchema = false)
abstract class AppDataBase:RoomDatabase() {

    abstract fun SudokuDao(): SudokuDao
}