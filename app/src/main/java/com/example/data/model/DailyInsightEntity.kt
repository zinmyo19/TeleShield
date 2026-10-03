package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_insights")
data class DailyInsightEntity(
    @PrimaryKey
    val date: String, // e.g. "2026-10-03"
    val listeningMinutes: Int = 42,
    val streamsPlayedCount: Int = 7,
    val encryptedMessagesSent: Int = 138,
    val trackersBlocked: Int = 29,
    val e2eHandshakesVerified: Int = 16,
    val securityScore: Int = 96,
    val topChannel: String = "CyberSec Underground Radio",
    val secretChatsActive: Int = 4,
    val selfDestructsTriggered: Int = 23,
    val shareableVerificationToken: String = "SHIELD-2026-E2E-99F7A"
)
