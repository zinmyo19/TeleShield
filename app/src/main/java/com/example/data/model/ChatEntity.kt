package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val handle: String,
    val avatarColorHex: Long = 0xFF00E5FF,
    val isSecretChat: Boolean = false,
    val isVerified: Boolean = false,
    val selfDestructTimerSeconds: Int = 0, // 0 = off, 5, 15, 30, 60, etc.
    val encryptionFingerprint: String = "🔐 🛡️ ⚡ 👁️",
    val encryptionKeyHex: String = "4A:9C:1F:D3:8E:77:2B:09",
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val isScreenshotProtected: Boolean = true,
    val forwardRestricted: Boolean = true
)
