package com.reread.app.ui.home

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.reread.app.R
import com.reread.app.data.Book
import com.reread.app.data.CartManager
import com.reread.app.data.MessagingRepository
import com.reread.app.ui.cart.CartActivity
import com.reread.app.ui.messaging.ChatActivity
import com.reread.app.utils.SessionManager
import coil.load
import java.io.File

class BookDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val bookId    = intent.getIntExtra("book_id", 0)
        val title     = intent.getStringExtra("book_title") ?: ""
        val author    = intent.getStringExtra("book_author") ?: ""
        val price     = intent.getDoubleExtra("book_price", 0.0)
        val condition = intent.getStringExtra("book_condition") ?: ""
        val category  = intent.getStringExtra("book_category") ?: ""
        val desc      = intent.getStringExtra("book_description") ?: "No description provided."
        val seller    = intent.getStringExtra("book_seller") ?: ""
        val sellerId  = intent.getIntExtra("book_seller_id", -1)

        findViewById<TextView>(R.id.tv_detail_title).text       = title
        findViewById<TextView>(R.id.tv_detail_author).text      = "by $author"
        findViewById<TextView>(R.id.tv_detail_price).text       = "\$${String.format("%.2f", price)}"
        findViewById<TextView>(R.id.tv_detail_condition).text   = "Condition: $condition"
        findViewById<TextView>(R.id.tv_detail_category).text    = "Category: $category"
        findViewById<TextView>(R.id.tv_detail_description).text = desc
        findViewById<TextView>(R.id.tv_detail_seller).text      = "Sold by: $seller"

        val btnAddToCart     = findViewById<Button>(R.id.btn_add_to_cart)
        val btnMessageSeller = findViewById<Button>(R.id.btn_message_seller)
        val tvSwitchMode     = findViewById<TextView>(R.id.tv_switch_to_buyer)
        val session          = SessionManager(this)

        val imagePath = intent.getStringExtra("book_image_path") ?: ""
        val ivImage = findViewById<ImageView>(R.id.iv_book_image)

        if (imagePath.isNotBlank()) {
            ivImage.scaleType = ImageView.ScaleType.FIT_CENTER

            ivImage.load(File(imagePath)) {
                placeholder(R.drawable.placeholder_image)
                error(R.drawable.placeholder_image)
                crossfade(true)
            }
        } else {
            ivImage.scaleType = ImageView.ScaleType.CENTER_CROP
            ivImage.load(R.drawable.placeholder_image)
        }

        when (session.role) {
            "buyer" -> {
                btnAddToCart.visibility     = View.VISIBLE
                btnMessageSeller.visibility = View.VISIBLE
                tvSwitchMode.visibility     = View.GONE

                updateCartButton(btnAddToCart, bookId)

                btnAddToCart.setOnClickListener {
                    if (CartManager.isInCart(bookId)) {
                        startActivity(Intent(this, CartActivity::class.java))
                    } else {
                        val book = Book(
                            id             = bookId,
                            sellerId       = 0,
                            sellerUsername = seller,
                            title          = title,
                            author         = author,
                            price          = price,
                            condition      = condition,
                            category       = category,
                            imagePath      = imagePath,
                            description    = desc
                        )
                        CartManager.addItem(book)
                        updateCartButton(btnAddToCart, bookId)
                        Toast.makeText(this, "Added to cart!", Toast.LENGTH_SHORT).show()
                    }
                }

                btnMessageSeller.setOnClickListener {
                    if (sellerId == -1) {
                        Toast.makeText(this, "Cannot message this seller", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    val repo = MessagingRepository(this)
                    val conversationId = repo.getOrCreateConversation(
                        bookId        = bookId,
                        bookTitle     = title,
                        buyerId       = session.userId,
                        buyerUsername = session.username,
                        sellerId      = sellerId,
                        sellerUsername = seller
                    )
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra("conversation_id", conversationId)
                    intent.putExtra("book_title", title)
                    intent.putExtra("other_username", seller)
                    startActivity(intent)
                }
            }
            else -> {
                btnAddToCart.visibility     = View.GONE
                btnMessageSeller.visibility = View.GONE
                tvSwitchMode.visibility     = View.VISIBLE
            }
        }
    }

    private fun updateCartButton(btn: Button, bookId: Int) {
        if (CartManager.isInCart(bookId)) {
            btn.text = "View Cart"
            btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#007AFF"))
        } else {
            btn.text = "Add to Cart"
            btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A1A1A"))
        }
    }
}