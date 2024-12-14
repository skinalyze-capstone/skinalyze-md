package com.dicoding.skinalyze.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryAnalyzeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historyAnalyze: HistoryAnalyze): Long

    @Query("SELECT * FROM HistoryAnalyze WHERE id = :id LIMIT 1")
    fun getById(id: Int): LiveData<HistoryAnalyze>

    @Query("SELECT * FROM HistoryAnalyze ORDER BY time DESC")
    fun getAllHistory(): LiveData<List<HistoryAnalyze>>

    @Query("DELETE FROM HistoryAnalyze WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM HistoryAnalyze ORDER BY id DESC LIMIT 3")
    fun getRecentHistories(): LiveData<List<HistoryAnalyze>>
}