package com.example

import com.example.data.model.MessageEntity
import com.example.data.model.UserProfileEntity
import com.example.security.SecurityCryptoEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SecureStorageDatabaseTest {

    @Test
    fun testUserProfileEntityDefaultSecurityConfiguration() {
        val profile = UserProfileEntity()
        assertEquals(1L, profile.id)
        assertEquals("@shield_operator", profile.username)
        assertEquals(98, profile.securityScore)
        assertFalse(profile.isScreenshotProtected)
        assertTrue(profile.isGhostMode)
        assertTrue(profile.isIncognitoKeyboard)
        assertTrue(profile.isTwoFactorEnabled)
        assertEquals(64, profile.publicIdentityKeyHex.length)
    }

    @Test
    fun testSecurityCryptoEngineEncryptionDecryption() {
        val originalText = "Top secret zero-knowledge transmission payload"
        val bundle = SecurityCryptoEngine.encryptPayload(originalText)

        assertNotNull(bundle.ciphertext)
        assertNotNull(bundle.hmac)
        assertTrue(bundle.ciphertext.startsWith("ENC:"))
        assertNotEquals(originalText, bundle.ciphertext)

        val decrypted = SecurityCryptoEngine.decryptPayload(bundle.ciphertext)
        assertEquals(originalText, decrypted)
    }

    @Test
    fun testMessageEntityIntegrityFields() {
        val bundle = SecurityCryptoEngine.encryptPayload("Classified handshake")
        val message = MessageEntity(
            chatId = 1L,
            senderName = "Operator",
            isOutgoing = true,
            content = "Classified handshake",
            encryptedPayload = bundle.ciphertext,
            integrityHmacHex = bundle.hmac,
            selfDestructSeconds = 30
        )

        assertEquals("Classified handshake", message.content)
        assertTrue(message.encryptedPayload.startsWith("ENC:"))
        assertEquals(64, message.integrityHmacHex.length)
        assertEquals(30, message.selfDestructSeconds)
    }

    @Test
    fun testIdentityKeyGeneration() {
        val key1 = SecurityCryptoEngine.generateIdentityKey()
        val key2 = SecurityCryptoEngine.generateIdentityKey()

        assertEquals(64, key1.length)
        assertEquals(64, key2.length)
        assertNotEquals(key1, key2)
    }
}
