package com.example.cse3310app.ui.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cse3310app.ui.viewmodel.CartViewModel

@Composable
fun CartScreen(userId: Long, cartViewModel: CartViewModel, onCheckout: () -> Unit) {
    val items by cartViewModel.cart(userId).collectAsStateWithLifecycle()
    val total = items.sumOf { it.price * it.cartItem.quantity }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(items) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(item.title, style = MaterialTheme.typography.titleMedium)
                        Text("Qty: ${item.cartItem.quantity} • $${item.price}")
                        Button(onClick = { cartViewModel.removeItem(item.cartItem.cartItemId) }) {
                            Text("Remove")
                        }
                    }
                }
            }
        }
        Text("Total: $${"%.2f".format(total)}", style = MaterialTheme.typography.titleLarge)
        Button(onClick = onCheckout, modifier = Modifier.fillMaxWidth()) {
            Text("Proceed to Checkout")
        }
    }
}

@Composable
fun CheckoutScreen(userId: Long, cartViewModel: CartViewModel, onFinish: () -> Unit) {
    val items by cartViewModel.cart(userId).collectAsStateWithLifecycle()
    val orderId by cartViewModel.lastOrderId.collectAsStateWithLifecycle()
    val error by cartViewModel.checkoutError.collectAsStateWithLifecycle()

    var paymentMethod by remember { mutableStateOf("Card") }
    var cardNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Checkout", style = MaterialTheme.typography.headlineSmall)
        Text("Simulated payment only. No real transaction is performed.")
        OutlinedTextField(
            value = paymentMethod,
            onValueChange = { paymentMethod = it },
            label = { Text("Payment Method") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = cardNumber,
            onValueChange = { cardNumber = it },
            label = { Text("Card Number") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { cartViewModel.checkout(userId, paymentMethod, cardNumber, items) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirm Order")
        }
        if (error != null) Text(error ?: "", color = MaterialTheme.colorScheme.error)
        if (orderId != null) {
            Text("Order #$orderId confirmed.")
            Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) { Text("Back to Home") }
        }
    }
}
