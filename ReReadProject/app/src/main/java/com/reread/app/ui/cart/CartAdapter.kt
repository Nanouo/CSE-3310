package com.reread.app.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reread.app.R
import com.reread.app.data.CartItem

class CartAdapter(
    private val onRemoveClick: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_cart_title)
        private val tvAuthor: TextView = itemView.findViewById(R.id.tv_cart_author)
        private val tvPrice: TextView = itemView.findViewById(R.id.tv_cart_price)
        private val tvCondition: TextView = itemView.findViewById(R.id.tv_cart_condition)
        private val tvSeller: TextView = itemView.findViewById(R.id.tv_cart_seller)
        private val btnRemove: ImageButton = itemView.findViewById(R.id.btn_remove)

        fun bind(item: CartItem) {
            tvTitle.text = item.title
            tvAuthor.text = item.author
            tvPrice.text = "$${String.format("%.2f", item.price)}"
            tvCondition.text = item.condition
            tvSeller.text = "Sold by ${item.sellerUsername}"
            btnRemove.setOnClickListener { onRemoveClick(item) }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem) = oldItem.bookId == newItem.bookId
        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem) = oldItem == newItem
    }
}