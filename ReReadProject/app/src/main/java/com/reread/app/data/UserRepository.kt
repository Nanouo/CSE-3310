package com.reread.app.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class UserRepository(context: Context) {

    private val db = DatabaseHelper(context).writableDatabase

    // ─── Registration ────────────────────────────────────────────────────────

    sealed class RegisterResult {
        data class Success(val user: User) : RegisterResult()
        object UsernameTaken : RegisterResult()
        object EmailTaken : RegisterResult()
        object DatabaseError : RegisterResult()
    }

    fun register(username: String, email: String, password: String, role: String = "buyer"): RegisterResult {
        if (findByUsername(username) != null) return RegisterResult.UsernameTaken
        if (findByEmail(email) != null) return RegisterResult.EmailTaken

        val values = ContentValues().apply {
            put(DatabaseHelper.COL_USERNAME, username)
            put(DatabaseHelper.COL_EMAIL, email)
            put(DatabaseHelper.COL_PASSWORD_HASH, PasswordUtils.hash(password))
            put(DatabaseHelper.COL_ROLE, role)
            put(DatabaseHelper.COL_IS_ACTIVE, 1)
            put(DatabaseHelper.COL_CREATED_AT, System.currentTimeMillis().toString())
        }

        val id = db.insert(DatabaseHelper.TABLE_USERS, null, values)
        return if (id != -1L) {
            RegisterResult.Success(User(id.toInt(), username, email, role = role))
        } else {
            RegisterResult.DatabaseError
        }
    }

    // ─── Login ───────────────────────────────────────────────────────────────

    sealed class LoginResult {
        data class Success(val user: User) : LoginResult()
        object InvalidCredentials : LoginResult()
        object AccountDisabled : LoginResult()
    }

    fun login(usernameOrEmail: String, password: String): LoginResult {
        val user = findByUsername(usernameOrEmail) ?: findByEmail(usernameOrEmail)
            ?: return LoginResult.InvalidCredentials

        if (!user.isActive) return LoginResult.AccountDisabled

        return if (PasswordUtils.verify(password, user.passwordHash)) {
            LoginResult.Success(user)
        } else {
            LoginResult.InvalidCredentials
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    fun findByUsername(username: String): User? {
        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            null,
            "${DatabaseHelper.COL_USERNAME} = ?",
            arrayOf(username),
            null, null, null
        )
        return cursor.use { it.toUser() }
    }

    fun findByEmail(email: String): User? {
        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            null,
            "${DatabaseHelper.COL_EMAIL} = ?",
            arrayOf(email),
            null, null, null
        )
        return cursor.use { it.toUser() }
    }

    private fun Cursor.toUser(): User? {
        if (!moveToFirst()) return null
        return User(
            id = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID)),
            username = getString(getColumnIndexOrThrow(DatabaseHelper.COL_USERNAME)),
            email = getString(getColumnIndexOrThrow(DatabaseHelper.COL_EMAIL)),
            passwordHash = getString(getColumnIndexOrThrow(DatabaseHelper.COL_PASSWORD_HASH)),
            role = getString(getColumnIndexOrThrow(DatabaseHelper.COL_ROLE)),
            isActive = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_IS_ACTIVE)) == 1,
            createdAt = getString(getColumnIndexOrThrow(DatabaseHelper.COL_CREATED_AT))
        )
    }
}
