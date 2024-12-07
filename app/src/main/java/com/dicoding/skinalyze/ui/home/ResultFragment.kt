package com.dicoding.skinalyze.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.skinalyze.databinding.FragmentResultBinding
import com.dicoding.skinalyze.ui.history.History

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var resultAdapter: ResultAdapter
    private var historyList: List<History> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)

        // Ambil data dari arguments
        val condition = arguments?.getString("condition_key")
        historyList = listOf(
            History(condition ?: "Unknown", "00:00", "2024-12-07", "No recommendations yet")
        ) // Data dummy untuk uji coba

        // Siapkan adapter
        resultAdapter = ResultAdapter(historyList) {
            // Aksi ketika item di klik
        }
        binding.rvResultList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = resultAdapter
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
