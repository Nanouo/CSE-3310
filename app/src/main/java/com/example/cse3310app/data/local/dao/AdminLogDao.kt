package com.example.cse3310app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cse3310app.data.local.entity.AdminLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdminLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: AdminLogEntity)

    @Query("SELECT * FROM admin_logs ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<AdminLogEntity>>
}
