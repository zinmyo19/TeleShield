package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.data.local.TeleShieldDatabase
import com.example.data.repository.TeleShieldRepository
import com.example.player.MediaPlayerController
import com.example.ui.components.CollapsibleTeleShieldHeader
import com.example.ui.components.EncryptionFingerprintDialog
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.NewSecretChatDialog
import com.example.ui.components.SelfDestructTimerDialog
import com.example.ui.components.SocialShareInviteDialog
import com.example.ui.components.TeleShieldBottomBar
import com.example.ui.components.TeleShieldNavigationRail
import com.example.ui.screens.AudioPlayerScreen
import com.example.ui.screens.ChatListScreen
import com.example.ui.screens.DailyInsightsScreen
import com.example.ui.screens.PrivacySecurityScreen
import com.example.ui.screens.SecretChatConversationScreen
import com.example.ui.screens.SecretVaultScreen
import com.example.ui.screens.SubscriptionScreen
import com.example.ui.theme.LocalTeleShieldColors
import com.example.ui.theme.TeleShieldTheme
import com.example.viewmodel.MainNavigationTab
import com.example.viewmodel.TeleShieldViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = TeleShieldDatabase.getDatabase(applicationContext)
        val repository = TeleShieldRepository(
            chatDao = database.chatDao(),
            messageDao = database.messageDao(),
            audioStreamDao = database.audioStreamDao(),
            insightDao = database.insightDao(),
            scope = lifecycleScope
        )
        val playerController = MediaPlayerController(lifecycleScope, repository)
        val viewModel = TeleShieldViewModel(repository, playerController)

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val privacySettings by viewModel.privacySettings.collectAsState()

            // Real Android FLAG_SECURE enforcement for Screenshot & Screen Record Protection
            DisposableEffect(privacySettings.screenshotProtection) {
                if (privacySettings.screenshotProtection) {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
                onDispose {}
            }

            TeleShieldTheme(themeMode = themeMode) {
                TeleShieldApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TeleShieldApp(viewModel: TeleShieldViewModel) {
    val colors = LocalTeleShieldColors.current

    val currentTab by viewModel.currentTab.collectAsState()
    val activeChatId by viewModel.activeChatId.collectAsState()
    val activeChat by viewModel.activeChat.collectAsState()
    val activeChatMessages by viewModel.activeChatMessages.collectAsState()
    val isHeaderCollapsed by viewModel.isHeaderCollapsed.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val chatFilter by viewModel.chatFilter.collectAsState()
    val filteredChats by viewModel.filteredChats.collectAsState()
    val allAudioStreams by viewModel.allAudioStreams.collectAsState()
    val playerState by viewModel.playerState.collectAsState()
    val latestInsight by viewModel.latestInsight.collectAsState()
    val privacySettings by viewModel.privacySettings.collectAsState()
    val subscriptionTier by viewModel.subscriptionTier.collectAsState()
    val activeSessions by viewModel.activeSessions.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()

    // Dialog state collectors
    val isFingerprintDialogVisible by viewModel.isFingerprintDialogVisible.collectAsState()
    val isTimerDialogVisible by viewModel.isTimerDialogVisible.collectAsState()
    val isShareInviteDialogVisible by viewModel.isShareInviteDialogVisible.collectAsState()
    val isNewChatDialogVisible by viewModel.isNewChatDialogVisible.collectAsState()

    // Use BoxWithConstraints to support both Phone screens and Android TV Box / Tablet wide screens
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        val isWideScreen = maxWidth >= 700.dp

        Row(modifier = Modifier.fillMaxSize()) {
            // If on a wide display (like TV Box or large tablet), show side Navigation Rail
            if (isWideScreen && activeChatId == null) {
                TeleShieldNavigationRail(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) },
                    modifier = Modifier.statusBarsPadding().navigationBarsPadding()
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // If a conversation is active, show the Secret Chat screen
                if (activeChatId != null && activeChat != null) {
                    SecretChatConversationScreen(
                        chat = activeChat!!,
                        messages = activeChatMessages,
                        onBackClick = { viewModel.closeChat() },
                        onSendMessage = { text -> viewModel.sendMessage(text) },
                        onOpenFingerprintDialog = { viewModel.toggleFingerprintDialog(true) },
                        onOpenTimerDialog = { viewModel.toggleTimerDialog(true) },
                        modifier = Modifier.statusBarsPadding()
                    )
                } else {
                    // Main App Bar: Collapsible Header
                    CollapsibleTeleShieldHeader(
                        isCollapsed = isHeaderCollapsed,
                        onToggleCollapse = { viewModel.toggleHeaderCollapse() },
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        subscriptionTier = subscriptionTier,
                        securityScore = latestInsight?.securityScore ?: 98,
                        activeThemeName = themeMode.displayName,
                        onNewSecretChatClick = { viewModel.toggleNewChatDialog(true) },
                        onShareInviteClick = { viewModel.toggleShareInviteDialog(true) },
                        modifier = Modifier.statusBarsPadding()
                    )

                    // Tab Screen Body
                    Box(modifier = Modifier.weight(1f)) {
                        when (currentTab) {
                            MainNavigationTab.CHATS -> {
                                ChatListScreen(
                                    chats = filteredChats,
                                    activeFilter = chatFilter,
                                    onSelectFilter = { viewModel.setChatFilter(it) },
                                    onChatClick = { chatId -> viewModel.openChat(chatId) },
                                    onNewSecretChatClick = { viewModel.toggleNewChatDialog(true) }
                                )
                            }
                            MainNavigationTab.SECRET_VAULT -> {
                                SecretVaultScreen(
                                    secretChats = filteredChats.filter { it.isSecretChat },
                                    onOpenChat = { chatId -> viewModel.openChat(chatId) },
                                    onNewSecretChatClick = { viewModel.toggleNewChatDialog(true) },
                                    onInspectFingerprint = {
                                        viewModel.openChat(it.id)
                                        viewModel.toggleFingerprintDialog(true)
                                    }
                                )
                            }
                            MainNavigationTab.PLAYER -> {
                                AudioPlayerScreen(
                                    playerState = playerState,
                                    streams = allAudioStreams,
                                    subscriptionTier = subscriptionTier,
                                    onSelectStream = { stream -> viewModel.playAudioStream(stream) },
                                    onTogglePlayPause = { viewModel.togglePlayerPlayback() },
                                    onSeekTo = { sec -> viewModel.playerController.seekTo(sec) },
                                    onCycleSpeed = { viewModel.playerController.cyclePlaybackSpeed() },
                                    onToggleStreamLibraryView = { viewModel.togglePlayerStreamLibraryView() },
                                    onToggleOfflineVault = { viewModel.playerController.toggleOfflineVault() },
                                    onUpgradeClick = { viewModel.selectTab(MainNavigationTab.SUBSCRIPTION) }
                                )
                            }
                            MainNavigationTab.INSIGHTS -> {
                                DailyInsightsScreen(
                                    dailyInsight = latestInsight,
                                    onShareInviteClick = { viewModel.toggleShareInviteDialog(true) }
                                )
                            }
                            MainNavigationTab.PRIVACY -> {
                                PrivacySecurityScreen(
                                    privacySettings = privacySettings,
                                    activeSessions = activeSessions,
                                    currentTheme = themeMode,
                                    onUpdateSettings = { viewModel.updatePrivacySettings(it) },
                                    onSelectTheme = { viewModel.setTheme(it) },
                                    onTerminateSession = { viewModel.terminateSession(it) },
                                    onTerminateAllOtherSessions = { viewModel.terminateAllOtherSessions() },
                                    onOpenTimerDialog = { viewModel.toggleTimerDialog(true) }
                                )
                            }
                            MainNavigationTab.SUBSCRIPTION -> {
                                SubscriptionScreen(
                                    currentTier = subscriptionTier,
                                    onSelectTier = { viewModel.updateSubscription(it) }
                                )
                            }
                        }
                    }

                    // Persistent MiniPlayerBar when stream is selected and not in full player tab
                    if (playerState.currentStream != null && currentTab != MainNavigationTab.PLAYER) {
                        MiniPlayerBar(
                            playerState = playerState,
                            onTogglePlayPause = { viewModel.togglePlayerPlayback() },
                            onToggleStreamLibraryView = { viewModel.togglePlayerStreamLibraryView() },
                            onOpenFullPlayer = { viewModel.selectTab(MainNavigationTab.PLAYER) }
                        )
                    }

                    // Bottom Navigation Bar on handheld portrait screens
                    if (!isWideScreen) {
                        TeleShieldBottomBar(
                            currentTab = currentTab,
                            onTabSelected = { viewModel.selectTab(it) }
                        )
                    }
                }
            }
        }
    }

    // Modal Dialogs
    if (isFingerprintDialogVisible && activeChat != null) {
        EncryptionFingerprintDialog(
            title = activeChat!!.title,
            fingerprint = activeChat!!.encryptionFingerprint,
            keyHex = activeChat!!.encryptionKeyHex,
            onDismiss = { viewModel.toggleFingerprintDialog(false) }
        )
    }

    if (isTimerDialogVisible && activeChat != null) {
        SelfDestructTimerDialog(
            currentSeconds = activeChat!!.selfDestructTimerSeconds,
            onSelectSeconds = { sec -> viewModel.setChatTimer(sec) },
            onDismiss = { viewModel.toggleTimerDialog(false) }
        )
    }

    if (isShareInviteDialogVisible) {
        SocialShareInviteDialog(
            onDismiss = { viewModel.toggleShareInviteDialog(false) }
        )
    }

    if (isNewChatDialogVisible) {
        NewSecretChatDialog(
            onCreateChat = { name, handle, timer ->
                viewModel.createSecretChat(name, handle, timer)
            },
            onDismiss = { viewModel.toggleNewChatDialog(false) }
        )
    }
}
