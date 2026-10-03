package com.example

import com.example.data.model.MessageEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TeleShieldRepositoryTest {

    @Test
    fun testSelfDestructCountdown() {
        val now = 100_000L
        val message = MessageEntity(
            id = 1,
            chatId = 1,
            senderName = "You",
            isOutgoing = true,
            content = "Self destructing in 30 seconds",
            timestamp = now,
            isEncrypted = true,
            selfDestructSeconds = 30,
            openedTimestamp = now
        )

        // At 10 seconds later, 20 seconds remaining
        val remainingAt10s = message.getRemainingBurnSeconds(now + 10_000L)
        assertEquals(20, remainingAt10s)
        assertFalse(message.shouldBeDestroyed(now + 10_000L))

        // At 30 seconds later, should be destroyed
        val remainingAt30s = message.getRemainingBurnSeconds(now + 30_000L)
        assertEquals(0, remainingAt30s)
        assertTrue(message.shouldBeDestroyed(now + 30_000L))
    }

    @Test
    fun testZeroTimerDoesNotDestroy() {
        val now = 100_000L
        val permanentMessage = MessageEntity(
            id = 2,
            chatId = 1,
            senderName = "Edward Cyber",
            isOutgoing = false,
            content = "Permanent notice",
            timestamp = now,
            isEncrypted = true,
            selfDestructSeconds = 0,
            openedTimestamp = now
        )

        assertFalse(permanentMessage.shouldBeDestroyed(now + 100_000_000L))
    }
}
