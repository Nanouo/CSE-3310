package com.example.cse3310app.data.repository

import com.example.cse3310app.data.local.dao.GenrePreferenceDao
import com.example.cse3310app.data.local.dao.UserDao
import com.example.cse3310app.data.local.entity.GenrePreferenceEntity
import com.example.cse3310app.data.local.entity.UserEntity
import com.example.cse3310app.util.PasswordUtils
import kotlinx.coroutines.flow.Flow

class AuthRepository(
    private val userDao: UserDao,
    private val genrePreferenceDao: GenrePreferenceDao
) {
    fun observeUsers(): Flow<List<UserEntity>> = userDao.observeAll()

    suspend fun register(
        username: String,
        email: String,
        rawPassword: String,
        genres: List<String>
    ): Result<Long> = runCatching {
        val userId = userDao.insert(
            UserEntity(
                username = username,
                email = email,
                passwordHash = PasswordUtils.hashWithSalt(rawPassword),
                preferredGenres = genres.joinToString(",")
            )
        )
        if (genres.isNotEmpty()) {
            genrePreferenceDao.insertAll(genres.map { GenrePreferenceEntity(userId = userId, genre = it) })
        }
        userId
    }

    suspend fun login(username: String, rawPassword: String): Result<UserEntity> = runCatching {
        val user = userDao.getByUsername(username) ?: error("User not found")
        require(!user.isDisabled) { "Account is disabled" }
        require(PasswordUtils.verify(rawPassword, user.passwordHash)) { "Invalid password" }
        user
    }

    suspend fun getUser(userId: Long): UserEntity? = userDao.getById(userId)

    suspend fun updateUserDisabled(userId: Long, disabled: Boolean) {
        userDao.setDisabled(userId, disabled)
    }

    // Seed admin account required by the project spec.
    suspend fun seedAdminIfNeeded() {
        if (userDao.getByUsername("admin") == null) {
            userDao.insert(
                UserEntity(
                    username = "admin",
                    email = "admin@local",
                    passwordHash = PasswordUtils.hashWithSalt("admin123"),
                    role = "ADMIN"
                )
            )
        }
    }
}
