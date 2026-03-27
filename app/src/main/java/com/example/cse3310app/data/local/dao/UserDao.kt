package com.example.cse3310app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cse3310app.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Update
    suspend fun update(user: UserEntity)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    suspend fun getById(userId: Long): UserEntity?

    @Query("SELECT * FROM users ORDER BY username ASC")
    fun observeAll(): Flow<List<UserEntity>>

    @Query("UPDATE users SET isDisabled = :disabled WHERE userId = :userId")
    suspend fun setDisabled(userId: Long, disabled: Boolean)
}
