package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AudioStreamEntity
import com.example.data.model.ChatEntity
import com.example.data.model.DailyInsightEntity
import com.example.data.model.MessageEntity
import com.example.data.repository.ActiveSession
import com.example.data.repository.SubscriptionTier
import com.example.data.repository.TeleShieldRepository
import com.example.data.repository.UserPrivacySettings
import com.example.player.MediaPlayerController
import com.example.player.PlayerDisplayMode
import com.example.player.PlayerState
import com.example.ui.theme.TeleShieldThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainNavigationTab(val label: String) {
    CHATS("Chats"),
    SECRET_VAULT("Secret Vault"),
    PLAYER("Audio Stream"),
    INSIGHTS("Insights"),
    PRIVACY("Privacy & Security"),
    SUBSCRIPTION("Premium")
}

class TeleShieldViewModel(
    private val repository: TeleShieldRepository,
    val playerController: MediaPlayerController
) : ViewModel() {

    private val _currentTab = MutableStateFlow(MainNavigationTab.CHATS)
    val currentTab: StateFlow<MainNavigationTab> = _currentTab.asStateFlow()

    private val _activeChatId = MutableStateFlow<Long?>(null)
    val activeChatId: StateFlow<Long?> = _activeChatId.asStateFlow()

    private val _isHeaderCollapsed = MutableStateFlow(false)
    val isHeaderCollapsed: StateFlow<Boolean> = _isHeaderCollapsed.asStateFlow()

    private val _themeMode = MutableStateFlow(TeleShieldThemeMode.CYBER_SHIELD)
    val themeMode: StateFlow<TeleShieldThemeMode> = _themeMode.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _chatFilter = MutableStateFlow("ALL") // "ALL", "SECRET", "CHANNELS"
    val chatFilter: StateFlow<String> = _chatFilter.asStateFlow()

    // Dialog & Inspection states
    private val _isFingerprintDialogVisible = MutableStateFlow(false)
    val isFingerprintDialogVisible: StateFlow<Boolean> = _isFingerprintDialogVisible.asStateFlow()

    private val _isTimerDialogVisible = MutableStateFlow(false)
    val isTimerDialogVisible: StateFlow<Boolean> = _isTimerDialogVisible.asStateFlow()

    private val _isShareInviteDialogVisible = MutableStateFlow(false)
    val isShareInviteDialogVisible: StateFlow<Boolean> = _isShareInviteDialogVisible.asStateFlow()

    private val _isNewChatDialogVisible = MutableStateFlow(false)
    val isNewChatDialogVisible: StateFlow<Boolean> = _isNewChatDialogVisible.asStateFlow()

    val privacySettings: StateFlow<UserPrivacySettings> = repository.privacySettings
    val subscriptionTier: StateFlow<SubscriptionTier> = repository.subscriptionTier
    val activeSessions: StateFlow<List<ActiveSession>> = repository.activeSessions
    val playerState: StateFlow<PlayerState> = playerController.playerState
    val latestInsight: StateFlow<DailyInsightEntity?> = repository.latestInsight
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allAudioStreams: StateFlow<List<AudioStreamEntity>> = repository.allAudioStreams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredChats: StateFlow<List<ChatEntity>> = combine(
        repository.allChats,
        _searchQuery,
        _chatFilter
    ) { chats, query, filter ->
        chats.filter { chat ->
            val matchesQuery = query.isBlank() ||
                    chat.title.contains(query, ignoreCase = true) ||
                    chat.handle.contains(query, ignoreCase = true) ||
                    chat.lastMessage.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "SECRET" -> chat.isSecretChat
                "CHANNELS" -> !chat.isSecretChat
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeChat: StateFlow<ChatEntity?> = _activeChatId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.observeChat(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeChatMessages: StateFlow<List<MessageEntity>> = _activeChatId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.observeMessages(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: MainNavigationTab) {
        _currentTab.value = tab
    }

    fun openChat(chatId: Long) {
        _activeChatId.value = chatId
    }

    fun closeChat() {
        _activeChatId.value = null
    }

    fun toggleHeaderCollapse() {
        _isHeaderCollapsed.value = !_isHeaderCollapsed.value
    }

    fun setHeaderCollapsed(collapsed: Boolean) {
        _isHeaderCollapsed.value = collapsed
    }

    fun setTheme(theme: TeleShieldThemeMode) {
        _themeMode.value = theme
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setChatFilter(filter: String) {
        _chatFilter.value = filter
    }

    fun sendMessage(text: String) {
        val chatId = _activeChatId.value ?: return
        val chat = activeChat.value ?: return
        if (text.isBlank()) return

        viewModelScope.launch {
            repository.sendMessage(
                chatId = chatId,
                text = text.trim(),
                isSecretChat = chat.isSecretChat,
                selfDestructSec = chat.selfDestructTimerSeconds
            )
        }
    }

    fun setChatTimer(seconds: Int) {
        val chatId = _activeChatId.value ?: return
        viewModelScope.launch {
            repository.updateChatSelfDestructTimer(chatId, seconds)
        }
    }

    fun createSecretChat(title: String, handle: String, timerSeconds: Int) {
        viewModelScope.launch {
            val newId = repository.createNewSecretChat(title, handle, timerSeconds)
            _activeChatId.value = newId
            _isNewChatDialogVisible.value = false
        }
    }

    fun updatePrivacySettings(newSettings: UserPrivacySettings) {
        repository.updatePrivacySettings(newSettings)
    }

    fun updateSubscription(tier: SubscriptionTier) {
        repository.updateSubscriptionTier(tier)
    }

    fun terminateSession(id: String) {
        repository.terminateSession(id)
    }

    fun terminateAllOtherSessions() {
        repository.terminateAllOtherSessions()
    }

    fun toggleFingerprintDialog(show: Boolean) {
        _isFingerprintDialogVisible.value = show
    }

    fun toggleTimerDialog(show: Boolean) {
        _isTimerDialogVisible.value = show
    }

    fun toggleShareInviteDialog(show: Boolean) {
        _isShareInviteDialogVisible.value = show
    }

    fun toggleNewChatDialog(show: Boolean) {
        _isNewChatDialogVisible.value = show
    }

    // Media player commands
    fun playAudioStream(stream: AudioStreamEntity) {
        playerController.playStream(stream)
    }

    fun togglePlayerPlayback() {
        playerController.togglePlayPause()
    }

    fun togglePlayerStreamLibraryView() {
        playerController.toggleStreamLibraryView()
    }
}
