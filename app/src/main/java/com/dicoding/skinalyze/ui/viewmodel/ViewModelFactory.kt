package com.dicoding.skinalyze.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.skinalyze.repository.HistoryAnalyzeRepository
import com.dicoding.skinalyze.ui.history.HistoryViewModel

class ViewModelFactory(private val repository: HistoryAnalyzeRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            HistoryViewModel(repository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
