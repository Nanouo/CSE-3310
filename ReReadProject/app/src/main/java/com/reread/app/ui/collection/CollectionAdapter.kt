package com.reread.app.ui.collection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.reread.app.R
import com.reread.app.data.PurchasedBook
import java.io.File
import coil.load

class CollectionAdapter(
    private val books: List<PurchasedBook>
) : RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount() = books.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView     = itemView.findViewById(R.id.tv_col_title)
        private val tvAuthor: TextView    = itemView.findViewById(R.id.tv_col_author)
        private val tvPrice: TextView     = itemView.findViewById(R.id.tv_col_price)
        private val tvCondition: TextView = itemView.findViewById(R.id.tv_col_condition)
        private val tvOrderId: TextView   = itemView.findViewById(R.id.tv_col_order_id)
        private val ivImage: ImageView = itemView.findViewById(R.id.iv_book_image)

        fun bind(book: PurchasedBook) {
            tvTitle.text     = book.title
            tvAuthor.text    = book.author
            tvPrice.text     = "\$${String.format("%.2f", book.price)}"
            tvCondition.text = book.condition
            tvOrderId.text   = "Order #${book.orderId.takeLast(6).uppercase()}"
            val path = book.imagePath.trim()
            if (path.isNotEmpty()) {
                ivImage.scaleType = ImageView.ScaleType.FIT_CENTER
                ivImage.load(File(path)) {
                    placeholder(R.drawable.placeholder_image)
                    error(R.drawable.placeholder_image)
                    crossfade(false)
                    memoryCacheKey(path)
                }
            } else {
                ivImage.scaleType = ImageView.ScaleType.CENTER_CROP
                ivImage.setImageResource(R.drawable.placeholder_image)
            }
        }
    }
}