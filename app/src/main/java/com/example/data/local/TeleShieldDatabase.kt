package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AudioStreamEntity
import com.example.data.model.ChatEntity
import com.example.data.model.DailyInsightEntity
import com.example.data.model.MessageEntity

@Database(
    entities = [
        ChatEntity::class,
        MessageEntity::class,
        AudioStreamEntity::class,
        DailyInsightEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TeleShieldDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun audioStreamDao(): AudioStreamDao
    abstract fun insightDao(): InsightDao

    companion object {
        @Volatile
        private var INSTANCE: TeleShieldDatabase? = null

        fun getDatabase(context: Context): TeleShieldDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TeleShieldDatabase::class.java,
                    "teleshield_vault.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
