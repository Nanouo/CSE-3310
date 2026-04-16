package com.reread.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reread.app.R
import com.reread.app.data.Book
import com.reread.app.ui.account.AccountBottomSheet
import com.reread.app.ui.admin.AdminBottomSheet
import com.reread.app.ui.collection.CollectionBottomSheet
import com.reread.app.ui.listings.MyListingsBottomSheet
import com.reread.app.ui.messaging.InboxActivity
import com.reread.app.utils.SessionManager
import com.reread.app.viewmodel.HomeViewModel

class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: BookAdapter
    private lateinit var session: SessionManager

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var searchView: SearchView
    private lateinit var categoryContainer: LinearLayout
    private lateinit var btnListings: LinearLayout
    private lateinit var btnInbox: LinearLayout
    private lateinit var btnAccount: LinearLayout
    private lateinit var tvListings: TextView
    private lateinit var ivListings: ImageView
    private var selectedCategory: String = "All"
    private val categories = listOf("All", "Academic", "Fiction", "Non-Fiction", "Biography", "Documentary")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        btnListings = findViewById(R.id.btn_listings)
        btnInbox = findViewById(R.id.btn_inbox)
        btnAccount = findViewById(R.id.btn_account)
        tvListings = findViewById(R.id.tv_listings)
        ivListings = findViewById(R.id.ic_listings)

        session = SessionManager(this)
        recyclerView      = findViewById(R.id.rv_books)
        progressBar       = findViewById(R.id.progress_bar)
        tvEmpty           = findViewById(R.id.tv_empty)
        searchView        = findViewById(R.id.search_view)
        categoryContainer = findViewById(R.id.category_container)
        //bottomNav         = findViewById(R.id.bottom_navigation)
        btnListings.setOnClickListener {
            when (currentRole()) {
                "admin" -> AdminBottomSheet().show(supportFragmentManager, "admin")
                "buyer" -> CollectionBottomSheet().show(supportFragmentManager, "collection")
                else -> MyListingsBottomSheet().show(supportFragmentManager, "listings")
            }
        }

        btnInbox.setOnClickListener {
            startActivity(Intent(this, InboxActivity::class.java))
        }

        btnAccount.setOnClickListener {
            AccountBottomSheet().show(supportFragmentManager, "account")
        }
        findViewById<android.widget.ImageButton>(R.id.btn_cart).setOnClickListener {
            startActivity(Intent(this, com.reread.app.ui.cart.CartActivity::class.java))
        }

        setupRecyclerView()
        setupSearch()
        setupCategories()
        observeViewModel()
        refreshRoleUI()
        SessionManager.setRoleChangeListener {
            runOnUiThread {
                refreshRoleUI()
            }
        }
    }

    private fun refreshRoleUI() {
        when (currentRole()) {
            "admin" -> {
                tvListings.text = "Admin Panel"
                ivListings.setImageResource(R.drawable.ic_admin)
            }
            "buyer" -> {
                tvListings.text = "Collection"
                ivListings.setImageResource(R.drawable.ic_collection)
            }
            else -> {
                tvListings.text = "My Listings"
                ivListings.setImageResource(R.drawable.ic_listings)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.filterByCategory(selectedCategory)
    }

    private fun setupRecyclerView() {
        adapter = BookAdapter { book -> openBookDetail(book) }
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.search(query ?: "")
                searchView.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    viewModel.filterByCategory(selectedCategory)
                }
                return true
            }
        })
    }

    private fun setupCategories() {
        categories.forEach { category ->
            val chip = TextView(this).apply {
                text = category
                setPadding(40, 20, 40, 20)
                setBackgroundResource(R.drawable.chip_unselected)
                setTextColor(ContextCompat.getColor(context, R.color.chip_text_unselected))
                textSize = 13f
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 16, 0)
                layoutParams = params
                setOnClickListener { selectCategory(category, this) }
            }
            categoryContainer.addView(chip)
        }
        (categoryContainer.getChildAt(0) as? TextView)?.let { selectCategory("All", it) }
    }

    private fun selectCategory(category: String, chip: TextView) {
        selectedCategory = category

        for (i in 0 until categoryContainer.childCount) {
            val c = categoryContainer.getChildAt(i) as? TextView ?: continue
            c.setBackgroundResource(R.drawable.chip_unselected)
            c.setTextColor(ContextCompat.getColor(this, R.color.chip_text_unselected))
        }

        chip.setBackgroundResource(R.drawable.chip_selected)
        chip.setTextColor(ContextCompat.getColor(this, R.color.chip_text_selected))

        viewModel.filterByCategory(category)
    }

    private fun currentRole() = session.role

    private fun observeViewModel() {
        viewModel.books.observe(this) { books ->
            adapter.submitList(books)
            tvEmpty.visibility = if (books.isEmpty()) View.VISIBLE else View.GONE
            recyclerView.visibility = if (books.isEmpty()) View.GONE else View.VISIBLE
        }
        viewModel.isLoading.observe(this) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }

    private fun openBookDetail(book: Book) {
        val intent = Intent(this, BookDetailActivity::class.java)
        intent.putExtra("book_id", book.id)
        intent.putExtra("book_title", book.title)
        intent.putExtra("book_author", book.author)
        intent.putExtra("book_price", book.price)
        intent.putExtra("book_condition", book.condition)
        intent.putExtra("book_category", book.category)
        intent.putExtra("book_description", book.description)
        intent.putExtra("book_seller", book.sellerUsername)
        intent.putExtra("book_seller_id", book.sellerId)
        startActivity(intent)
    }
}