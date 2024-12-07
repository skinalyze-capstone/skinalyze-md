package com.dicoding.skinalyze.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.skinalyze.databinding.ItemAnalyzeBinding
import com.dicoding.skinalyze.ui.history.History

class ResultAdapter(
    private val historyList: List<History>,
    private val onItemClick: (History) -> Unit
) : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    // ViewHolder untuk item
    inner class ResultViewHolder(private val binding: ItemAnalyzeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Menghubungkan data ke tampilan
        @SuppressLint("SetTextI18n")
        fun bind(history: History) {
            binding.apply {
                tvDiagnoseTitle.text = "Diagnose"
                tvDiagnoseResult.text = history.condition
                tvIngredientsTitle.text = "Ingredients Recommendation"
                tvIngredientsResult.text = history.recommendation

                // Menangani klik pada item
                root.setOnClickListener {
                    onItemClick(history)
                }
            }
        }
    }

    // Membuat ViewHolder dengan layout item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemAnalyzeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultViewHolder(binding)
    }

    // Mengikat data ke ViewHolder
    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    // Menghitung jumlah item
    override fun getItemCount(): Int = historyList.size
}
