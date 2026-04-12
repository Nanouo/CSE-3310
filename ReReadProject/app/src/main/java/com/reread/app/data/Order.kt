package com.reread.app.data

data class Order(
    val id: String = System.currentTimeMillis().toString(),
    val items: List<CartItem>,
    val total: Double,
    val fullName: String,
    val email: String,
    val address: String,
    val paymentMethod: String,
    val createdAt: String = System.currentTimeMillis().toString()
)