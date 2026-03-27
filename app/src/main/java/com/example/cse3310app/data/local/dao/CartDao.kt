package com.example.cse3310app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.cse3310app.data.local.entity.CartItemEntity
import com.example.cse3310app.data.local.entity.CartItemWithListing
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItemEntity): Long

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND listingId = :listingId LIMIT 1")
    suspend fun find(userId: Long, listingId: Long): CartItemEntity?

    @Query("UPDATE cart_items SET quantity = :quantity WHERE cartItemId = :cartItemId")
    suspend fun updateQuantity(cartItemId: Long, quantity: Int)

    @Query("DELETE FROM cart_items WHERE cartItemId = :cartItemId")
    suspend fun deleteById(cartItemId: Long)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearForUser(userId: Long)

    @Transaction
    @Query(
        """
        SELECT c.*, l.title AS title, l.author AS author, l.price AS price, l.imageUri AS imageUri
        FROM cart_items c
        INNER JOIN listings l ON c.listingId = l.listingId
        WHERE c.userId = :userId
        """
    )
    fun observeCart(userId: Long): Flow<List<CartItemWithListing>>
}
