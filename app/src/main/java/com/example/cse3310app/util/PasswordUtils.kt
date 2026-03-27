package com.example.cse3310app.util

import java.security.MessageDigest
import java.security.SecureRandom

object PasswordUtils {
    private val random = SecureRandom()

    fun hashWithSalt(rawPassword: String): String {
        val saltBytes = ByteArray(16)
        random.nextBytes(saltBytes)
        val salt = saltBytes.toHex()
        val hash = sha256("$salt:$rawPassword")
        return "$salt:$hash"
    }

    fun verify(rawPassword: String, saltedHash: String): Boolean {
        val parts = saltedHash.split(":")
        if (parts.size != 2) return false
        val salt = parts[0]
        val expected = parts[1]
        return sha256("$salt:$rawPassword") == expected
    }

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(input.toByteArray()).toHex()
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
}
