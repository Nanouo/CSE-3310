package com.example.cse3310app.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class SessionDataStore(private val dataStore: DataStore<Preferences>) {
    companion object {
        val USER_ID = longPreferencesKey("session_user_id")
        val IS_LOGGED_IN = booleanPreferencesKey("session_is_logged_in")
    }

    val sessionFlow: Flow<Pair<Boolean, Long?>> = dataStore.data
        .catch { ex ->
            if (ex is IOException) emit(emptyPreferences()) else throw ex
        }
        .map { pref -> pref[IS_LOGGED_IN] == true to pref[USER_ID] }

    suspend fun saveSession(userId: Long) {
        dataStore.edit { pref ->
            pref[IS_LOGGED_IN] = true
            pref[USER_ID] = userId
        }
    }

    suspend fun clearSession() {
        dataStore.edit { pref ->
            pref[IS_LOGGED_IN] = false
            pref.remove(USER_ID)
        }
    }
}
