package com.example.data.repository

import com.example.data.local.AudioStreamDao
import com.example.data.local.ChatDao
import com.example.data.local.InsightDao
import com.example.data.local.MessageDao
import com.example.data.model.AudioStreamEntity
import com.example.data.model.ChatEntity
import com.example.data.model.DailyInsightEntity
import com.example.data.model.MessageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class SubscriptionTier(val title: String, val price: String, val badge: String) {
    FREE("Standard Cipher", "Free", "STANDARD"),
    SHIELD_PLUS("TeleShield Shield+", "$3.99/mo", "SHIELD+"),
    ULTRA_PRO("TeleShield Ultra Pro", "$7.99/mo", "ULTRA PRO")
}

data class ActiveSession(
    val id: String,
    val deviceName: String,
    val clientType: String, // "Android TV Box", "Pixel 9 Pro", "TeleShield Desktop", "Linux Terminal"
    val ipAddress: String,
    val location: String,
    val lastActive: String,
    val isCurrentDevice: Boolean
)

data class UserPrivacySettings(
    val screenshotProtection: Boolean = false, // Off by default to allow streaming emulator display
    val ghostMode: Boolean = true, // Hide online & last seen
    val incognitoKeyboard: Boolean = true,
    val forwardRestrictedByDefault: Boolean = true,
    val defaultSelfDestructSeconds: Int = 30, // 0 = off
    val p2pCallsOption: String = "Nobody (Relayed via Shield Nodes)",
    val twoFactorEnabled: Boolean = true,
    val passcodeLocked: Boolean = false,
    val selfDestructAccountMonths: Int = 3,
    val tvRemoteMode: Boolean = false // high visibility focus mode
)

class TeleShieldRepository(
    private val chatDao: ChatDao,
    private val messageDao: MessageDao,
    private val audioStreamDao: AudioStreamDao,
    private val insightDao: InsightDao,
    private val scope: CoroutineScope
) {
    val allChats: Flow<List<ChatEntity>> = chatDao.getAllChats()
    val secretChats: Flow<List<ChatEntity>> = chatDao.getSecretChats()
    val allAudioStreams: Flow<List<AudioStreamEntity>> = audioStreamDao.getAllStreams()
    val latestInsight: Flow<DailyInsightEntity?> = insightDao.getLatestInsight()

    private val _privacySettings = MutableStateFlow(UserPrivacySettings())
    val privacySettings: StateFlow<UserPrivacySettings> = _privacySettings.asStateFlow()

    private val _subscriptionTier = MutableStateFlow(SubscriptionTier.SHIELD_PLUS)
    val subscriptionTier: StateFlow<SubscriptionTier> = _subscriptionTier.asStateFlow()

    private val _activeSessions = MutableStateFlow(
        listOf(
            ActiveSession("s1", "Android TV Box (Living Room)", "TeleShield TV Box", "192.168.1.144 (Encrypted VPN)", "Zurich, Switzerland", "Active now", true),
            ActiveSession("s2", "Pixel 9 Pro", "TeleShield Mobile", "10.8.0.2 (Shield Node)", "Geneva, Switzerland", "5 minutes ago", false),
            ActiveSession("s3", "TeleShield Workstation CLI", "Desktop Encrypted App", "10.8.0.9 (Shield Node)", "Reykjavik, Iceland", "Yesterday at 22:15", false)
        )
    )
    val activeSessions: StateFlow<List<ActiveSession>> = _activeSessions.asStateFlow()

    init {
        scope.launch(Dispatchers.IO) {
            seedInitialDataIfEmpty()
            startMessageBurnMonitor()
        }
    }

    private suspend fun seedInitialDataIfEmpty() {
        if (chatDao.getChatCount() > 0) return

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        insightDao.insertOrUpdate(
            DailyInsightEntity(
                date = today,
                listeningMinutes = 68,
                streamsPlayedCount = 12,
                encryptedMessagesSent = 174,
                trackersBlocked = 34,
                e2eHandshakesVerified = 28,
                securityScore = 98,
                topChannel = "CyberSec Underground Daily",
                secretChatsActive = 3,
                selfDestructsTriggered = 45,
                shareableVerificationToken = "SHIELD-2026-CIPHER-0091"
            )
        )

        val sampleStreams = listOf(
            AudioStreamEntity(
                id = 1,
                title = "CyberSec Underground Radio",
                channelOrArtist = "Anonymous Operations",
                description = "24/7 Encrypted secure stream discussing zero-day vulnerabilities, decentralized networking, and privacy toolchains.",
                streamType = "LIVE_STREAM",
                durationSeconds = 3600,
                bitRateKbps = 320,
                isLossless = true,
                isPremiumOnly = false,
                listenersCount = 3840,
                artworkColorHex = 0xFF00E5FF,
                category = "Live Dispatch"
            ),
            AudioStreamEntity(
                id = 2,
                title = "Shieldcast: Whistleblower Audio Archives",
                channelOrArtist = "Freedom of Press Vault",
                description = "Lossless E2E voice reports from investigative journalists across high-surveillance zones.",
                streamType = "PODCAST",
                durationSeconds = 1420,
                bitRateKbps = 256,
                isLossless = true,
                isPremiumOnly = true,
                listenersCount = 1920,
                artworkColorHex = 0xFF00E676,
                category = "Encrypted Podcast"
            ),
            AudioStreamEntity(
                id = 3,
                title = "Lo-Fi Cipher Beats to Code By",
                channelOrArtist = "DefCon Chill Studio",
                description = "Ambient low-frequency audio stream designed for terminal coding and confidential analysis.",
                streamType = "LIVE_STREAM",
                durationSeconds = 7200,
                bitRateKbps = 320,
                isLossless = true,
                isPremiumOnly = false,
                listenersCount = 5120,
                artworkColorHex = 0xFF7C4DFF,
                category = "Ambient Streams"
            ),
            AudioStreamEntity(
                id = 4,
                title = "Decentralized Mesh & P2P Protocols",
                channelOrArtist = "TeleShield Devs",
                description = "Deep dive into onion routing, metadata scrubbing, and forward secrecy implementations.",
                streamType = "PODCAST",
                durationSeconds = 2100,
                bitRateKbps = 320,
                isLossless = true,
                isPremiumOnly = true,
                listenersCount = 980,
                artworkColorHex = 0xFFFFD600,
                category = "Technical Architecture"
            )
        )
        audioStreamDao.insertStreams(sampleStreams)

        val sampleChats = listOf(
            ChatEntity(
                id = 1,
                title = "Edward Cyber (Secret Chat)",
                handle = "@edward_ghost",
                avatarColorHex = 0xFF00E5FF,
                isSecretChat = true,
                isVerified = true,
                selfDestructTimerSeconds = 30,
                encryptionFingerprint = "🛡️ ⚡ 🔐 👁️",
                encryptionKeyHex = "E8:4A:21:BC:77:90:3A:D4",
                lastMessage = "Burn timer is set to 30s. The payload signature is verified.",
                lastMessageTimestamp = System.currentTimeMillis() - 120_000,
                unreadCount = 1,
                isPinned = true
            ),
            ChatEntity(
                id = 2,
                title = "Satoshi_N (Encrypted)",
                handle = "@nakamoto_vault",
                avatarColorHex = 0xFFFFD600,
                isSecretChat = true,
                isVerified = true,
                selfDestructTimerSeconds = 60,
                encryptionFingerprint = "🗝️ 🌐 ⚡ 💎",
                encryptionKeyHex = "9C:12:F4:A8:3B:55:10:EE",
                lastMessage = "Peer-to-peer verification completed. No central logs exist.",
                lastMessageTimestamp = System.currentTimeMillis() - 600_000,
                unreadCount = 0,
                isPinned = true
            ),
            ChatEntity(
                id = 3,
                title = "CyberSec Intel Dispatch",
                handle = "@shield_intel",
                avatarColorHex = 0xFF00E676,
                isSecretChat = false,
                isVerified = true,
                selfDestructTimerSeconds = 0,
                encryptionFingerprint = "📡 🛡️ 🛰️ 🔒",
                encryptionKeyHex = "33:FA:81:CC:11:45:90:72",
                lastMessage = "New critical broadcast available on Audio Stream Player channel #1.",
                lastMessageTimestamp = System.currentTimeMillis() - 1_800_000,
                unreadCount = 2,
                isPinned = false
            ),
            ChatEntity(
                id = 4,
                title = "Zero-Trace Operatives Group",
                handle = "@zerotrace_ops",
                avatarColorHex = 0xFFFF1744,
                isSecretChat = true,
                isVerified = false,
                selfDestructTimerSeconds = 15,
                encryptionFingerprint = "⚔️ 🛡️ 🕶️ 💣",
                encryptionKeyHex = "F1:A0:5B:3C:99:82:1D:6E",
                lastMessage = "Automatic message shredder active for this room.",
                lastMessageTimestamp = System.currentTimeMillis() - 7_200_000,
                unreadCount = 0,
                isPinned = false
            )
        )
        chatDao.insertChats(sampleChats)

        // Seed initial messages for Edward Cyber
        val edwardMessages = listOf(
            MessageEntity(
                chatId = 1,
                senderName = "Edward Cyber",
                isOutgoing = false,
                content = "End-to-end encrypted secret chat established. Encryption keys exchanged via 4096-bit ECDH.",
                timestamp = System.currentTimeMillis() - 300_000,
                isEncrypted = true,
                selfDestructSeconds = 0,
                mediaType = "SECURITY_ALERT"
            ),
            MessageEntity(
                chatId = 1,
                senderName = "Edward Cyber",
                isOutgoing = false,
                content = "TeleShield guarantees no server storage and zero metadata retention. Screenshot blocking is enforced.",
                timestamp = System.currentTimeMillis() - 240_000,
                isEncrypted = true,
                selfDestructSeconds = 30,
                openedTimestamp = System.currentTimeMillis() - 5_000,
                mediaType = "TEXT"
            ),
            MessageEntity(
                chatId = 1,
                senderName = "You",
                isOutgoing = true,
                content = "Understood. The TV Box D-pad remote controller mode is seamless, allowing rapid navigation with keyboard/remote controls.",
                timestamp = System.currentTimeMillis() - 180_000,
                isEncrypted = true,
                selfDestructSeconds = 30,
                openedTimestamp = System.currentTimeMillis() - 5_000,
                mediaType = "TEXT"
            ),
            MessageEntity(
                chatId = 1,
                senderName = "Edward Cyber",
                isOutgoing = false,
                content = "Burn timer is set to 30s. The payload signature is verified.",
                timestamp = System.currentTimeMillis() - 120_000,
                isEncrypted = true,
                selfDestructSeconds = 30,
                openedTimestamp = System.currentTimeMillis() - 2_000,
                mediaType = "TEXT"
            )
        )
        messageDao.insertMessages(edwardMessages)

        // Seed messages for Satoshi_N
        val satoshiMessages = listOf(
            MessageEntity(
                chatId = 2,
                senderName = "Satoshi_N",
                isOutgoing = false,
                content = "Encrypted session verified. Security Key Fingerprint: 🗝️ 🌐 ⚡ 💎",
                timestamp = System.currentTimeMillis() - 900_000,
                isEncrypted = true,
                selfDestructSeconds = 0,
                mediaType = "SECURITY_ALERT"
            ),
            MessageEntity(
                chatId = 2,
                senderName = "Satoshi_N",
                isOutgoing = false,
                content = "Peer-to-peer verification completed. No central logs exist.",
                timestamp = System.currentTimeMillis() - 600_000,
                isEncrypted = true,
                selfDestructSeconds = 60,
                openedTimestamp = System.currentTimeMillis() - 10_000,
                mediaType = "TEXT"
            )
        )
        messageDao.insertMessages(satoshiMessages)
    }

    private fun startMessageBurnMonitor() {
        scope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(1000)
                val now = System.currentTimeMillis()
                val burningMessages = messageDao.getActiveBurnMessages()
                for (msg in burningMessages) {
                    if (msg.shouldBeDestroyed(now)) {
                        messageDao.markDestroyed(msg.id)
                    }
                }
            }
        }
    }

    fun observeMessages(chatId: Long): Flow<List<MessageEntity>> {
        return messageDao.getMessagesForChat(chatId)
    }

    fun observeChat(chatId: Long): Flow<ChatEntity?> {
        return chatDao.observeChatById(chatId)
    }

    suspend fun sendMessage(chatId: Long, text: String, isSecretChat: Boolean, selfDestructSec: Int) {
        val now = System.currentTimeMillis()
        val message = MessageEntity(
            chatId = chatId,
            senderName = "You",
            isOutgoing = true,
            content = text,
            timestamp = now,
            isEncrypted = isSecretChat,
            selfDestructSeconds = selfDestructSec,
            openedTimestamp = if (selfDestructSec > 0) now else 0L,
            mediaType = "TEXT"
        )
        messageDao.insertMessage(message)
        chatDao.updateLastMessage(chatId, text, now)

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        insightDao.incrementEncryptedMessages(today)
    }

    suspend fun markMessageRead(messageId: Long) {
        messageDao.markMessageOpened(messageId, System.currentTimeMillis())
    }

    suspend fun updateChatSelfDestructTimer(chatId: Long, seconds: Int) {
        chatDao.updateSelfDestructTimer(chatId, seconds)
    }

    suspend fun createNewSecretChat(title: String, handle: String, timerSeconds: Int): Long {
        val emojis = listOf("🛡️", "⚡", "🔐", "👁️", "🗝️", "🌐", "💎", "⚔️", "🛰️", "🚀")
        val fingerprint = emojis.shuffled().take(4).joinToString(" ")
        val randomHex = (1..8).map { "%02X".format((0..255).random()) }.joinToString(":")

        val chat = ChatEntity(
            title = title,
            handle = handle,
            avatarColorHex = 0xFF00E5FF,
            isSecretChat = true,
            isVerified = true,
            selfDestructTimerSeconds = timerSeconds,
            encryptionFingerprint = fingerprint,
            encryptionKeyHex = randomHex,
            lastMessage = "Secret chat initialized. E2E Keys established.",
            lastMessageTimestamp = System.currentTimeMillis(),
            unreadCount = 0
        )
        val newChatId = chatDao.insertChat(chat)

        // Add security banner alert
        messageDao.insertMessage(
            MessageEntity(
                chatId = newChatId,
                senderName = "System Guard",
                isOutgoing = false,
                content = "Encrypted secret chat opened. Diffie-Hellman Key fingerprint: $fingerprint. Self-destruct: ${timerSeconds}s.",
                timestamp = System.currentTimeMillis(),
                isEncrypted = true,
                selfDestructSeconds = 0,
                mediaType = "SECURITY_ALERT"
            )
        )
        return newChatId
    }

    suspend fun addListeningMinutes(minutes: Int) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        insightDao.addListeningMinutes(today, minutes)
    }

    fun updatePrivacySettings(newSettings: UserPrivacySettings) {
        _privacySettings.value = newSettings
    }

    fun updateSubscriptionTier(tier: SubscriptionTier) {
        _subscriptionTier.value = tier
    }

    fun terminateSession(sessionId: String) {
        _activeSessions.value = _activeSessions.value.filter { it.id != sessionId }
    }

    fun terminateAllOtherSessions() {
        _activeSessions.value = _activeSessions.value.filter { it.isCurrentDevice }
    }
}
