package com.reread.app.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class AdminRepository(context: Context) {

    private val db = DatabaseHelper(context).writableDatabase

    // ─── Users ───────────────────────────────────────────────────────────────

    fun getAllUsers(): List<User> {
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_USERS} ORDER BY ${DatabaseHelper.COL_CREATED_AT} DESC",
            null
        )
        return cursor.use { it.toUserList() }
    }

    fun setUserActive(userId: Int, isActive: Boolean) {
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_IS_ACTIVE, if (isActive) 1 else 0)
        }
        db.update(
            DatabaseHelper.TABLE_USERS,
            values,
            "${DatabaseHelper.COL_USER_ID} = ?",
            arrayOf(userId.toString())
        )
    }

    fun deleteUser(userId: Int) {
        db.delete(
            DatabaseHelper.TABLE_USERS,
            "${DatabaseHelper.COL_USER_ID} = ?",
            arrayOf(userId.toString())
        )
    }

    // ─── Listings ─────────────────────────────────────────────────────────────

    fun getAllListings(): List<Book> {
        val cursor = db.rawQuery(
            """SELECT b.*, u.${DatabaseHelper.COL_USERNAME} as seller_name
               FROM ${DatabaseHelper.TABLE_BOOKS} b
               JOIN ${DatabaseHelper.TABLE_USERS} u ON b.${DatabaseHelper.COL_SELLER_ID} = u.${DatabaseHelper.COL_USER_ID}
               ORDER BY b.${DatabaseHelper.COL_BOOK_CREATED_AT} DESC""",
            null
        )
        return cursor.use { it.toBookList() }
    }

    fun deleteListing(bookId: Int) {
        db.delete(
            DatabaseHelper.TABLE_BOOKS,
            "${DatabaseHelper.COL_BOOK_ID} = ?",
            arrayOf(bookId.toString())
        )
    }

    // ─── Stats ────────────────────────────────────────────────────────────────

    fun getTotalUsers(): Int {
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_USERS}", null)
        return cursor.use { if (it.moveToFirst()) it.getInt(0) else 0 }
    }

    fun getTotalListings(): Int {
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_BOOKS}", null)
        return cursor.use { if (it.moveToFirst()) it.getInt(0) else 0 }
    }

    fun getTotalOrders(): Int {
        val cursor = db.rawQuery("SELECT COUNT(*) FROM purchased_books", null)
        return cursor.use { if (it.moveToFirst()) it.getInt(0) else 0 }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private fun Cursor.toUserList(): List<User> {
        val list = mutableListOf<User>()
        while (moveToNext()) {
            list.add(
                User(
                    id           = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID)),
                    username     = getString(getColumnIndexOrThrow(DatabaseHelper.COL_USERNAME)),
                    email        = getString(getColumnIndexOrThrow(DatabaseHelper.COL_EMAIL)),
                    passwordHash = getString(getColumnIndexOrThrow(DatabaseHelper.COL_PASSWORD_HASH)),
                    role         = getString(getColumnIndexOrThrow(DatabaseHelper.COL_ROLE)),
                    isActive     = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_IS_ACTIVE)) == 1,
                    createdAt    = getString(getColumnIndexOrThrow(DatabaseHelper.COL_CREATED_AT))
                )
            )
        }
        return list
    }

    private fun Cursor.toBookList(): List<Book> {
        val list = mutableListOf<Book>()
        while (moveToNext()) {
            list.add(
                Book(
                    id             = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_BOOK_ID)),
                    sellerId       = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_SELLER_ID)),
                    sellerUsername = getString(getColumnIndexOrThrow("seller_name")) ?: "",
                    title          = getString(getColumnIndexOrThrow(DatabaseHelper.COL_TITLE)),
                    author         = getString(getColumnIndexOrThrow(DatabaseHelper.COL_AUTHOR)),
                    price          = getDouble(getColumnIndexOrThrow(DatabaseHelper.COL_PRICE)),
                    condition      = getString(getColumnIndexOrThrow(DatabaseHelper.COL_CONDITION)),
                    category       = getString(getColumnIndexOrThrow(DatabaseHelper.COL_CATEGORY)),
                    isAvailable    = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_IS_AVAILABLE)) == 1,
                    createdAt      = getString(getColumnIndexOrThrow(DatabaseHelper.COL_BOOK_CREATED_AT))
                )
            )
        }
        return list
    }
}