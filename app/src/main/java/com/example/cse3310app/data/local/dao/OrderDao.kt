package com.example.cse3310app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.cse3310app.data.local.entity.OrderEntity
import com.example.cse3310app.data.local.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<OrderItemEntity>)

    @Transaction
    suspend fun insertOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>): Long {
        val orderId = insertOrder(order)
        val prepared = items.map { it.copy(orderId = orderId) }
        insertItems(prepared)
        return orderId
    }

    @Query("SELECT * FROM orders WHERE buyerId = :buyerId ORDER BY createdAt DESC")
    fun observeByBuyer(buyerId: Long): Flow<List<OrderEntity>>
}
