package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val chatId: Long,
    val senderName: String,
    val isOutgoing: Boolean,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isEncrypted: Boolean = true,
    val selfDestructSeconds: Int = 0, // 0 = permanent
    val openedTimestamp: Long = 0L,
    val isDestroyed: Boolean = false,
    val mediaType: String = "TEXT", // "TEXT", "AUDIO", "VOICE", "CIPHER_DOC"
    val mediaDurationSec: Int = 0,
    val audioPlayed: Boolean = false,
    val encryptionCipher: String = "AES-256-GCM / Ephemeral Diffie-Hellman"
) {
    fun getRemainingBurnSeconds(currentTime: Long): Int {
        if (selfDestructSeconds <= 0 || openedTimestamp <= 0L) return selfDestructSeconds
        val elapsed = (currentTime - openedTimestamp) / 1000
        val remaining = selfDestructSeconds - elapsed
        return if (remaining > 0) remaining.toInt() else 0
    }

    fun shouldBeDestroyed(currentTime: Long): Boolean {
        if (selfDestructSeconds <= 0 || openedTimestamp <= 0L) return false
        return (currentTime - openedTimestamp) >= (selfDestructSeconds * 1000L)
    }
}
