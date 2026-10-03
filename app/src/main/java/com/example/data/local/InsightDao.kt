package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DailyInsightEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InsightDao {
    @Query("SELECT * FROM daily_insights WHERE date = :date LIMIT 1")
    fun getInsightForDate(date: String): Flow<DailyInsightEntity?>

    @Query("SELECT * FROM daily_insights ORDER BY date DESC LIMIT 1")
    fun getLatestInsight(): Flow<DailyInsightEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(insight: DailyInsightEntity)

    @Query("UPDATE daily_insights SET listeningMinutes = listeningMinutes + :minutes WHERE date = :date")
    suspend fun addListeningMinutes(date: String, minutes: Int)

    @Query("UPDATE daily_insights SET encryptedMessagesSent = encryptedMessagesSent + 1 WHERE date = :date")
    suspend fun incrementEncryptedMessages(date: String)
}
