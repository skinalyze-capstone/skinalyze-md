package com.dicoding.skinalyze.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import android.view.ViewGroup
import android.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dicoding.skinalyze.database.HistoryAnalyzeRoom
import com.dicoding.skinalyze.databinding.FragmentResultBinding
import com.dicoding.skinalyze.repository.HistoryAnalyzeRepository
import com.dicoding.skinalyze.ui.history.HistoryViewModel
import com.dicoding.skinalyze.ui.viewmodel.ViewModelFactory

class ResultFragment : Fragment() {

    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var resultAdapter: ResultAdapter

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private val historyAnalyzeRepository: HistoryAnalyzeRepository by lazy {
        val historyAnalyzeDao = HistoryAnalyzeRoom.getDatabase(requireContext()).historyAnalyzeDao()
        HistoryAnalyzeRepository(historyAnalyzeDao)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        val historyId = arguments?.getInt("historyId") ?: run {
            Toast.makeText(requireContext(), "History not found.", Toast.LENGTH_SHORT).show()
            return binding.root
        }
        val factory = ViewModelFactory(historyAnalyzeRepository)

        historyViewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]
        historyViewModel.getHistoryById(historyId).observe(viewLifecycleOwner) { history ->
            Glide.with(this)
                .load(history.imageUri)
                .into(binding.ivFaceImagePreview)
            resultAdapter = ResultAdapter(listOf(history))
            binding.rvResultList.layoutManager = LinearLayoutManager(requireContext())
            binding.rvResultList.adapter = resultAdapter
            binding.dateTextView.text = history.date
            binding.timeTextView.text = history.time
            binding.ivTrash.setOnClickListener {
                deleteHistory(historyId)
            }
        }
        return binding.root
        Toast.makeText(requireContext(), "History not found.", Toast.LENGTH_SHORT).show()
    }

    private fun deleteHistory(id: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to delete this history?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                historyViewModel.deleteHistory(id)
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

