package com.example.cse3310app.data.repository

import com.example.cse3310app.data.local.dao.MessageDao
import com.example.cse3310app.data.local.entity.ConversationEntity
import com.example.cse3310app.data.local.entity.MessageEntity

class MessageRepository(private val messageDao: MessageDao) {
    fun observeConversations(userId: Long) = messageDao.observeConversations(userId)

    fun observeMessages(conversationId: Long) = messageDao.observeMessages(conversationId)

    suspend fun sendMessage(
        buyerId: Long,
        sellerId: Long,
        listingId: Long,
        senderId: Long,
        receiverId: Long,
        text: String
    ) {
        require(text.isNotBlank()) { "Message cannot be empty" }

        val existing = messageDao.getConversation(buyerId, sellerId, listingId)
        val conversationId = if (existing == null) {
            messageDao.insertConversation(
                ConversationEntity(buyerId = buyerId, sellerId = sellerId, listingId = listingId)
            )
        } else {
            existing.conversationId
        }

        val now = System.currentTimeMillis()
        messageDao.insertMessage(
            MessageEntity(
                conversationId = conversationId,
                senderId = senderId,
                receiverId = receiverId,
                listingId = listingId,
                text = text,
                timestamp = now
            )
        )
        messageDao.touchConversation(conversationId, now)
    }
}
