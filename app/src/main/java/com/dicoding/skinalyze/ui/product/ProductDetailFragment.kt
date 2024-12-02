package com.dicoding.skinalyze.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.dicoding.skinalyze.R

class ProductDetailFragment : Fragment() {

    private val args: ProductDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_product_detail, container, false)

        val ivProductImage = view.findViewById<ImageView>(R.id.iv_product_image)
        val tvProductTitle = view.findViewById<TextView>(R.id.tv_product_title)
        val tvProductDescription = view.findViewById<TextView>(R.id.tv_product_description)
        val tvProductIngredients = view.findViewById<TextView>(R.id.tv_product_ingredients)
        val tvProductUsage = view.findViewById<TextView>(R.id.tv_product_usage)

        // Isi data berdasarkan args
        tvProductTitle.text = args.productName
        ivProductImage.setImageResource(args.productImageRes)
        tvProductDescription.text = "This is a detailed description of ${args.productName}."
        tvProductIngredients.text = "Key Ingredients: Water, Glycerin, Niacinamide."
        tvProductUsage.text = "Usage: Apply a small amount on your face and rinse off."

        return view
    }
}
