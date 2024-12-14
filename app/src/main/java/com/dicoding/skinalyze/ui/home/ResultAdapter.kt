package com.dicoding.skinalyze.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.skinalyze.database.HistoryAnalyze
import com.dicoding.skinalyze.databinding.ItemAnalyzeBinding

class ResultAdapter(
    private val historyList: List<HistoryAnalyze>
) : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    inner class ResultViewHolder(private val binding: ItemAnalyzeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(history: HistoryAnalyze) {
            binding.apply {
                tvDiagnoseTitle.text = "Diagnose"
                tvDiagnoseResult.text = history.result
                tvIngredientsTitle.text = "Ingredients Recommendation"
                tvIngredientsResult.text = history.ingredients
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemAnalyzeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    override fun getItemCount(): Int = historyList.size
}

