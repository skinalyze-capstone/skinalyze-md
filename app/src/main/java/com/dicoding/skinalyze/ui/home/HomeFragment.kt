package com.dicoding.skinalyze.ui.home

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.skinalyze.R
import com.dicoding.skinalyze.databinding.FragmentHomeBinding
import com.dicoding.skinalyze.ui.history.History
import com.dicoding.skinalyze.ui.history.HistoryAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inisialisasi binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Mengatur teks dengan gaya yang diinginkan
        val helloText = "Hello"
        val userName = "Abdul"
        val spannable = SpannableString("$helloText $userName")

        // Membuat teks "Hello" menjadi bold
        spannable.setSpan(
            StyleSpan(android.graphics.Typeface.BOLD),
            0, // Mulai dari indeks 0
            helloText.length, // Sampai panjang "Hello"
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Menampilkan teks pada TextView
        binding.tvHelloUser.text = spannable

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menangani klik pada tombol btn_reminder
        binding.btnReminder.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingFragment)
        }

        binding.btnAnalyze.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_analyzeFragment)
        }

        val recentHistories = loadRecentHistories() // Maksimal 2 data
        val adapter = HistoryAdapter(recentHistories)

        // Mengatur RecyclerView untuk menampilkan history
        binding.recyclerViewRecentHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRecentHistory.adapter = adapter
    }

    private fun loadRecentHistories(): List<History> {
        // Contoh data dummy, ganti dengan data dari database
        return listOf(
            History("Acne", "12:00 PM", "01/12/2024", "Use salicylic acid"),
            History("Dry Skin", "08:00 AM", "30/11/2024", "Use moisturizer")
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
