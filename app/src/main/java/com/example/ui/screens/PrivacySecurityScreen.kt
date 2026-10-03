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
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfileEntity
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
    userProfile: UserProfileEntity? = null,
    activeMessageCount: Int = 0,
    onUpdateSettings: (UserPrivacySettings) -> Unit,
    onSelectTheme: (TeleShieldThemeMode) -> Unit,
    onUpdateProfile: (String, String) -> Unit = { _, _ -> },
    onRotateKey: () -> Unit = {},
    onTerminateSession: (String) -> Unit,
    onTerminateAllOtherSessions: () -> Unit,
    onOpenTimerDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalTeleShieldColors.current
    val context = LocalContext.current
    var isEditProfileDialogOpen by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Secure Cryptographic Profile (Room Database Persisted)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("secure_user_profile_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, colors.primary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header with Avatar & Tier
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(colors.primary.copy(alpha = 0.2f))
                                    .border(1.5.dp, colors.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = userProfile?.displayName ?: "Cipher Operator",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = userProfile?.username ?: "@shield_operator",
                                    fontSize = 12.sp,
                                    color = colors.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Edit Button
                        IconButton(
                            onClick = { isEditProfileDialogOpen = true },
                            modifier = Modifier
                                .size(36.dp)
                                .tvFocusable(shape = CircleShape, onClick = { isEditProfileDialogOpen = true })
                                .testTag("edit_profile_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = colors.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = userProfile?.bio ?: "Zero-Knowledge Cryptography • Quantum-Resilient E2E Node",
                        fontSize = 12.sp,
                        color = colors.textSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Public Identity Key Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.surfaceVariant)
                            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VpnKey,
                                        contentDescription = null,
                                        tint = colors.secondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "256-BIT PUBLIC IDENTITY KEY",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.secondary
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        onRotateKey()
                                        Toast.makeText(context, "New cryptographic identity key generated and stored.", Toast.LENGTH_SHORT).show()
                                    },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Autorenew,
                                        contentDescription = null,
                                        tint = colors.primary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Rotate Key",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = userProfile?.publicIdentityKeyHex ?: "3F8A91C2E4B7A5D190C6384FE51280B9AC4D673129845E01827A9B34CD8E1F76",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = colors.textMuted,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Room Database Configuration & Storage Metrics
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("room_database_metrics_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = colors.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Room Encrypted Storage Engine",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.secondary.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "SQLITE V3",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = colors.secondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "All chat histories, cryptographic keys, and user profile data are saved locally inside an encrypted Room sandbox. No metadata is shared with servers.",
                        fontSize = 11.sp,
                        color = colors.textSecondary,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Active Messages", fontSize = 10.sp, color = colors.textMuted)
                            Text(
                                text = "$activeMessageCount Secured",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary
                            )
                        }

                        Column {
                            Text(text = "Encryption Protocol", fontSize = 10.sp, color = colors.textMuted)
                            Text(
                                text = "AES-256-GCM / HMAC",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.secondary
                            )
                        }

                        Column {
                            Text(text = "Database Sandbox", fontSize = 10.sp, color = colors.textMuted)
                            Text(
                                text = "teleshield_vault.db",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = colors.textPrimary
                            )
                        }
                    }
                }
            }
        }

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
                                tint = colors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Active Sessions & Devices",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }

                        TextButton(
                            onClick = onTerminateAllOtherSessions,
                            modifier = Modifier.testTag("terminate_all_sessions_button")
                        ) {
                            Text(
                                text = "Terminate Others",
                                fontSize = 11.sp,
                                color = colors.threatAlert,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        activeSessions.forEach { session ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(colors.surfaceVariant)
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = session.deviceName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textPrimary
                                        )
                                        if (session.isCurrentDevice) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(colors.primary.copy(alpha = 0.2f))
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    text = "CURRENT",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = colors.primary
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "${session.ipAddress} • ${session.location}",
                                        fontSize = 10.sp,
                                        color = colors.textMuted
                                    )
                                    Text(
                                        text = session.lastActive,
                                        fontSize = 10.sp,
                                        color = colors.textSecondary
                                    )
                                }

                                if (!session.isCurrentDevice) {
                                    IconButton(
                                        onClick = { onTerminateSession(session.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Terminate Session",
                                            tint = colors.threatAlert,
                                            modifier = Modifier.size(18.dp)
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

    // Edit Profile AlertDialog
    if (isEditProfileDialogOpen) {
        var editedName by remember { mutableStateOf(userProfile?.displayName ?: "") }
        var editedBio by remember { mutableStateOf(userProfile?.bio ?: "") }

        AlertDialog(
            onDismissRequest = { isEditProfileDialogOpen = false },
            title = {
                Text(
                    text = "Edit Cryptographic Profile",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Changes will be securely saved into the local Room database sandbox.",
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )

                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary
                        )
                    )

                    OutlinedTextField(
                        value = editedBio,
                        onValueChange = { editedBio = it },
                        label = { Text("Bio & Security Declaration") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editedName.isNotBlank()) {
                            onUpdateProfile(editedName, editedBio)
                            isEditProfileDialogOpen = false
                            Toast.makeText(context, "Profile saved to Room Database.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                ) {
                    Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isEditProfileDialogOpen = false }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun PrivacyToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalTeleShieldColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceVariant.copy(alpha = 0.5f))
            .tvFocusable(shape = RoundedCornerShape(8.dp), onClick = { onCheckedChange(!checked) })
            .padding(10.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = colors.textSecondary,
                lineHeight = 15.sp
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = colors.primary,
                uncheckedThumbColor = colors.textMuted,
                uncheckedTrackColor = colors.surface
            )
        )
    }
}
