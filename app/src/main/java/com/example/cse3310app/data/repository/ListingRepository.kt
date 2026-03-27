package com.example.cse3310app.data.repository

import com.example.cse3310app.data.local.dao.ListingDao
import com.example.cse3310app.data.local.entity.ListingEntity
import kotlinx.coroutines.flow.Flow

class ListingRepository(private val listingDao: ListingDao) {
    fun observeActiveListings(): Flow<List<ListingEntity>> = listingDao.observeActive()

    fun observeMyListings(userId: Long): Flow<List<ListingEntity>> = listingDao.observeBySeller(userId)

    fun search(query: String, category: String?, subcategory: String?): Flow<List<ListingEntity>> {
        return listingDao.search(query, category, subcategory)
    }

    suspend fun createListing(listing: ListingEntity): Long = listingDao.insert(listing)

    suspend fun updateListing(listing: ListingEntity) = listingDao.update(listing)

    suspend fun deactivateListing(listingId: Long) = listingDao.softDelete(listingId)

    suspend fun getListing(listingId: Long): ListingEntity? = listingDao.getById(listingId)

    fun observeAllWithSeller() = listingDao.observeAllWithSeller()
}
