package com.example.cse3310app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cse3310app.data.local.entity.ListingEntity
import com.example.cse3310app.data.local.entity.ListingWithSeller
import kotlinx.coroutines.flow.Flow

@Dao
interface ListingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(listing: ListingEntity): Long

    @Update
    suspend fun update(listing: ListingEntity)

    @Query("UPDATE listings SET isActive = 0 WHERE listingId = :listingId")
    suspend fun softDelete(listingId: Long)

    @Query("SELECT * FROM listings WHERE listingId = :listingId LIMIT 1")
    suspend fun getById(listingId: Long): ListingEntity?

    @Query("SELECT * FROM listings WHERE sellerId = :sellerId ORDER BY createdAt DESC")
    fun observeBySeller(sellerId: Long): Flow<List<ListingEntity>>

    @Query("SELECT * FROM listings WHERE isActive = 1 ORDER BY createdAt DESC")
    fun observeActive(): Flow<List<ListingEntity>>

    @Query(
        """
        SELECT * FROM listings
        WHERE isActive = 1
        AND (
            LOWER(title) LIKE '%' || LOWER(:query) || '%' OR
            LOWER(author) LIKE '%' || LOWER(:query) || '%' OR
            LOWER(isbn) LIKE '%' || LOWER(:query) || '%'
        )
        AND (:category IS NULL OR category = :category)
        AND (:subcategory IS NULL OR subcategory = :subcategory)
        ORDER BY createdAt DESC
        """
    )
    fun search(
        query: String,
        category: String?,
        subcategory: String?
    ): Flow<List<ListingEntity>>

    @Query(
        """
        SELECT l.*, u.username AS sellerUsername
        FROM listings l
        INNER JOIN users u ON l.sellerId = u.userId
        ORDER BY l.createdAt DESC
        """
    )
    fun observeAllWithSeller(): Flow<List<ListingWithSeller>>
}
