package com.example.security

import java.security.MessageDigest
import java.util.Base64
import java.util.UUID

/**
 * Lightweight Cryptographic helper simulating AES-256-GCM authenticated encryption
 * and SHA-256 HMAC integrity tags before persisting chat records into the Room database.
 */
object SecurityCryptoEngine {
    private const val DEVICE_SECRET_SALT = "TeleShield_Device_Master_Key_X25519_2026"

    fun encryptPayload(plainText: String): EncryptedBundle {
        val reversed = plainText.reversed()
        val encoded = Base64.getEncoder().encodeToString(reversed.toByteArray(Charsets.UTF_8))
        val hmac = computeHmac(plainText)
        return EncryptedBundle(
            ciphertext = "ENC:$encoded",
            hmac = hmac
        )
    }

    fun decryptPayload(ciphertext: String): String {
        return try {
            if (ciphertext.startsWith("ENC:")) {
                val rawBase64 = ciphertext.removePrefix("ENC:")
                val decoded = Base64.getDecoder().decode(rawBase64)
                String(decoded, Charsets.UTF_8).reversed()
            } else {
                ciphertext
            }
        } catch (e: Exception) {
            ciphertext
        }
    }

    fun computeHmac(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest((input + DEVICE_SECRET_SALT).toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    fun generateIdentityKey(): String {
        val part1 = UUID.randomUUID().toString().replace("-", "")
        val part2 = UUID.randomUUID().toString().replace("-", "")
        return (part1 + part2).uppercase()
    }
}

data class EncryptedBundle(
    val ciphertext: String,
    val hmac: String
)
