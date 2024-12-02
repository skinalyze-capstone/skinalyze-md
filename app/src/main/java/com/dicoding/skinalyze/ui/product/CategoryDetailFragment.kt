package com.dicoding.skinalyze.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.skinalyze.R

class CategoryDetailFragment : Fragment() {

    private val args: CategoryDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_category_detail, container, false)

        val categoryName = args.categoryName
        val products = getProductsForCategory(categoryName)

        val rvProducts = view.findViewById<RecyclerView>(R.id.rv_products)
        rvProducts.adapter = ProductAdapter(products) { product ->
            val action = CategoryDetailFragmentDirections.actionCategoryDetailFragmentToProductDetailFragment(
                product.name,
                product.imageRes
            )
            findNavController().navigate(action)
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
