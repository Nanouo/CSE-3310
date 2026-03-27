package com.example.cse3310app.data.repository

import com.example.cse3310app.util.SessionDataStore

class SessionRepository(private val sessionDataStore: SessionDataStore) {
    val session = sessionDataStore.sessionFlow

    suspend fun login(userId: Long) = sessionDataStore.saveSession(userId)

    suspend fun logout() = sessionDataStore.clearSession()
}
