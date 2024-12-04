package com.dicoding.skinalyze.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.skinalyze.R

class ProductFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_product, container, false)

        val categories = listOf(
            "Acne", "Eye bags", "Blackhead", "Milia",
            "Redness", "Texture"
        )

        val rvCategories = view.findViewById<RecyclerView>(R.id.rv_categories)
        rvCategories.adapter = CategoryAdapter(categories) { category ->
            val bundle = Bundle().apply {
                putString("categoryName", category)
            }
            findNavController().navigate(R.id.action_productFragment_to_categoryDetailFragment, bundle)
        }

        return view
    }
}
