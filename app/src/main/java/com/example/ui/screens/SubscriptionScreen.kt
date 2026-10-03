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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.data.repository.SubscriptionTier
import com.example.ui.theme.LocalTeleShieldColors
import com.example.ui.tv.tvFocusable

@Composable
fun SubscriptionScreen(
    currentTier: SubscriptionTier,
    onSelectTier: (SubscriptionTier) -> Unit,
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
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("subscription_hero_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, colors.secondary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(colors.secondary.copy(alpha = 0.2f))
                            .border(2.dp, colors.secondary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = colors.secondary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "TeleShield Premium Tiers",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Unlock ad-free encrypted broadcast streams, lossless FLAC listening, offline storage vault, and 4GB secret file transfer limits.",
                        fontSize = 12.sp,
                        color = colors.textSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // Tiers Cards
        item {
            TierOptionCard(
                tier = SubscriptionTier.SHIELD_PLUS,
                isActive = currentTier == SubscriptionTier.SHIELD_PLUS,
                features = listOf(
                    "100% Ad-Free Listening on all broadcasts & podcasts",
                    "Offline Vault caching for confidential listening anywhere",
                    "Lossless 320kbps & FLAC encrypted audio streaming",
                    "Up to 4GB Zero-Knowledge file transfer in Secret Chats",
                    "Exclusive Cyber Shield+ verified profile badge"
                ),
                onSelect = {
                    onSelectTier(SubscriptionTier.SHIELD_PLUS)
                    Toast.makeText(context, "Activated TeleShield Shield+ Plan!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        item {
            TierOptionCard(
                tier = SubscriptionTier.ULTRA_PRO,
                isActive = currentTier == SubscriptionTier.ULTRA_PRO,
                features = listOf(
                    "All Shield+ features included",
                    "Real-time voice modulation disguise for confidential dispatches",
                    "Anti-forensic volatile memory shredder on app close",
                    "Prioritized onion routing nodes with ultra-low latency",
                    "Lifetime Cryptographic Seal & Hardware Token Support"
                ),
                onSelect = {
                    onSelectTier(SubscriptionTier.ULTRA_PRO)
                    Toast.makeText(context, "Activated TeleShield Ultra Pro Plan!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        item {
            TierOptionCard(
                tier = SubscriptionTier.FREE,
                isActive = currentTier == SubscriptionTier.FREE,
                features = listOf(
                    "Standard End-to-End Encrypted Secret Chats",
                    "Standard Audio Streams with sponsored dispatches",
                    "500MB secure file transfer limit"
                ),
                onSelect = {
                    onSelectTier(SubscriptionTier.FREE)
                    Toast.makeText(context, "Switched to Standard Cipher plan.", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun TierOptionCard(
    tier: SubscriptionTier,
    isActive: Boolean,
    features: List<String>,
    onSelect: () -> Unit
) {
    val colors = LocalTeleShieldColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .tvFocusable(shape = RoundedCornerShape(14.dp), onClick = onSelect)
            .testTag("tier_card_${tier.name}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) colors.surfaceVariant else colors.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isActive) 2.dp else 1.dp,
            color = if (isActive) colors.primary else colors.border
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = tier.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        if (isActive) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(colors.primary)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                    Text(
                        text = tier.price,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.secondary
                    )
                }

                Button(
                    onClick = onSelect,
                    modifier = Modifier.tvFocusable(shape = RoundedCornerShape(8.dp), onClick = onSelect),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isActive) colors.surfaceVariant else colors.primary,
                        contentColor = if (isActive) colors.primary else Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isActive) "Current" else "Select Tier",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                features.forEach { feat ->
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = colors.secondary,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = feat,
                            fontSize = 12.sp,
                            color = colors.textSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
