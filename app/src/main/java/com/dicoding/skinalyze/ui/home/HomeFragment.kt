package com.dicoding.skinalyze.ui.home

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.skinalyze.R
import com.dicoding.skinalyze.database.HistoryAnalyzeRoom
import com.dicoding.skinalyze.databinding.FragmentHomeBinding
import com.dicoding.skinalyze.repository.HistoryAnalyzeRepository
import com.dicoding.skinalyze.ui.history.HistoryAdapter
import com.dicoding.skinalyze.ui.history.HistoryViewModel
import com.dicoding.skinalyze.ui.viewmodel.ViewModelFactory

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HistoryViewModel
    private lateinit var historyAdapter: HistoryAdapter
    private val historyAnalyzeRepository: HistoryAnalyzeRepository by lazy {
        val historyAnalyzeDao = HistoryAnalyzeRoom.getDatabase(requireContext()).historyAnalyzeDao()
        HistoryAnalyzeRepository(historyAnalyzeDao)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val helloText = "Hello"
        val userName = "Abdul"
        val spannable = SpannableString("$helloText $userName")
        spannable.setSpan(
            StyleSpan(android.graphics.Typeface.BOLD),
            0,
            helloText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvHelloUser.text = spannable
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        historyAdapter = HistoryAdapter(mutableListOf()) { history ->
            val bundle = Bundle().apply {
                putInt("historyId", history.id)
                putString("condition", history.result)
            }
            findNavController().navigate(R.id.action_homeFragment_to_resultFragment, bundle)
        }

        binding.recyclerViewRecentHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRecentHistory.adapter = historyAdapter

        val factory = ViewModelFactory(historyAnalyzeRepository)
        homeViewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]
        homeViewModel.getRecentHistories()
        homeViewModel.historyList.observe(viewLifecycleOwner) { historyList ->
            if (historyList.isNullOrEmpty()) {
                binding.noDataText.visibility = View.VISIBLE
                binding.recyclerViewRecentHistory.visibility = View.GONE
            } else {
                binding.noDataText.visibility = View.GONE
                binding.recyclerViewRecentHistory.visibility = View.VISIBLE
                historyAdapter.updateHistoryList(historyList)
            }
        }

        binding.btnReminder.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingFragment)
        }

        binding.btnAnalyze.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_analyzeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

