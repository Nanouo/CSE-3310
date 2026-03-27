package com.example.cse3310app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true), Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val userId: Long = 0,
    val username: String,
    val email: String,
    val passwordHash: String,
    val role: String = "USER",
    val isDisabled: Boolean = false,
    val preferredGenres: String = ""
)

@Entity(
    tableName = "listings",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["sellerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sellerId"), Index("title"), Index("author"), Index("isbn")]
)
data class ListingEntity(
    @PrimaryKey(autoGenerate = true) val listingId: Long = 0,
    val sellerId: Long,
    val title: String,
    val author: String,
    val isbn: String,
    val description: String,
    val price: Double,
    val condition: String,
    val category: String,
    val subcategory: String,
    val imageUri: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ListingEntity::class,
            parentColumns = ["listingId"],
            childColumns = ["listingId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId", "listingId"], unique = true), Index("listingId")]
)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val cartItemId: Long = 0,
    val userId: Long,
    val listingId: Long,
    val quantity: Int = 1
)

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["buyerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("buyerId")]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val orderId: Long = 0,
    val buyerId: Long,
    val totalAmount: Double,
    val paymentMethod: String,
    val status: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["orderId"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ListingEntity::class,
            parentColumns = ["listingId"],
            childColumns = ["listingId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("orderId"), Index("listingId")]
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val orderItemId: Long = 0,
    val orderId: Long,
    val listingId: Long,
    val priceAtPurchase: Double
)

@Entity(
    tableName = "conversations",
    indices = [Index(value = ["buyerId", "sellerId", "listingId"], unique = true)]
)
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true) val conversationId: Long = 0,
    val buyerId: Long,
    val sellerId: Long,
    val listingId: Long,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["conversationId"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("conversationId"), Index("senderId"), Index("receiverId")]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val messageId: Long = 0,
    val conversationId: Long,
    val senderId: Long,
    val receiverId: Long,
    val listingId: Long,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "admin_logs",
    indices = [Index("adminId"), Index("timestamp")]
)
data class AdminLogEntity(
    @PrimaryKey(autoGenerate = true) val logId: Long = 0,
    val adminId: Long,
    val action: String,
    val targetType: String,
    val targetId: Long,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "genre_preferences",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class GenrePreferenceEntity(
    @PrimaryKey(autoGenerate = true) val prefId: Long = 0,
    val userId: Long,
    val genre: String
)
