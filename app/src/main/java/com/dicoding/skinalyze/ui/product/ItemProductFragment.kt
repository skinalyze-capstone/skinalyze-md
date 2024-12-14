package com.dicoding.skinalyze.ui.product

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.skinalyze.R

class ItemProductFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_recommendation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ganti teks di TextView untuk laman Item Product
        val titleTextView: TextView = view.findViewById(R.id.tv_title_category)
        titleTextView.text = "Choose based on your Diagnose"

        recyclerView = view.findViewById(R.id.rv_categories)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Ambil kategori dari Bundle
        val category = arguments?.getString("category") ?: return
        val productList = ProductData.products[category] ?: emptyList()

        // Set adapter untuk RecyclerView
        val adapter = ProductAdapter(productList) { product ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(product.linkProduct))
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }
}
