package com.example.cse3310app.data.repository

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.cse3310app.data.local.db.AppDatabase
import com.example.cse3310app.util.SessionDataStore

private val Context.sessionStore by preferencesDataStore(name = "session_store")

class AppContainer(context: Context) {
    private val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "book_market.db"
    ).build()

    private val sessionDataStore = SessionDataStore(context.sessionStore)

    val authRepository = AuthRepository(db.userDao(), db.genrePreferenceDao())
    val listingRepository = ListingRepository(db.listingDao())
    val cartRepository = CartRepository(db, db.cartDao(), db.orderDao())
    val messageRepository = MessageRepository(db.messageDao())
    val sessionRepository = SessionRepository(sessionDataStore)
    val adminRepository = AdminRepository(authRepository, listingRepository, db.adminLogDao())
}
