package com.dicoding.skinalyze.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.skinalyze.R

class ProductRecommendationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val categories = listOf("Acne", "Bags", "Bopeng", "Beruntusan", "Milia", "Redness")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_recommendation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_categories)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = CategoryAdapter(categories) { category ->
            // Navigasi menggunakan Bundle
            val bundle = Bundle()
            bundle.putString("category", category)

            findNavController().navigate(
                R.id.action_productRecommendationFragment_to_itemProductFragment,
                bundle
            )
        }
        recyclerView.adapter = adapter
    }
}
