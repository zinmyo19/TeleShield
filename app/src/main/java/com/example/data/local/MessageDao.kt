package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE chatId = :chatId AND isDestroyed = 0 ORDER BY timestamp ASC")
    fun getMessagesForChat(chatId: Long): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Update
    suspend fun updateMessage(message: MessageEntity)

    @Query("UPDATE messages SET openedTimestamp = :openedTime WHERE id = :messageId AND openedTimestamp = 0")
    suspend fun markMessageOpened(messageId: Long, openedTime: Long)

    @Query("UPDATE messages SET isDestroyed = 1 WHERE id = :messageId")
    suspend fun markDestroyed(messageId: Long)

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun purgeMessage(messageId: Long)

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun clearChatMessages(chatId: Long)

    @Query("SELECT * FROM messages WHERE selfDestructSeconds > 0 AND openedTimestamp > 0 AND isDestroyed = 0")
    suspend fun getActiveBurnMessages(): List<MessageEntity>
}
