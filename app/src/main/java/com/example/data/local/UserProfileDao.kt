package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for local encrypted user profile persistence.
 */
@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    fun observeUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profiles SET displayName = :name, bio = :bio WHERE id = 1")
    suspend fun updateProfileInfo(name: String, bio: String)

    @Query("UPDATE user_profiles SET isGhostMode = :ghostMode, isScreenshotProtected = :screenshotProtected, isIncognitoKeyboard = :incognitoKeyboard, isTwoFactorEnabled = :twoFactor WHERE id = 1")
    suspend fun updateSecuritySettings(
        ghostMode: Boolean,
        screenshotProtected: Boolean,
        incognitoKeyboard: Boolean,
        twoFactor: Boolean
    )

    @Query("UPDATE user_profiles SET publicIdentityKeyHex = :newKeyHex, lastKeyRotationTimestamp = :timestamp WHERE id = 1")
    suspend fun rotateIdentityKey(newKeyHex: String, timestamp: Long)

    @Query("SELECT COUNT(*) FROM user_profiles")
    suspend fun getProfileCount(): Int
}
