package com.reread.app.data

data class Message(
    val id: Int = 0,
    val conversationId: Int,
    val senderId: Int,
    val senderUsername: String,
    val content: String,
    val createdAt: String = System.currentTimeMillis().toString()
)

data class Conversation(
    val id: Int = 0,
    val bookId: Int,
    val bookTitle: String,
    val buyerId: Int,
    val buyerUsername: String,
    val sellerId: Int,
    val sellerUsername: String,
    val lastMessage: String = "",
    val lastMessageAt: String = System.currentTimeMillis().toString()
)