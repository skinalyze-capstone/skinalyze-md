package com.dicoding.skinalyze.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "HistoryAnalyze")
data class HistoryAnalyze(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageUri: String,
    val ingredients: String,
    val result: String,
    val date: String,
    val time: String
) : Parcelable