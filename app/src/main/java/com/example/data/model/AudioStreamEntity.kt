package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_streams")
data class AudioStreamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val channelOrArtist: String,
    val description: String,
    val streamType: String = "LIVE_STREAM", // "LIVE_STREAM", "PODCAST", "DISPATCH", "VOICE_VAULT"
    val durationSeconds: Int = 180,
    val bitRateKbps: Int = 320,
    val isLossless: Boolean = true,
    val isPremiumOnly: Boolean = false,
    val isEncryptedTransmission: Boolean = true,
    val listenersCount: Int = 1240,
    val artworkColorHex: Long = 0xFF00E5FF,
    val category: String = "Security Dispatch"
)
