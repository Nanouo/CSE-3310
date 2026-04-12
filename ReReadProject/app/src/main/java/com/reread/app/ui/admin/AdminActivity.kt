package com.reread.app.ui.admin

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reread.app.R
import com.reread.app.data.AdminRepository

class AdminActivity : AppCompatActivity() {

    private lateinit var repo: AdminRepository
    private lateinit var usersAdapter: AdminUsersAdapter
    private lateinit var listingsAdapter: AdminListingsAdapter

    private lateinit var tabUsers: TextView
    private lateinit var tabListings: TextView
    private lateinit var rvUsers: RecyclerView
    private lateinit var rvListings: RecyclerView
    private lateinit var tvStatUsers: TextView
    private lateinit var tvStatListings: TextView
    private lateinit var tvStatOrders: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Admin Panel"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        repo = AdminRepository(this)

        tabUsers       = findViewById(R.id.tab_users)
        tabListings    = findViewById(R.id.tab_listings)
        rvUsers        = findViewById(R.id.rv_users)
        rvListings     = findViewById(R.id.rv_listings)
        tvStatUsers    = findViewById(R.id.tv_stat_users)
        tvStatListings = findViewById(R.id.tv_stat_listings)
        tvStatOrders   = findViewById(R.id.tv_stat_orders)

        loadStats()
        setupUsersTab()
        setupListingsTab()
        showTab("users")

        tabUsers.setOnClickListener { showTab("users") }
        tabListings.setOnClickListener { showTab("listings") }
    }

    private fun loadStats() {
        tvStatUsers.text    = repo.getTotalUsers().toString()
        tvStatListings.text = repo.getTotalListings().toString()
        tvStatOrders.text   = repo.getTotalOrders().toString()
    }

    private fun setupUsersTab() {
        usersAdapter = AdminUsersAdapter(
            users = repo.getAllUsers().toMutableList(),
            onToggleActive = { user ->
                repo.setUserActive(user.id, !user.isActive)
                setupUsersTab()
                loadStats()
            },
            onDelete = { user ->
                repo.deleteUser(user.id)
                setupUsersTab()
                loadStats()
            }
        )
        rvUsers.layoutManager = LinearLayoutManager(this)
        rvUsers.adapter = usersAdapter
    }

    private fun setupListingsTab() {
        listingsAdapter = AdminListingsAdapter(
            listings = repo.getAllListings().toMutableList(),
            onDelete = { book ->
                repo.deleteListing(book.id)
                setupListingsTab()
                loadStats()
            }
        )
        rvListings.layoutManager = LinearLayoutManager(this)
        rvListings.adapter = listingsAdapter
    }

    private fun showTab(tab: String) {
        val isUsers = tab == "users"
        rvUsers.visibility    = if (isUsers) View.VISIBLE else View.GONE
        rvListings.visibility = if (isUsers) View.GONE else View.VISIBLE

        tabUsers.setBackgroundResource(
            if (isUsers) R.drawable.chip_selected else R.drawable.chip_unselected
        )
        tabUsers.setTextColor(
            if (isUsers) getColor(R.color.chip_text_selected) else getColor(R.color.chip_text_unselected)
        )
        tabListings.setBackgroundResource(
            if (isUsers) R.drawable.chip_unselected else R.drawable.chip_selected
        )
        tabListings.setTextColor(
            if (isUsers) getColor(R.color.chip_text_unselected) else getColor(R.color.chip_text_selected)
        )
    }
}