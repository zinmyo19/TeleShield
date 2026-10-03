package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioStreamEntity
import com.example.data.repository.SubscriptionTier
import com.example.player.PlayerDisplayMode
import com.example.player.PlayerState
import com.example.ui.theme.LocalTeleShieldColors
import com.example.ui.tv.tvFocusable

@Composable
fun AudioPlayerScreen(
    playerState: PlayerState,
    streams: List<AudioStreamEntity>,
    subscriptionTier: SubscriptionTier,
    onSelectStream: (AudioStreamEntity) -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Int) -> Unit,
    onCycleSpeed: () -> Unit,
    onToggleStreamLibraryView: () -> Unit,
    onToggleOfflineVault: () -> Unit,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalTeleShieldColors.current
    val currentStream = playerState.currentStream ?: streams.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Single-click Command Bar: Toggle Between Media Streams and Library Views
        Surface(
            color = colors.surface,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, colors.border.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (playerState.displayMode == PlayerDisplayMode.STREAM_LIVE)
                            "Live Audio Stream" else "Encrypted Channels Library",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                // The requested single-click command button
                Button(
                    onClick = onToggleStreamLibraryView,
                    modifier = Modifier
                        .tvFocusable(
                            shape = RoundedCornerShape(8.dp),
                            onClick = onToggleStreamLibraryView
                        )
                        .testTag("single_click_toggle_stream_library"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary.copy(alpha = 0.18f),
                        contentColor = colors.primary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Toggle View",
                        tint = colors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (playerState.displayMode == PlayerDisplayMode.STREAM_LIVE)
                            "Switch to Library (1-Click)" else "Switch to Live Player (1-Click)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }
            }
        }

        // View Mode: Either LIVE NOW-PLAYING STREAM or LIBRARY VIEW
        if (playerState.displayMode == PlayerDisplayMode.STREAM_LIVE && currentStream != null) {
            LiveStreamPlayerView(
                stream = currentStream,
                playerState = playerState,
                subscriptionTier = subscriptionTier,
                onTogglePlayPause = onTogglePlayPause,
                onSeekTo = onSeekTo,
                onCycleSpeed = onCycleSpeed,
                onToggleOfflineVault = onToggleOfflineVault,
                onUpgradeClick = onUpgradeClick,
                onToggleStreamLibraryView = onToggleStreamLibraryView
            )
        } else {
            StreamLibraryView(
                streams = streams,
                activeStreamId = playerState.currentStream?.id,
                subscriptionTier = subscriptionTier,
                onSelectStream = onSelectStream,
                onUpgradeClick = onUpgradeClick
            )
        }
    }
}

@Composable
fun LiveStreamPlayerView(
    stream: AudioStreamEntity,
    playerState: PlayerState,
    subscriptionTier: SubscriptionTier,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Int) -> Unit,
    onCycleSpeed: () -> Unit,
    onToggleOfflineVault: () -> Unit,
    onUpgradeClick: () -> Unit,
    onToggleStreamLibraryView: () -> Unit
) {
    val colors = LocalTeleShieldColors.current

    val formatSeconds: (Int) -> String = { totalSec ->
        val m = totalSec / 60
        val s = totalSec % 60
        "%02d:%02d".format(m, s)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Artwork & Cyber Waveform Visualizer Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("stream_player_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(stream.artworkColorHex))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.secondary.copy(alpha = 0.18f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "LOSSLESS 320 KBPS • E2E STREAM",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.secondary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${stream.listenersCount} Secure Listeners",
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Animated Cyber Waveform Visualizer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            playerState.waveformAmplitudes.forEachIndexed { index, amp ->
                                val heightMultiplier = if (playerState.isPlaying) amp else 0.2f
                                val barHeight by animateFloatAsState(
                                    targetValue = (heightMultiplier * 70f).coerceAtLeast(6f),
                                    animationSpec = tween(durationMillis = 200),
                                    label = "bar_$index"
                                )

                                Box(
                                    modifier = Modifier
                                        .width(6.dp)
                                        .height(barHeight.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            if (index % 2 == 0) colors.primary else colors.secondary
                                        )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = stream.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stream.channelOrArtist,
                        fontSize = 13.sp,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Time & Slider
                    Slider(
                        value = playerState.currentPositionSeconds.toFloat(),
                        onValueChange = { onSeekTo(it.toInt()) },
                        valueRange = 0f..playerState.durationSeconds.toFloat().coerceAtLeast(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = colors.primary,
                            activeTrackColor = colors.primary,
                            inactiveTrackColor = colors.border
                        ),
                        modifier = Modifier.testTag("stream_player_seekbar")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatSeconds(playerState.currentPositionSeconds),
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                        Text(
                            text = formatSeconds(playerState.durationSeconds),
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Playback Controls Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Speed Cycle Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.surfaceVariant)
                                .tvFocusable(shape = RoundedCornerShape(8.dp), onClick = onCycleSpeed)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("stream_player_speed_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = "Speed",
                                    tint = colors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${playerState.playbackSpeed}x",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                            }
                        }

                        // Main Large Play/Pause Button
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(colors.primary)
                                .tvFocusable(
                                    shape = CircleShape,
                                    onClick = onTogglePlayPause
                                )
                                .testTag("stream_player_main_play_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                                tint = Color.Black,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Offline Vault Cache Toggle
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (playerState.isOfflineCached) colors.secondary.copy(alpha = 0.2f) else colors.surfaceVariant)
                                .tvFocusable(shape = RoundedCornerShape(8.dp), onClick = onToggleOfflineVault)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("stream_player_offline_vault_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (playerState.isOfflineCached) Icons.Default.DownloadDone else Icons.Default.CloudDownload,
                                    contentDescription = "Offline Vault",
                                    tint = if (playerState.isOfflineCached) colors.secondary else colors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (playerState.isOfflineCached) "Cached" else "Offline",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (playerState.isOfflineCached) colors.secondary else colors.textPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Description Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.border)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "STREAM DETAILS & BROADCAST LOGS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stream.description,
                        fontSize = 12.sp,
                        color = colors.textSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StreamLibraryView(
    streams: List<AudioStreamEntity>,
    activeStreamId: Long?,
    subscriptionTier: SubscriptionTier,
    onSelectStream: (AudioStreamEntity) -> Unit,
    onUpgradeClick: () -> Unit
) {
    val colors = LocalTeleShieldColors.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            // Subscription Perks Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUpgradeClick() }
                    .testTag("audio_premium_banner_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.secondary.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.secondary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = colors.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Ad-Free Streams & Lossless FLAC",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Current plan: ${subscriptionTier.title} • Tap for details",
                                fontSize = 11.sp,
                                color = colors.secondary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors.secondary)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "TIERS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "ENCRYPTED BROADCAST FEEDS (${streams.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textMuted,
                letterSpacing = 1.sp
            )
        }

        items(streams, key = { it.id }) { item ->
            val isCurrent = item.id == activeStreamId

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .tvFocusable(
                        shape = RoundedCornerShape(12.dp),
                        onClick = { onSelectStream(item) }
                    )
                    .testTag("stream_item_${item.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) colors.surfaceVariant else colors.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isCurrent) 1.5.dp else 1.dp,
                    color = if (isCurrent) colors.primary else colors.border
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(item.artworkColorHex).copy(alpha = 0.2f))
                            .border(1.dp, Color(item.artworkColorHex), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (item.streamType == "LIVE_STREAM") Icons.Default.Radio else Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = Color(item.artworkColorHex),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            if (item.isPremiumOnly) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(colors.primary.copy(alpha = 0.2f))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "SHIELD+",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.primary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = item.channelOrArtist,
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = colors.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${item.listenersCount} listeners",
                                fontSize = 10.sp,
                                color = colors.textMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
