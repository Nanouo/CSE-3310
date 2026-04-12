package com.reread.app.data

data class Book(
    val id: Int = 0,
    val sellerId: Int,
    val sellerUsername: String = "",
    val title: String,
    val author: String,
    val isbn: String = "",
    val price: Double,
    val condition: String,
    val category: String,
    val subcategory: String = "",
    val description: String = "",
    val imagePath: String = "",
    val isAvailable: Boolean = true,
    val createdAt: String = System.currentTimeMillis().toString()
)