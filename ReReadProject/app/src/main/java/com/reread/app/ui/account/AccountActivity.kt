package com.reread.app.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.reread.app.R
import com.reread.app.ui.auth.LoginActivity
import com.reread.app.utils.SessionManager

class AccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val session = SessionManager(this)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        findViewById<TextView>(R.id.tv_username).text = session.username
        findViewById<TextView>(R.id.tv_email).text    = session.email
        findViewById<TextView>(R.id.tv_role).text     = session.role.replaceFirstChar { it.uppercase() }

        val btnBuyer  = findViewById<Button>(R.id.btn_role_buyer)
        val btnSeller = findViewById<Button>(R.id.btn_role_seller)
        val btnAdmin  = findViewById<Button>(R.id.btn_role_admin)

        // Only the admin account can switch to admin role
        if (session.username != "admin") {
            btnAdmin.visibility = View.GONE
        } else {
            btnAdmin.visibility = View.VISIBLE
        }

        updateRoleButtons(btnBuyer, btnSeller, btnAdmin, session.role)

        btnBuyer.setOnClickListener {
            session.setRole("buyer")
            updateRoleButtons(btnBuyer, btnSeller, btnAdmin, "buyer")
            findViewById<TextView>(R.id.tv_role).text = "Buyer"
            restartHome()
        }
        btnSeller.setOnClickListener {
            session.setRole("seller")
            updateRoleButtons(btnBuyer, btnSeller, btnAdmin, "seller")
            findViewById<TextView>(R.id.tv_role).text = "Seller"
            restartHome()
        }
        btnAdmin.setOnClickListener {
            session.setRole("admin")
            updateRoleButtons(btnBuyer, btnSeller, btnAdmin, "admin")
            findViewById<TextView>(R.id.tv_role).text = "Admin"
            restartHome()
        }

        findViewById<Button>(R.id.btn_logout).setOnClickListener {
            session.clear()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun updateRoleButtons(btnBuyer: Button, btnSeller: Button, btnAdmin: Button, currentRole: String) {
        val active       = android.graphics.Color.parseColor("#1A1A1A")
        val inactive     = android.graphics.Color.parseColor("#E0E0E0")
        val activeText   = android.graphics.Color.parseColor("#FFFFFF")
        val inactiveText = android.graphics.Color.parseColor("#1A1A1A")
        listOf(btnBuyer to "buyer", btnSeller to "seller", btnAdmin to "admin").forEach { (btn, role) ->
            btn.backgroundTintList = android.content.res.ColorStateList.valueOf(
                if (role == currentRole) active else inactive
            )
            btn.setTextColor(if (role == currentRole) activeText else inactiveText)
        }
    }

    private fun restartHome() {
        val intent = Intent(this, com.reread.app.ui.home.HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}