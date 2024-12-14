package com.dicoding.skinalyze.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.skinalyze.R
import com.dicoding.skinalyze.database.HistoryAnalyze
import com.dicoding.skinalyze.database.HistoryAnalyzeRoom
import com.dicoding.skinalyze.databinding.FragmentHistoryBinding
import com.dicoding.skinalyze.repository.HistoryAnalyzeRepository
import com.dicoding.skinalyze.ui.viewmodel.ViewModelFactory
import androidx.navigation.fragment.findNavController

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyList: MutableList<HistoryAnalyze>
    private val historyAnalyzeRepository: HistoryAnalyzeRepository by lazy {
        val historyAnalyzeDao = HistoryAnalyzeRoom.getDatabase(requireContext()).historyAnalyzeDao()
        HistoryAnalyzeRepository(historyAnalyzeDao)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        historyList = mutableListOf()
        historyAdapter = HistoryAdapter(historyList) { history ->
            val bundle = Bundle().apply {
                putInt("historyId", history.id)
                putString("condition", history.result)
            }
            findNavController().navigate(R.id.action_historyFragment_to_resultFragment, bundle)
        }

        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHistory.adapter = historyAdapter

        val factory = ViewModelFactory(historyAnalyzeRepository)
        historyViewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]

        historyViewModel.historyList.observe(viewLifecycleOwner) { historyList ->
            if (historyList.isNullOrEmpty()) {
                binding.noDataText.visibility = View.VISIBLE
                binding.recyclerViewHistory.visibility = View.GONE
            } else {
                binding.noDataText.visibility = View.GONE
                binding.recyclerViewHistory.visibility = View.VISIBLE
                historyAdapter.updateHistoryList(historyList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

