package com.reread.app.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.view.View
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
        val layoutCard     = findViewById<View>(R.id.layout_card)
        val layoutApplePay = findViewById<View>(R.id.layout_applepay)
        val layoutGooglePay= findViewById<View>(R.id.layout_googlepay)
        val layoutCashApp  = findViewById<View>(R.id.layout_cashapp)
        tvItemCount.text  = "${items.size} item${if (items.size != 1) "s" else ""}"
        tvOrderTotal.text = "\$${String.format("%.2f", CartManager.getTotal())}"

        btnPlaceOrder.setOnClickListener {
            placeOrder()
        }

        rgPayment.setOnCheckedChangeListener { _, checkedId ->
            layoutCard.visibility = View.GONE
            layoutApplePay.visibility = View.GONE
            layoutGooglePay.visibility = View.GONE
            layoutCashApp.visibility = View.GONE
            when (checkedId) {
                R.id.rb_card -> layoutCard.visibility = View.VISIBLE
                R.id.rb_applepay -> layoutApplePay.visibility = View.VISIBLE
                R.id.rb_googlepay -> layoutGooglePay.visibility = View.VISIBLE
                R.id.rb_cashapp -> layoutCashApp.visibility = View.VISIBLE
            }
        }

        val etCard = findViewById<EditText>(R.id.et_card_number)
        etCard.addTextChangedListener(object : android.text.TextWatcher {
            private var isFormatting = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                if (isFormatting) return
                isFormatting = true
                val digitsOnly = s.toString().replace(" ", "").take(16)
                val formatted = digitsOnly.chunked(4).joinToString(" ")
                etCard.setText(formatted)
                etCard.setSelection(formatted.length)
                isFormatting = false
            }
        })

        val etExpiry = findViewById<EditText>(R.id.et_expiry)
        etExpiry.addTextChangedListener(object : android.text.TextWatcher {
            private var isFormatting = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                if (isFormatting) return
                isFormatting = true
                val digits = s.toString().replace("/", "")
                var result = ""
                if (digits.isNotEmpty()) {
                    // First digit of month
                    val m1 = digits[0]
                    if (m1 > '1') {
                        // If user types 3–9 → force leading 0
                        result = "0$m1"
                    } else {
                        result = m1.toString()
                    }
                    // Second digit of month
                    if (digits.length >= 2) {
                        val month = digits.substring(0, 2).toIntOrNull() ?: 0
                        if (month in 1..12) {
                            result = digits.substring(0, 2)
                        } else {
                            result = "12"
                        }
                    }
                    // Add year
                    if (digits.length > 2) {
                        result += "/" + digits.substring(2).take(2)
                    }
                }
                etExpiry.setText(result)
                etExpiry.setSelection(result.length)
                isFormatting = false
            }
        })

        val etCash = findViewById<EditText>(R.id.et_cash_tag)
        etCash.addTextChangedListener(object : android.text.TextWatcher {
            private var isEditing = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                if (isEditing) return
                isEditing = true
                val cleaned = s.toString()
                    .replace("[^A-Za-z0-9_]".toRegex(), "")
                    .take(19)
                if (cleaned != s.toString()) {
                    etCash.setText(cleaned)
                    etCash.setSelection(cleaned.length)
                }
                isEditing = false
            }
        })

    }

    private fun placeOrder() {
        val fullName = etFullName.text.toString().trim()
        val email    = etEmail.text.toString().trim()
        val address  = etAddress.text.toString().trim()

        if (fullName.isBlank()) { etFullName.error = "Name is required"; return }
        if (email.isBlank()) { etEmail.error = "Email is required"; return }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Enter a valid email"; return
        }
        if (address.isBlank()) { etAddress.error = "Address is required"; return }

        val selectedId = rgPayment.checkedRadioButtonId

        if (selectedId == -1) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
            return
        }
        when (selectedId) {
            R.id.rb_card -> {
                val card = findViewById<EditText>(R.id.et_card_number).text.toString().replace(" ", "")
                val expiry = findViewById<EditText>(R.id.et_expiry).text.toString()
                val cvv = findViewById<EditText>(R.id.et_cvv).text.toString()
                if (card.length != 16) {
                    findViewById<EditText>(R.id.et_card_number).error = "Card must be 16 digits"
                    return
                }
                if (!expiry.matches(Regex("^(0[1-9]|1[0-2])/\\d{2}$"))) {
                    findViewById<EditText>(R.id.et_expiry).error = "Use MM/YY"
                    return
                }
                if (cvv.length != 3) {
                    findViewById<EditText>(R.id.et_cvv).error = "CVV must be 3 digits"
                    return
                }
            }

            R.id.rb_cashapp -> {
                val tag = findViewById<EditText>(R.id.et_cash_tag).text.toString()

                if (!tag.matches(Regex("^[A-Za-z0-9_]{1,20}$"))) {
                    findViewById<EditText>(R.id.et_cash_tag).error =
                        $$"Use format like $Cashtag"
                    return
                }
            }
        }

        val selectedPayment = when (selectedId) {
            R.id.rb_card -> "Credit/Debit Card"
            R.id.rb_applepay -> "Apple Pay"
            R.id.rb_googlepay -> "Google Pay"
            R.id.rb_cashapp -> "Cash App"
            else -> ""
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