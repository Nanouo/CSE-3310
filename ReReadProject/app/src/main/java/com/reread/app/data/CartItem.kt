package com.reread.app.data

data class CartItem(
    val id: Int = 0,
    val bookId: Int,
    val title: String,
    val author: String,
    val price: Double,
    val condition: String,
    val sellerUsername: String
)