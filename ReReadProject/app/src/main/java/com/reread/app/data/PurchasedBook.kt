package com.reread.app.data

data class PurchasedBook(
    val id: Int = 0,
    val userId: Int,
    val bookId: Int,
    val orderId: String,
    val title: String,
    val author: String,
    val price: Double,
    val condition: String,
    val purchasedAt: String = System.currentTimeMillis().toString()
)