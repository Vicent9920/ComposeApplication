package com.vicent.composeapplication.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * <pre>
 *     author: Vincent
 *     date  :  2021/11/29 12:23
 *     desc  :  数据库Dao
 * </pre>
 */
@Dao
interface SudokuDao {

    /**
     * 插入数据
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData( data: SudokuLevel)

    /**
     * 查询数据
     * @param target 数独数字
     */
    @Query("SELECT * FROM SudokuLevel WHERE data = :target")
    suspend fun queryLevel(target:String): Array<SudokuLevel?>?

    /**
     * 加载所有关卡数据
     */
    @Query("SELECT * FROM SudokuLevel")
    fun loadAllSudokuLevels(): Array<SudokuLevel>
}