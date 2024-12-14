package com.dicoding.skinalyze.ui.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.skinalyze.R
import com.dicoding.skinalyze.database.HistoryAnalyze
import com.dicoding.skinalyze.databinding.ItemHistoryBinding

class HistoryAdapter(
    private var historyList: MutableList<HistoryAnalyze>,
    private val onItemClick: (HistoryAnalyze) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoryAnalyze) {
            binding.conditionTitle.text = history.result
            binding.timeTextView.text = history.time
            binding.dateTextView.text = history.date
            binding.recommendationTextView.text = history.ingredients
            Glide.with(binding.conditionImageView.context)
                .load(history.imageUri)
                .placeholder(R.drawable.ic_placeholder_image)
                .into(binding.conditionImageView)
            itemView.setOnClickListener {
                onItemClick(history)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    override fun getItemCount(): Int = historyList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateHistoryList(newHistoryList: List<HistoryAnalyze>) {
        historyList.clear()
        historyList.addAll(newHistoryList)
        notifyDataSetChanged()
    }
}
