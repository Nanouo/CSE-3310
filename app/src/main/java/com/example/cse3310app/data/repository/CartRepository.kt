package com.example.cse3310app.data.repository

import androidx.room.withTransaction
import com.example.cse3310app.data.local.dao.CartDao
import com.example.cse3310app.data.local.dao.OrderDao
import com.example.cse3310app.data.local.db.AppDatabase
import com.example.cse3310app.data.local.entity.CartItemEntity
import com.example.cse3310app.data.local.entity.CartItemWithListing
import com.example.cse3310app.data.local.entity.OrderEntity
import com.example.cse3310app.data.local.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

class CartRepository(
    private val db: AppDatabase,
    private val cartDao: CartDao,
    private val orderDao: OrderDao
) {
    fun observeCart(userId: Long): Flow<List<CartItemWithListing>> = cartDao.observeCart(userId)

    suspend fun addToCart(userId: Long, listingId: Long) {
        val existing = cartDao.find(userId, listingId)
        if (existing == null) {
            cartDao.insert(CartItemEntity(userId = userId, listingId = listingId, quantity = 1))
        } else {
            cartDao.updateQuantity(existing.cartItemId, existing.quantity + 1)
        }
    }

    suspend fun removeItem(cartItemId: Long) = cartDao.deleteById(cartItemId)

    suspend fun checkout(
        buyerId: Long,
        paymentMethod: String,
        cardNumber: String,
        cartItems: List<CartItemWithListing>
    ): Result<Long> = runCatching {
        require(paymentMethod.isNotBlank()) { "Payment method is required" }
        require(cardNumber.length >= 12) { "Card number is invalid" }
        require(cartItems.isNotEmpty()) { "Cart is empty" }

        val total = cartItems.sumOf { it.price * it.cartItem.quantity }
        db.withTransaction {
            val orderId = orderDao.insertOrder(
                OrderEntity(
                    buyerId = buyerId,
                    totalAmount = total,
                    paymentMethod = paymentMethod,
                    status = "CONFIRMED"
                )
            )
            orderDao.insertItems(
                cartItems.map {
                    OrderItemEntity(
                        orderId = orderId,
                        listingId = it.cartItem.listingId,
                        priceAtPurchase = it.price
                    )
                }
            )
            cartDao.clearForUser(buyerId)
            orderId
        }
    }

    fun observeOrders(buyerId: Long) = orderDao.observeByBuyer(buyerId)
}
