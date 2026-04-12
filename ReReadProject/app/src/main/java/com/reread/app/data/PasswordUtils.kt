package com.reread.app.data

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

/**
 * Lightweight password hashing using SHA-256 + a random salt.
 * Format stored in DB:  "<base64_salt>:<base64_hash>"
 *
 * For production, replace with a proper BCrypt library (e.g. jBCrypt).
 */
object PasswordUtils {

    private const val SALT_BYTES = 16

    /** Hash a plain-text password and return a storable string. */
    fun hash(password: String): String {
        val salt = ByteArray(SALT_BYTES).also { SecureRandom().nextBytes(it) }
        val hash = sha256(salt + password.toByteArray())
        val enc = Base64.getEncoder()
        return "${enc.encodeToString(salt)}:${enc.encodeToString(hash)}"
    }

    /** Verify a plain-text password against a stored hash string. */
    fun verify(password: String, stored: String): Boolean {
        return try {
            val parts = stored.split(":")
            if (parts.size != 2) return false
            val dec = Base64.getDecoder()
            val salt = dec.decode(parts[0])
            val expectedHash = dec.decode(parts[1])
            val actualHash = sha256(salt + password.toByteArray())
            expectedHash.contentEquals(actualHash)
        } catch (e: Exception) {
            false
        }
    }

    private fun sha256(input: ByteArray): ByteArray =
        MessageDigest.getInstance("SHA-256").digest(input)
}
