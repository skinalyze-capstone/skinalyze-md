package com.dicoding.skinalyze.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.skinalyze.database.HistoryAnalyze
import com.dicoding.skinalyze.repository.HistoryAnalyzeRepository
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: HistoryAnalyzeRepository) : ViewModel() {
    val historyList: LiveData<List<HistoryAnalyze>> = repository.getAllHistory()

    fun deleteHistory(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun getHistoryById(id: Int): LiveData<HistoryAnalyze> {
        return repository.getById(id)
    }

    fun getRecentHistories(): LiveData<List<HistoryAnalyze>> {
        return repository.getRecentHistories()
    }
}