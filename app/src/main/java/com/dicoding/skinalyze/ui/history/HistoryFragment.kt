package com.dicoding.skinalyze.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.skinalyze.databinding.FragmentHistoryBinding
import androidx.navigation.fragment.findNavController
import com.dicoding.skinalyze.R

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val allHistories = loadAllHistories() // Ganti dengan data dari database
        val adapter = HistoryAdapter(allHistories) { history ->
            val bundle = Bundle().apply {
                putString("condition", history.condition)
                putString("recommendation", history.recommendation)
                putString("time", history.time)
                putString("date", history.date)
            }
            findNavController().navigate(R.id.action_historyFragment_to_resultFragment, bundle)
        }
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHistory.adapter = adapter
    }

    private fun loadAllHistories(): List<History> {
        return listOf(
            History("Acne", "12:00 PM", "01/12/2024", "Use salicylic acid"),
            History("Dry Skin", "08:00 AM", "30/11/2024", "Use moisturizer"),
            History("Oily Skin", "09:00 AM", "25/11/2024", "Use oil-free cleanser"),
            History("Dark Spots", "03:00 PM", "20/11/2024", "Apply vitamin C serum"),
            History("Sensitive Skin", "07:00 AM", "15/11/2024", "Use gentle cleanser"),
            History("Sunburn", "05:00 PM", "10/11/2024", "Apply aloe vera gel")
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
     }
}