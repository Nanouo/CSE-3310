package com.reread.app.ui.listings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.reread.app.R
import com.reread.app.data.Book
import java.io.File
import coil.load

class MyListingsAdapter(
    private val books: MutableList<Book>,
    private val onEdit: (Book) -> Unit,
    private val onDelete: (Book) -> Unit
) : RecyclerView.Adapter<MyListingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_listing, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount() = books.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView     = itemView.findViewById(R.id.tv_my_listing_title)
        private val tvAuthor: TextView    = itemView.findViewById(R.id.tv_my_listing_author)
        private val tvPrice: TextView     = itemView.findViewById(R.id.tv_my_listing_price)
        private val tvCondition: TextView = itemView.findViewById(R.id.tv_my_listing_condition)
        private val tvCategory: TextView  = itemView.findViewById(R.id.tv_my_listing_category)
        private val btnEdit: Button       = itemView.findViewById(R.id.btn_edit_listing)
        private val btnDelete: Button     = itemView.findViewById(R.id.btn_delete_listing)
        private val ivImage: ImageView = itemView.findViewById(R.id.iv_book_image)

        fun bind(book: Book) {
            tvTitle.text     = book.title
            tvAuthor.text    = book.author
            tvPrice.text     = "\$${String.format("%.2f", book.price)}"
            tvCondition.text = book.condition
            tvCategory.text  = book.category
            btnEdit.setOnClickListener   { onEdit(book) }
            btnDelete.setOnClickListener { onDelete(book) }
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