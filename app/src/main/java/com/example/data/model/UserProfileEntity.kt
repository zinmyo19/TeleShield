package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local Room Entity for securely persisting the TeleShield operator profile,
 * cryptographic public keys, device fingerprints, and security configurations.
 */
@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey
    val id: Long = 1L,
    val username: String = "@shield_operator",
    val displayName: String = "Cipher Operator",
    val bio: String = "Zero-Knowledge Cryptography • Quantum-Resilient E2E Node",
    val phoneMasked: String = "+1 (•••) •••-9281",
    val publicIdentityKeyHex: String = "3F8A91C2E4B7A5D190C6384FE51280B9AC4D673129845E01827A9B34CD8E1F76",
    val deviceFingerprint: String = "SHIELD-NODE-9904-ALPHA",
    val securityLevel: String = "MILITARY-GRADE (E2E Verified)",
    val securityScore: Int = 98,
    val avatarColorHex: Long = 0xFF00E5FF,
    val isGhostMode: Boolean = true,
    val isScreenshotProtected: Boolean = false,
    val isIncognitoKeyboard: Boolean = true,
    val isTwoFactorEnabled: Boolean = true,
    val accountTier: String = "SHIELD PRO",
    val createdAtTimestamp: Long = System.currentTimeMillis() - (86400000L * 45), // 45 days ago
    val lastKeyRotationTimestamp: Long = System.currentTimeMillis() - (86400000L * 3) // 3 days ago
)
