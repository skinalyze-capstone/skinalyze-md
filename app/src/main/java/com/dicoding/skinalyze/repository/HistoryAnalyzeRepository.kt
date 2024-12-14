package com.dicoding.skinalyze.repository

import androidx.lifecycle.LiveData
import com.dicoding.skinalyze.database.HistoryAnalyze
import com.dicoding.skinalyze.database.HistoryAnalyzeDao

class HistoryAnalyzeRepository(private val historyAnalyzeDao: HistoryAnalyzeDao) {

    fun getAllHistory(): LiveData<List<HistoryAnalyze>> {
        return historyAnalyzeDao.getAllHistory()
    }

    suspend fun insert(historyAnalyze: HistoryAnalyze) {
        historyAnalyzeDao.insert(historyAnalyze)
    }

    suspend fun deleteById(id: Int) {
        historyAnalyzeDao.deleteById(id)
    }
}
