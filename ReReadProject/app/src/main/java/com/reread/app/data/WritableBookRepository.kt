package com.reread.app.data

import android.content.ContentValues
import android.content.Context

class WritableBookRepository(context: Context) {

    private val db = DatabaseHelper(context).writableDatabase

    fun addBook(
        sellerId: Int,
        title: String,
        author: String,
        isbn: String,
        price: Double,
        condition: String,
        category: String,
        description: String,
        imagePath: String
    ): Long {
        val values = ContentValues().apply {
            put(COL_SELLER_ID, sellerId)
            put(COL_TITLE, title)
            put(COL_AUTHOR, author)
            put(COL_ISBN, isbn)
            put(COL_PRICE, price)
            put(COL_CONDITION, condition)
            put(COL_CATEGORY, category)
            put(COL_SUBCATEGORY, "")
            put(COL_DESCRIPTION, description)
            put(COL_IMAGE_PATH, imagePath)
            put(COL_IS_AVAILABLE, 1)
            put(COL_BOOK_CREATED_AT, System.currentTimeMillis().toString())
        }
        return db.insert(DatabaseHelper.TABLE_BOOKS, null, values)
    }

    fun updateBook(
        bookId: Int,
        title: String,
        author: String,
        isbn: String,
        price: Double,
        condition: String,
        category: String,
        description: String,
        imagePath: String
    ) {
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_TITLE, title)
            put(DatabaseHelper.COL_AUTHOR, author)
            put(DatabaseHelper.COL_ISBN, isbn)
            put(DatabaseHelper.COL_PRICE, price)
            put(DatabaseHelper.COL_CONDITION, condition)
            put(DatabaseHelper.COL_CATEGORY, category)
            put(DatabaseHelper.COL_DESCRIPTION, description)
            put(DatabaseHelper.COL_IMAGE_PATH, imagePath)
        }
        db.update(
            DatabaseHelper.TABLE_BOOKS,
            values,
            "${DatabaseHelper.COL_BOOK_ID} = ?",
            arrayOf(bookId.toString())
        )
    }

    fun deleteBook(bookId: Int) {
        db.delete(
            DatabaseHelper.TABLE_BOOKS,
            "${DatabaseHelper.COL_BOOK_ID} = ?",
            arrayOf(bookId.toString())
        )
    }

    private val COL_SELLER_ID     = DatabaseHelper.COL_SELLER_ID
    private val COL_TITLE         = DatabaseHelper.COL_TITLE
    private val COL_AUTHOR        = DatabaseHelper.COL_AUTHOR
    private val COL_ISBN          = DatabaseHelper.COL_ISBN
    private val COL_PRICE         = DatabaseHelper.COL_PRICE
    private val COL_CONDITION     = DatabaseHelper.COL_CONDITION
    private val COL_CATEGORY      = DatabaseHelper.COL_CATEGORY
    private val COL_SUBCATEGORY   = DatabaseHelper.COL_SUBCATEGORY
    private val COL_DESCRIPTION   = DatabaseHelper.COL_DESCRIPTION
    private val COL_IMAGE_PATH    = DatabaseHelper.COL_IMAGE_PATH
    private val COL_IS_AVAILABLE  = DatabaseHelper.COL_IS_AVAILABLE
    private val COL_BOOK_CREATED_AT = DatabaseHelper.COL_BOOK_CREATED_AT
}