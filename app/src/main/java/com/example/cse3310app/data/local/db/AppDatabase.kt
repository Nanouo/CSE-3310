package com.example.cse3310app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cse3310app.data.local.dao.AdminLogDao
import com.example.cse3310app.data.local.dao.CartDao
import com.example.cse3310app.data.local.dao.GenrePreferenceDao
import com.example.cse3310app.data.local.dao.ListingDao
import com.example.cse3310app.data.local.dao.MessageDao
import com.example.cse3310app.data.local.dao.OrderDao
import com.example.cse3310app.data.local.dao.UserDao
import com.example.cse3310app.data.local.entity.AdminLogEntity
import com.example.cse3310app.data.local.entity.CartItemEntity
import com.example.cse3310app.data.local.entity.ConversationEntity
import com.example.cse3310app.data.local.entity.GenrePreferenceEntity
import com.example.cse3310app.data.local.entity.ListingEntity
import com.example.cse3310app.data.local.entity.MessageEntity
import com.example.cse3310app.data.local.entity.OrderEntity
import com.example.cse3310app.data.local.entity.OrderItemEntity
import com.example.cse3310app.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ListingEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        MessageEntity::class,
        ConversationEntity::class,
        AdminLogEntity::class,
        GenrePreferenceEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun listingDao(): ListingDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun messageDao(): MessageDao
    abstract fun adminLogDao(): AdminLogDao
    abstract fun genrePreferenceDao(): GenrePreferenceDao
}
