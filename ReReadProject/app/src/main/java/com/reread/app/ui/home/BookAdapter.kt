package com.reread.app.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reread.app.R
import com.reread.app.data.Book
import java.io.File
import coil.load

class BookAdapter(
    private val onBookClick: (Book) -> Unit
) : ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_book_title)
        private val tvAuthor: TextView = itemView.findViewById(R.id.tv_book_author)
        private val tvPrice: TextView = itemView.findViewById(R.id.tv_book_price)
        private val tvCondition: TextView = itemView.findViewById(R.id.tv_book_condition)
        private val tvCategory: TextView = itemView.findViewById(R.id.tv_book_category)
        private val tvSeller: TextView = itemView.findViewById(R.id.tv_book_seller)
        private val ivImage: ImageView = itemView.findViewById(R.id.iv_book_image)

        fun bind(book: Book) {
            tvTitle.text = book.title
            tvAuthor.text = book.author
            tvPrice.text = "$${String.format("%.2f", book.price)}"
            tvCondition.text = book.condition
            tvCategory.text = book.category
            tvSeller.text = "Sold by ${book.sellerUsername}"
            itemView.setOnClickListener { onBookClick(book) }
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

    class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.title == newItem.title &&
                    oldItem.author == newItem.author &&
                    oldItem.price == newItem.price &&
                    oldItem.imagePath == newItem.imagePath
        }
    }
}


