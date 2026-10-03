package com.example.player

import com.example.data.model.AudioStreamEntity
import com.example.data.repository.TeleShieldRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class PlayerDisplayMode {
    STREAM_LIVE,  // Now Playing large stream view with waveform & cipher stats
    LIBRARY_VIEW  // Media stream library & channels list
}

data class PlayerState(
    val currentStream: AudioStreamEntity? = null,
    val isPlaying: Boolean = false,
    val currentPositionSeconds: Int = 0,
    val durationSeconds: Int = 3600,
    val playbackSpeed: Float = 1.0f,
    val isMuted: Boolean = false,
    val displayMode: PlayerDisplayMode = PlayerDisplayMode.LIBRARY_VIEW,
    val waveformAmplitudes: List<Float> = listOf(0.3f, 0.6f, 0.9f, 0.4f, 0.8f, 0.5f, 0.7f, 1.0f, 0.6f, 0.85f, 0.45f, 0.7f, 0.95f, 0.35f, 0.65f),
    val isOfflineCached: Boolean = false,
    val isLosslessActive: Boolean = true
)

class MediaPlayerController(
    private val scope: CoroutineScope,
    private val repository: TeleShieldRepository
) {
    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private var playbackJob: Job? = null
    private var secondCounter = 0

    fun playStream(stream: AudioStreamEntity) {
        playbackJob?.cancel()
        _playerState.value = _playerState.value.copy(
            currentStream = stream,
            isPlaying = true,
            currentPositionSeconds = 0,
            durationSeconds = stream.durationSeconds,
            displayMode = PlayerDisplayMode.STREAM_LIVE // Seamless switch to player
        )
        startPlaybackTicker()
    }

    fun togglePlayPause() {
        val current = _playerState.value
        if (current.currentStream == null) return

        if (current.isPlaying) {
            playbackJob?.cancel()
            _playerState.value = current.copy(isPlaying = false)
        } else {
            _playerState.value = current.copy(isPlaying = true)
            startPlaybackTicker()
        }
    }

    fun seekTo(seconds: Int) {
        val dur = _playerState.value.durationSeconds
        val clamped = seconds.coerceIn(0, dur)
        _playerState.value = _playerState.value.copy(currentPositionSeconds = clamped)
    }

    fun cyclePlaybackSpeed() {
        val speeds = listOf(1.0f, 1.25f, 1.5f, 2.0f)
        val currentIndex = speeds.indexOf(_playerState.value.playbackSpeed)
        val nextSpeed = speeds[(currentIndex + 1) % speeds.size]
        _playerState.value = _playerState.value.copy(playbackSpeed = nextSpeed)
    }

    /**
     * Single-click command to toggle between media streams live view and library views
     */
    fun toggleStreamLibraryView() {
        val newMode = if (_playerState.value.displayMode == PlayerDisplayMode.STREAM_LIVE) {
            PlayerDisplayMode.LIBRARY_VIEW
        } else {
            PlayerDisplayMode.STREAM_LIVE
        }
        _playerState.value = _playerState.value.copy(displayMode = newMode)
    }

    fun setDisplayMode(mode: PlayerDisplayMode) {
        _playerState.value = _playerState.value.copy(displayMode = mode)
    }

    fun toggleOfflineVault() {
        _playerState.value = _playerState.value.copy(
            isOfflineCached = !_playerState.value.isOfflineCached
        )
    }

    private fun startPlaybackTicker() {
        playbackJob = scope.launch(Dispatchers.Default) {
            while (isActive && _playerState.value.isPlaying) {
                delay(1000)
                val cur = _playerState.value
                val newPos = cur.currentPositionSeconds + 1
                if (newPos >= cur.durationSeconds) {
                    _playerState.value = cur.copy(currentPositionSeconds = 0, isPlaying = false)
                    break
                }
                // Generate dynamic cyber waveform animation while playing
                val newAmps = (0..14).map { (20..100).random() / 100f }
                _playerState.value = cur.copy(
                    currentPositionSeconds = newPos,
                    waveformAmplitudes = newAmps
                )

                // Track listening minutes for daily insights
                secondCounter++
                if (secondCounter >= 60) {
                    secondCounter = 0
                    repository.addListeningMinutes(1)
                }
            }
        }
    }
}
