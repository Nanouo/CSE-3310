package com.reread.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.reread.app.R
import com.reread.app.data.Book
import com.reread.app.ui.account.AccountBottomSheet
import com.reread.app.ui.admin.AdminBottomSheet
import com.reread.app.ui.collection.CollectionBottomSheet
import com.reread.app.ui.listings.MyListingsBottomSheet
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
    private lateinit var bottomNav: BottomNavigationView

    private val categories = listOf("All", "Academic", "Fiction", "Non-Fiction", "Biography", "Documentary")
    private var accountSheet: AccountBottomSheet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        session = SessionManager(this)
        recyclerView      = findViewById(R.id.rv_books)
        progressBar       = findViewById(R.id.progress_bar)
        tvEmpty           = findViewById(R.id.tv_empty)
        searchView        = findViewById(R.id.search_view)
        categoryContainer = findViewById(R.id.category_container)
        bottomNav         = findViewById(R.id.bottom_navigation)

        findViewById<android.widget.ImageButton>(R.id.btn_cart).setOnClickListener {
            startActivity(Intent(this, com.reread.app.ui.cart.CartActivity::class.java))
        }

        setupRecyclerView()
        setupSearch()
        setupCategories()
        setupBottomNav()
        observeViewModel()
        SessionManager.setRoleChangeListener {
            runOnUiThread {
                updateBottomNav()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        updateBottomNav()
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
                if (newText.isNullOrBlank()) viewModel.loadAllBooks()
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
    private fun updateBottomNav() {
        val menu = bottomNav.menu
        menu.clear()

        val role = session.role

        menu.add(0, R.id.nav_home, 0, "Home")
            .setIcon(R.drawable.ic_home)

        when (role) {
            "admin" -> menu.add(0, R.id.nav_listings, 1, "Admin Panel")
                .setIcon(R.drawable.ic_admin)

            "buyer" -> menu.add(0, R.id.nav_listings, 1, "Collection")
                .setIcon(R.drawable.ic_collection)

            else -> menu.add(0, R.id.nav_listings, 1, "My Listings")
                .setIcon(R.drawable.ic_listings)
        }

        menu.add(0, R.id.nav_account, 2, "Account")
            .setIcon(R.drawable.ic_account)

        bottomNav.selectedItemId = R.id.nav_home
    }
    private fun setupBottomNav() {
        updateBottomNav()
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == bottomNav.selectedItemId) return@setOnItemSelectedListener true
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_listings -> {
                    when (currentRole()) {
                        "admin" -> AdminBottomSheet().show(supportFragmentManager, "admin")
                        "buyer" -> CollectionBottomSheet().show(supportFragmentManager, "collection")
                        else    -> MyListingsBottomSheet().show(supportFragmentManager, "listings")
                    }
                    false
                }
                R.id.nav_account -> {
                    if (accountSheet == null) {
                        accountSheet = AccountBottomSheet()
                    }
                    accountSheet?.show(supportFragmentManager, "account")
                    false
                }
                else -> false
            }
        }
    }
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
        startActivity(intent)
    }
}