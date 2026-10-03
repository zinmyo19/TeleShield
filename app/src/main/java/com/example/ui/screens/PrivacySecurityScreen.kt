package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ActiveSession
import com.example.data.repository.UserPrivacySettings
import com.example.ui.components.SecurityThemeConfigurationSelector
import com.example.ui.theme.LocalTeleShieldColors
import com.example.ui.theme.TeleShieldThemeMode
import com.example.ui.tv.tvFocusable

@Composable
fun PrivacySecurityScreen(
    privacySettings: UserPrivacySettings,
    activeSessions: List<ActiveSession>,
    currentTheme: TeleShieldThemeMode,
    onUpdateSettings: (UserPrivacySettings) -> Unit,
    onSelectTheme: (TeleShieldThemeMode) -> Unit,
    onTerminateSession: (String) -> Unit,
    onTerminateAllOtherSessions: () -> Unit,
    onOpenTimerDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalTeleShieldColors.current
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Theme Selector Section
        item {
            SecurityThemeConfigurationSelector(
                selectedTheme = currentTheme,
                onSelectTheme = onSelectTheme
            )
        }

        // Advanced Privacy Controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.border)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Advanced Privacy & Threat Defense",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Screenshot Protection Toggle (FLAG_SECURE)
                    PrivacyToggleRow(
                        title = "Screenshot & Capture Blocker",
                        subtitle = "Prevents screenshots, screen recorders, and app switcher sniffing (FLAG_SECURE).",
                        checked = privacySettings.screenshotProtection,
                        onCheckedChange = {
                            onUpdateSettings(privacySettings.copy(screenshotProtection = it))
                            Toast.makeText(context, if (it) "Screenshot protection enforced." else "Screenshot protection disabled.", Toast.LENGTH_SHORT).show()
                        },
                        testTag = "toggle_screenshot_protection"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Ghost Mode Toggle
                    PrivacyToggleRow(
                        title = "Ghost Mode (Invisible Status)",
                        subtitle = "Never transmits online presence or last seen timestamps to network.",
                        checked = privacySettings.ghostMode,
                        onCheckedChange = {
                            onUpdateSettings(privacySettings.copy(ghostMode = it))
                        },
                        testTag = "toggle_ghost_mode"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Incognito Keyboard Toggle
                    PrivacyToggleRow(
                        title = "Incognito Keyboard Protocol",
                        subtitle = "Forces IME keyboards to suppress dictionary learning and telemetry.",
                        checked = privacySettings.incognitoKeyboard,
                        onCheckedChange = {
                            onUpdateSettings(privacySettings.copy(incognitoKeyboard = it))
                        },
                        testTag = "toggle_incognito_keyboard"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Biometric / Passcode Lock
                    PrivacyToggleRow(
                        title = "Biometric & Passkey Lock",
                        subtitle = "Require fingerprint/PIN immediately on opening TeleShield.",
                        checked = privacySettings.passcodeLocked,
                        onCheckedChange = {
                            onUpdateSettings(privacySettings.copy(passcodeLocked = it))
                        },
                        testTag = "toggle_passcode_lock"
                    )
                }
            }
        }

        // Active Sessions Manager
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.border)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Devices,
                                contentDescription = null,
                                tint = colors.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Active Encrypted Sessions",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }

                        Button(
                            onClick = onTerminateAllOtherSessions,
                            modifier = Modifier
                                .tvFocusable(shape = RoundedCornerShape(6.dp), onClick = onTerminateAllOtherSessions)
                                .testTag("terminate_all_sessions_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF1744).copy(alpha = 0.2f),
                                contentColor = Color(0xFFFF5252)
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Terminate Others", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    activeSessions.forEach { session ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.surfaceVariant)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (session.clientType.contains("TV")) Icons.Default.Tv else Icons.Default.Devices,
                                    contentDescription = null,
                                    tint = if (session.isCurrentDevice) colors.primary else colors.textMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = session.deviceName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textPrimary
                                        )
                                        if (session.isCurrentDevice) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "THIS DEVICE",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = colors.primary
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${session.location} • ${session.ipAddress}",
                                        fontSize = 10.sp,
                                        color = colors.textSecondary
                                    )
                                    Text(
                                        text = session.lastActive,
                                        fontSize = 9.sp,
                                        color = colors.textMuted
                                    )
                                }
                            }

                            if (!session.isCurrentDevice) {
                                IconButton(
                                    onClick = { onTerminateSession(session.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Block,
                                        contentDescription = "Terminate Session",
                                        tint = Color(0xFFFF5252),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrivacyToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    val colors = LocalTeleShieldColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceVariant)
            .padding(12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = colors.textSecondary
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.primary,
                checkedTrackColor = colors.primary.copy(alpha = 0.3f),
                uncheckedThumbColor = colors.textMuted,
                uncheckedTrackColor = colors.border
            )
        )
    }
}
