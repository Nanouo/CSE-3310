package com.example.cse3310app.data.repository

import com.example.cse3310app.data.local.dao.AdminLogDao
import com.example.cse3310app.data.local.entity.AdminLogEntity

class AdminRepository(
    private val authRepository: AuthRepository,
    private val listingRepository: ListingRepository,
    private val adminLogDao: AdminLogDao
) {
    val users = authRepository.observeUsers()
    val listings = listingRepository.observeAllWithSeller()
    val logs = adminLogDao.observeAll()

    suspend fun disableUser(adminId: Long, userId: Long, disabled: Boolean) {
        authRepository.updateUserDisabled(userId, disabled)
        adminLogDao.insert(
            AdminLogEntity(
                adminId = adminId,
                action = if (disabled) "DISABLE_USER" else "ENABLE_USER",
                targetType = "USER",
                targetId = userId
            )
        )
    }

    suspend fun removeListing(adminId: Long, listingId: Long) {
        listingRepository.deactivateListing(listingId)
        adminLogDao.insert(
            AdminLogEntity(
                adminId = adminId,
                action = "REMOVE_LISTING",
                targetType = "LISTING",
                targetId = listingId
            )
        )
    }
}
