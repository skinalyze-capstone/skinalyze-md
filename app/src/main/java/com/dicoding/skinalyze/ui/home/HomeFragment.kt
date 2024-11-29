package com.dicoding.skinalyze.ui.home

import android.os.Bundle
import android.text.SpannableString
import android.text.Spannable
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.skinalyze.R
import com.dicoding.skinalyze.databinding.FragmentHomeBinding

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
