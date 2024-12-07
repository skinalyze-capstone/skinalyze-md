package com.dicoding.skinalyze.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.skinalyze.R
import com.dicoding.skinalyze.databinding.ItemHistoryBinding

class HistoryAdapter(
    private val histories: List<History>,
    private val onItemClick: (History) -> Unit ) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: History) {
            binding.conditionTitle.text = history.condition
            binding.timeTextView.text = history.time
            binding.dateTextView.text = history.date
            binding.recommendationTextView.text = history.recommendation
            binding.conditionImageView.setImageResource(R.drawable.ic_placeholder_image)

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
        holder.bind(histories[position])
    }

    override fun getItemCount(): Int = histories.size
}
