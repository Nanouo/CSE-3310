package com.reread.app.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.reread.app.R
import com.reread.app.data.CartManager
import com.reread.app.data.Order

class CheckoutActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etAddress: EditText
    private lateinit var rgPayment: RadioGroup
    private lateinit var tvOrderTotal: TextView
    private lateinit var tvItemCount: TextView
    private lateinit var btnPlaceOrder: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Checkout"
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        etFullName    = findViewById(R.id.et_full_name)
        etEmail       = findViewById(R.id.et_email)
        etAddress     = findViewById(R.id.et_address)
        rgPayment     = findViewById(R.id.rg_payment)
        tvOrderTotal  = findViewById(R.id.tv_order_total)
        tvItemCount   = findViewById(R.id.tv_item_count)
        btnPlaceOrder = findViewById(R.id.btn_place_order)

        val items = CartManager.getItems()
        tvItemCount.text  = "${items.size} item${if (items.size != 1) "s" else ""}"
        tvOrderTotal.text = "\$${String.format("%.2f", CartManager.getTotal())}"

        btnPlaceOrder.setOnClickListener {
            placeOrder()
        }
    }

    private fun placeOrder() {
        val fullName = etFullName.text.toString().trim()
        val email    = etEmail.text.toString().trim()
        val address  = etAddress.text.toString().trim()

        if (fullName.isBlank()) { etFullName.error = "Required"; return }
        if (email.isBlank()) { etEmail.error = "Required"; return }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Enter a valid email"; return
        }
        if (address.isBlank()) { etAddress.error = "Required"; return }

        val selectedPayment = when (rgPayment.checkedRadioButtonId) {
            R.id.rb_card      -> "Credit / Debit Card"
            R.id.rb_applepay  -> "Apple Pay"
            R.id.rb_googlepay -> "Google Pay"
            R.id.rb_cashapp   -> "Cash App"
            else -> {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val order = Order(
            items         = CartManager.getItems(),
            total         = CartManager.getTotal(),
            fullName      = fullName,
            email         = email,
            address       = address,
            paymentMethod = selectedPayment
        )

        // Save to collection
        val collectionRepo = com.reread.app.data.CollectionRepository(this)
        val session = com.reread.app.utils.SessionManager(this)
        collectionRepo.savePurchase(session.userId, order.id, order.items)
        val bookRepo = com.reread.app.data.BookRepository(this)
        order.items.forEach { item ->
            bookRepo.deleteBookById(item.bookId)
        }
        CartManager.clear()

        val intent = Intent(this, OrderConfirmationActivity::class.java)
        intent.putExtra("order_id", order.id)
        intent.putExtra("order_total", order.total)
        intent.putExtra("order_name", order.fullName)
        intent.putExtra("order_email", order.email)
        intent.putExtra("order_payment", order.paymentMethod)
        intent.putExtra("order_count", order.items.size)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}