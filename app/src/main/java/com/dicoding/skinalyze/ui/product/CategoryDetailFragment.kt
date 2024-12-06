package com.dicoding.skinalyze.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.skinalyze.R

class CategoryDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_category_detail, container, false)

        // Ambil argumen dari Bundle
        val categoryName = arguments?.getString("categoryName") ?: ""
        val products = getProductsForCategory(categoryName)

        val rvProducts = view.findViewById<RecyclerView>(R.id.rv_products)
        rvProducts.adapter = ProductAdapter(products) { product ->
            val bundle = Bundle().apply {
                putString("productName", product.name)
                putInt("productImageRes", product.imageRes)
            }
            findNavController().navigate(R.id.action_categoryDetailFragment_to_productDetailFragment, bundle)
        }

        return view
    }

    private fun getProductsForCategory(category: String): List<Product> {
        return when (category) {
            "Acne" -> listOf(
                Product("SK-II Facial Cleanser", R.drawable.ic_placeholder_image),
                Product("Cetaphil Gentle Cleanser", R.drawable.ic_placeholder_image)
            )
            else -> emptyList()
        }
    }
}