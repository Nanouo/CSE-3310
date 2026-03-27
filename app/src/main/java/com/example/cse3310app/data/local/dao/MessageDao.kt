package com.example.cse3310app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cse3310app.data.local.entity.ConversationEntity
import com.example.cse3310app.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity): Long

    @Query(
        """
        SELECT * FROM conversations
        WHERE buyerId = :userId OR sellerId = :userId
        ORDER BY lastUpdated DESC
        """
    )
    fun observeConversations(userId: Long): Flow<List<ConversationEntity>>

    @Query(
        """
        SELECT * FROM conversations
        WHERE buyerId = :buyerId AND sellerId = :sellerId AND listingId = :listingId
        LIMIT 1
        """
    )
    suspend fun getConversation(buyerId: Long, sellerId: Long, listingId: Long): ConversationEntity?

    @Query("UPDATE conversations SET lastUpdated = :timestamp WHERE conversationId = :conversationId")
    suspend fun touchConversation(conversationId: Long, timestamp: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun observeMessages(conversationId: Long): Flow<List<MessageEntity>>
}
