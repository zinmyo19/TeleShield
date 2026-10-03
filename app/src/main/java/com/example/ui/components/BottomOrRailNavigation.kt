package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalTeleShieldColors
import com.example.ui.tv.tvFocusable
import com.example.viewmodel.MainNavigationTab

data class NavItem(
    val tab: MainNavigationTab,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

val NAV_ITEMS = listOf(
    NavItem(MainNavigationTab.CHATS, "Chats", Icons.Filled.ChatBubble, Icons.Outlined.ChatBubbleOutline, "nav_chats"),
    NavItem(MainNavigationTab.SECRET_VAULT, "Vault", Icons.Filled.Lock, Icons.Outlined.Lock, "nav_vault"),
    NavItem(MainNavigationTab.PLAYER, "Player", Icons.Filled.PlayCircle, Icons.Outlined.PlayCircleOutline, "nav_player"),
    NavItem(MainNavigationTab.INSIGHTS, "Insights", Icons.Filled.Insights, Icons.Outlined.Insights, "nav_insights"),
    NavItem(MainNavigationTab.PRIVACY, "Privacy", Icons.Filled.Security, Icons.Outlined.Security, "nav_privacy"),
    NavItem(MainNavigationTab.SUBSCRIPTION, "Premium", Icons.Filled.Star, Icons.Outlined.StarOutline, "nav_subscription")
)

@Composable
fun TeleShieldBottomBar(
    currentTab: MainNavigationTab,
    onTabSelected: (MainNavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalTeleShieldColors.current

    Surface(
        color = colors.surface,
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = colors.border.copy(alpha = 0.5f))
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NAV_ITEMS.forEach { item ->
                val isSelected = currentTab == item.tab
                val tint = if (isSelected) colors.primary else colors.textMuted
                val bgColor = if (isSelected) colors.primary.copy(alpha = 0.12f) else colors.surface

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgColor)
                        .tvFocusable(
                            shape = RoundedCornerShape(10.dp),
                            onClick = { onTabSelected(item.tab) }
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag(item.testTag),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label,
                            tint = tint,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = tint
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TeleShieldNavigationRail(
    currentTab: MainNavigationTab,
    onTabSelected: (MainNavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalTeleShieldColors.current

    Surface(
        color = colors.surface,
        modifier = modifier
            .fillMaxHeight()
            .width(88.dp)
            .border(width = 1.dp, color = colors.border.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NAV_ITEMS.forEach { item ->
                val isSelected = currentTab == item.tab
                val tint = if (isSelected) colors.primary else colors.textMuted
                val bgColor = if (isSelected) colors.primary.copy(alpha = 0.14f) else colors.surface

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgColor)
                        .tvFocusable(
                            shape = RoundedCornerShape(10.dp),
                            onClick = { onTabSelected(item.tab) }
                        )
                        .padding(vertical = 10.dp)
                        .testTag("${item.testTag}_rail"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label,
                            tint = tint,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = tint
                        )
                    }
                }
            }
        }
    }
}
