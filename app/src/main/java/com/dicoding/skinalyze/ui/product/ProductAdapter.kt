package com.dicoding.skinalyze.ui.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.skinalyze.R

class ProductAdapter(
    private val productList: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.iv_product_image)
        val productName: TextView = view.findViewById(R.id.tv_product_name)
        val productDescription: TextView = view.findViewById(R.id.tv_product_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productName.text = product.nameProduct
        // Menampilkan brand dan kandungan produk di deskripsi
        holder.productDescription.text = "Brand: ${product.brand}. Kandungan: ${product.kandunganProduct}"
        Glide.with(holder.itemView.context).load(product.imageUrl).into(holder.image)

        holder.itemView.setOnClickListener { onItemClick(product) }
    }

    override fun getItemCount(): Int = productList.size
}
