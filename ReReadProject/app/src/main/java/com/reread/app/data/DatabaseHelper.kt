package com.reread.app.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "reread.db"
        const val DATABASE_VERSION = 3

        // Users table
        const val TABLE_USERS = "users"
        const val COL_USER_ID = "id"
        const val COL_USERNAME = "username"
        const val COL_EMAIL = "email"
        const val COL_PASSWORD_HASH = "password_hash"
        const val COL_ROLE = "role"
        const val COL_IS_ACTIVE = "is_active"
        const val COL_CREATED_AT = "created_at"

        // Books table
        const val TABLE_BOOKS = "books"
        const val COL_BOOK_ID = "id"
        const val COL_SELLER_ID = "seller_id"
        const val COL_TITLE = "title"
        const val COL_AUTHOR = "author"
        const val COL_ISBN = "isbn"
        const val COL_PRICE = "price"
        const val COL_CONDITION = "condition"
        const val COL_CATEGORY = "category"
        const val COL_SUBCATEGORY = "subcategory"
        const val COL_DESCRIPTION = "description"
        const val COL_IMAGE_PATH = "image_path"
        const val COL_IS_AVAILABLE = "is_available"
        const val COL_BOOK_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_USERS (
                $COL_USER_ID       INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USERNAME      TEXT    NOT NULL UNIQUE,
                $COL_EMAIL         TEXT    NOT NULL UNIQUE,
                $COL_PASSWORD_HASH TEXT    NOT NULL,
                $COL_ROLE          TEXT    NOT NULL DEFAULT 'buyer',
                $COL_IS_ACTIVE     INTEGER NOT NULL DEFAULT 1,
                $COL_CREATED_AT    TEXT    NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_BOOKS (
                $COL_BOOK_ID         INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_SELLER_ID       INTEGER NOT NULL,
                $COL_TITLE           TEXT    NOT NULL,
                $COL_AUTHOR          TEXT    NOT NULL,
                $COL_ISBN            TEXT    DEFAULT '',
                $COL_PRICE           REAL    NOT NULL,
                $COL_CONDITION       TEXT    NOT NULL,
                $COL_CATEGORY        TEXT    NOT NULL,
                $COL_SUBCATEGORY     TEXT    DEFAULT '',
                $COL_DESCRIPTION     TEXT    DEFAULT '',
                $COL_IMAGE_PATH      TEXT    DEFAULT '',
                $COL_IS_AVAILABLE    INTEGER NOT NULL DEFAULT 1,
                $COL_BOOK_CREATED_AT TEXT    NOT NULL,
                FOREIGN KEY($COL_SELLER_ID) REFERENCES $TABLE_USERS($COL_USER_ID)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE purchased_books (
                id           INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id      INTEGER NOT NULL,
                book_id      INTEGER NOT NULL,
                order_id     TEXT    NOT NULL,
                title        TEXT    NOT NULL,
                author       TEXT    NOT NULL,
                price        REAL    NOT NULL,
                condition    TEXT    NOT NULL,
                purchased_at TEXT    NOT NULL
            )
            """.trimIndent()
        )

        // Seed admin account
        db.execSQL(
            """
            INSERT INTO $TABLE_USERS ($COL_USERNAME, $COL_EMAIL, $COL_PASSWORD_HASH, $COL_ROLE, $COL_IS_ACTIVE, $COL_CREATED_AT)
            VALUES ('admin', 'admin@reread.com', '${PasswordUtils.hash("admin123")}', 'admin', 1, '${System.currentTimeMillis()}')
            """.trimIndent()
        )

        seedSampleBooks(db)
    }

    private fun seedSampleBooks(db: SQLiteDatabase) {
        val now = System.currentTimeMillis().toString()
        val books = listOf(
            arrayOf("Porn", "John Porn", "3", "0.01", "Poor", "Pornography", "Dystopian", "I coomed :("),
            arrayOf("1984", "George Orwell", "9780451524935", "8.99", "Good", "Fiction", "Dystopian", "Classic dystopian novel in good condition."),
            arrayOf("Clean Code", "Robert C. Martin", "9780132350884", "24.99", "Like New", "Academic", "Programming", "Barely used. Great for CS students."),
            arrayOf("Sapiens", "Yuval Noah Harari", "9780062316097", "12.50", "Fair", "Non-Fiction", "History", "Some highlighting but very readable."),
            arrayOf("The Great Gatsby", "F. Scott Fitzgerald", "9780743273565", "6.99", "Good", "Fiction", "Classic", "Perfect for English classes."),
            arrayOf("Atomic Habits", "James Clear", "9780735211292", "15.00", "Like New", "Non-Fiction", "Self-Help", "Read once, excellent condition."),
            arrayOf("Steve Jobs", "Walter Isaacson", "9781451648539", "10.00", "Good", "Biography", "Tech", "Great read, minor wear on cover.")
        )
        books.forEach { b ->
            db.execSQL(
                """INSERT INTO $TABLE_BOOKS ($COL_SELLER_ID,$COL_TITLE,$COL_AUTHOR,$COL_ISBN,$COL_PRICE,$COL_CONDITION,$COL_CATEGORY,$COL_SUBCATEGORY,$COL_DESCRIPTION,$COL_IMAGE_PATH,$COL_IS_AVAILABLE,$COL_BOOK_CREATED_AT)
                   VALUES (1,'${b[0]}','${b[1]}','${b[2]}',${b[3]},'${b[4]}','${b[5]}','${b[6]}','${b[7]}','',1,'$now')"""
            )
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Add books table
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS $TABLE_BOOKS (
                $COL_BOOK_ID         INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_SELLER_ID       INTEGER NOT NULL,
                $COL_TITLE           TEXT    NOT NULL,
                $COL_AUTHOR          TEXT    NOT NULL,
                $COL_ISBN            TEXT    DEFAULT '',
                $COL_PRICE           REAL    NOT NULL,
                $COL_CONDITION       TEXT    NOT NULL,
                $COL_CATEGORY        TEXT    NOT NULL,
                $COL_SUBCATEGORY     TEXT    DEFAULT '',
                $COL_DESCRIPTION     TEXT    DEFAULT '',
                $COL_IMAGE_PATH      TEXT    DEFAULT '',
                $COL_IS_AVAILABLE    INTEGER NOT NULL DEFAULT 1,
                $COL_BOOK_CREATED_AT TEXT    NOT NULL,
                FOREIGN KEY($COL_SELLER_ID) REFERENCES $TABLE_USERS($COL_USER_ID)
            )
            """.trimIndent()
            )
        }
        if (oldVersion < 3) {
            // Add purchased_books table
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS purchased_books (
                id           INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id      INTEGER NOT NULL,
                book_id      INTEGER NOT NULL,
                order_id     TEXT    NOT NULL,
                title        TEXT    NOT NULL,
                author       TEXT    NOT NULL,
                price        REAL    NOT NULL,
                condition    TEXT    NOT NULL,
                purchased_at TEXT    NOT NULL
            )
            """.trimIndent()
            )
        }
    }
}