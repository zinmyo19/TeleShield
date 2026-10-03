package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.AudioStreamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioStreamDao {
    @Query("SELECT * FROM audio_streams ORDER BY id ASC")
    fun getAllStreams(): Flow<List<AudioStreamEntity>>

    @Query("SELECT * FROM audio_streams WHERE streamType = :type")
    fun getStreamsByType(type: String): Flow<List<AudioStreamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreams(streams: List<AudioStreamEntity>)

    @Query("SELECT * FROM audio_streams WHERE id = :id")
    suspend fun getStreamById(id: Long): AudioStreamEntity?
}
