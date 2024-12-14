package com.dicoding.skinalyze.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dicoding.skinalyze.R

class ProductDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_product_detail, container, false)

        val productName = arguments?.getString("productName") ?: ""
        val productImageRes = arguments?.getInt("productImageRes") ?: 0

        val ivProductImage = view.findViewById<ImageView>(R.id.iv_product_image)
        val tvProductTitle = view.findViewById<TextView>(R.id.tv_product_title)
        val tvProductDescription = view.findViewById<TextView>(R.id.tv_product_description)
        val tvProductIngredients = view.findViewById<TextView>(R.id.tv_product_ingredients)
        val tvProductUsage = view.findViewById<TextView>(R.id.tv_product_usage)

        // Isi data
        tvProductTitle.text = productName
        ivProductImage.setImageResource(productImageRes)
        tvProductDescription.text = "This is a detailed description of $productName."
        tvProductIngredients.text = "Key Ingredients: Water, Glycerin, Niacinamide."
        tvProductUsage.text = "Usage: Apply a small amount on your face and rinse off."

        return view
    }
}