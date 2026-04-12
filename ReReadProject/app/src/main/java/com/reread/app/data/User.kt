package com.reread.app.data

data class User(
    val id: Int = 0,
    val username: String,
    val email: String,
    val passwordHash: String = "",
    val role: String = "buyer",   // "buyer" | "seller" | "admin"
    val isActive: Boolean = true,
    val createdAt: String = System.currentTimeMillis().toString()
)
