package com.example.cse3310app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cse3310app.data.local.entity.GenrePreferenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GenrePreferenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(preferences: List<GenrePreferenceEntity>)

    @Query("DELETE FROM genre_preferences WHERE userId = :userId")
    suspend fun deleteForUser(userId: Long)

    @Query("SELECT * FROM genre_preferences WHERE userId = :userId")
    fun observeForUser(userId: Long): Flow<List<GenrePreferenceEntity>>
}
