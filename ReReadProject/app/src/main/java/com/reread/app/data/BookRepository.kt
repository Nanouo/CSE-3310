package com.reread.app.data

import com.reread.app.data.DatabaseHelper
import android.content.Context
import android.database.Cursor


class BookRepository(private val context: Context) {

    private val db = DatabaseHelper(context).readableDatabase

    fun getAllBooks(): List<Book> {
        val cursor = db.rawQuery(
            """SELECT b.*, u.${DatabaseHelper.COL_USERNAME} as seller_name
               FROM ${DatabaseHelper.TABLE_BOOKS} b
               JOIN ${DatabaseHelper.TABLE_USERS} u ON b.${DatabaseHelper.COL_SELLER_ID} = u.${DatabaseHelper.COL_USER_ID}
               WHERE b.${DatabaseHelper.COL_IS_AVAILABLE} = 1
               ORDER BY b.${DatabaseHelper.COL_BOOK_CREATED_AT} DESC""",
            null
        )
        return cursor.use { it.toBookList() }
    }

    fun searchBooks(query: String): List<Book> {
        val like = "%$query%"
        val cursor = db.rawQuery(
            """SELECT b.*, u.${DatabaseHelper.COL_USERNAME} as seller_name
               FROM ${DatabaseHelper.TABLE_BOOKS} b
               JOIN ${DatabaseHelper.TABLE_USERS} u ON b.${DatabaseHelper.COL_SELLER_ID} = u.${DatabaseHelper.COL_USER_ID}
               WHERE b.${DatabaseHelper.COL_IS_AVAILABLE} = 1
               AND (b.${DatabaseHelper.COL_TITLE} LIKE ? OR b.${DatabaseHelper.COL_AUTHOR} LIKE ? OR b.${DatabaseHelper.COL_ISBN} LIKE ?)
               ORDER BY b.${DatabaseHelper.COL_BOOK_CREATED_AT} DESC""",
            arrayOf(like, like, like)
        )
        return cursor.use { it.toBookList() }
    }

    fun getBooksByCategory(category: String): List<Book> {
        val cursor = db.rawQuery(
            """SELECT b.*, u.${DatabaseHelper.COL_USERNAME} as seller_name
               FROM ${DatabaseHelper.TABLE_BOOKS} b
               JOIN ${DatabaseHelper.TABLE_USERS} u ON b.${DatabaseHelper.COL_SELLER_ID} = u.${DatabaseHelper.COL_USER_ID}
               WHERE b.${DatabaseHelper.COL_IS_AVAILABLE} = 1
               AND b.${DatabaseHelper.COL_CATEGORY} = ?
               ORDER BY b.${DatabaseHelper.COL_BOOK_CREATED_AT} DESC""",
            arrayOf(category)
        )
        return cursor.use { it.toBookList() }
    }

    fun getBooksBySeller(sellerId: Int): List<Book> {
        val cursor = db.rawQuery(
            """SELECT b.*, u.${DatabaseHelper.COL_USERNAME} as seller_name
               FROM ${DatabaseHelper.TABLE_BOOKS} b
               JOIN ${DatabaseHelper.TABLE_USERS} u ON b.${DatabaseHelper.COL_SELLER_ID} = u.${DatabaseHelper.COL_USER_ID}
               WHERE b.${DatabaseHelper.COL_SELLER_ID} = ?
               ORDER BY b.${DatabaseHelper.COL_BOOK_CREATED_AT} DESC""",
            arrayOf(sellerId.toString())
        )
        return cursor.use { it.toBookList() }
    }

    private fun Cursor.toBookList(): List<Book> {
        val list = mutableListOf<Book>()
        while (moveToNext()) {
            list.add(
                Book(
                    id = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_BOOK_ID)),
                    sellerId = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_SELLER_ID)),
                    sellerUsername = getString(getColumnIndexOrThrow("seller_name")) ?: "",
                    title = getString(getColumnIndexOrThrow(DatabaseHelper.COL_TITLE)),
                    author = getString(getColumnIndexOrThrow(DatabaseHelper.COL_AUTHOR)),
                    isbn = getString(getColumnIndexOrThrow(DatabaseHelper.COL_ISBN)) ?: "",
                    price = getDouble(getColumnIndexOrThrow(DatabaseHelper.COL_PRICE)),
                    condition = getString(getColumnIndexOrThrow(DatabaseHelper.COL_CONDITION)),
                    category = getString(getColumnIndexOrThrow(DatabaseHelper.COL_CATEGORY)),
                    subcategory = getString(getColumnIndexOrThrow(DatabaseHelper.COL_SUBCATEGORY)) ?: "",
                    description = getString(getColumnIndexOrThrow(DatabaseHelper.COL_DESCRIPTION)) ?: "",
                    imagePath = getString(getColumnIndexOrThrow(DatabaseHelper.COL_IMAGE_PATH)) ?: "",
                    isAvailable = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_IS_AVAILABLE)) == 1,
                    createdAt = getString(getColumnIndexOrThrow(DatabaseHelper.COL_BOOK_CREATED_AT))
                )
            )
        }
        return list
    }
    fun deleteBookById(bookId: Int): Boolean {
        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete(
            DatabaseHelper.TABLE_BOOKS,
            "${DatabaseHelper.COL_BOOK_ID} = ?",
            arrayOf(bookId.toString())
        )
        db.close()
        return rowsDeleted > 0
    }
}