package com.reread.app.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.reread.app.R
import com.reread.app.ui.home.HomeActivity

class OrderConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)

        val orderId = intent.getStringExtra("order_id") ?: ""
        val total   = intent.getDoubleExtra("order_total", 0.0)
        val name    = intent.getStringExtra("order_name") ?: ""
        val email   = intent.getStringExtra("order_email") ?: ""
        val payment = intent.getStringExtra("order_payment") ?: ""
        val count   = intent.getIntExtra("order_count", 0)

        findViewById<TextView>(R.id.tv_order_id).text       = "Order #${orderId.takeLast(6).uppercase()}"
        findViewById<TextView>(R.id.tv_confirm_name).text   = "Name: $name"
        findViewById<TextView>(R.id.tv_confirm_email).text  = "Email: $email"
        findViewById<TextView>(R.id.tv_confirm_total).text  = "$${String.format("%.2f", total)}"
        findViewById<TextView>(R.id.tv_confirm_items).text  = "$count item${if (count != 1) "s" else ""} purchased"
        findViewById<TextView>(R.id.tv_confirm_payment).text = "Paid via $payment"

        findViewById<Button>(R.id.btn_continue_shopping).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}