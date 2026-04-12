package com.reread.app.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class CollectionRepository(context: Context) {

    private val db = DatabaseHelper(context).writableDatabase

    fun savePurchase(userId: Int, orderId: String, items: List<CartItem>) {
        items.forEach { item ->
            val values = ContentValues().apply {
                put("user_id", userId)
                put("book_id", item.bookId)
                put("order_id", orderId)
                put("title", item.title)
                put("author", item.author)
                put("price", item.price)
                put("condition", item.condition)
                put("purchased_at", System.currentTimeMillis().toString())
            }
            db.insert("purchased_books", null, values)
        }
    }

    fun getPurchasedBooks(userId: Int): List<PurchasedBook> {
        val cursor = db.rawQuery(
            "SELECT * FROM purchased_books WHERE user_id = ? ORDER BY purchased_at DESC",
            arrayOf(userId.toString())
        )
        return cursor.use { it.toPurchasedBookList() }
    }

    private fun Cursor.toPurchasedBookList(): List<PurchasedBook> {
        val list = mutableListOf<PurchasedBook>()
        while (moveToNext()) {
            list.add(
                PurchasedBook(
                    id          = getInt(getColumnIndexOrThrow("id")),
                    userId      = getInt(getColumnIndexOrThrow("user_id")),
                    bookId      = getInt(getColumnIndexOrThrow("book_id")),
                    orderId     = getString(getColumnIndexOrThrow("order_id")),
                    title       = getString(getColumnIndexOrThrow("title")),
                    author      = getString(getColumnIndexOrThrow("author")),
                    price       = getDouble(getColumnIndexOrThrow("price")),
                    condition   = getString(getColumnIndexOrThrow("condition")),
                    purchasedAt = getString(getColumnIndexOrThrow("purchased_at"))
                )
            )
        }
        return list
    }
}