package com.example.ui.tv

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalTeleShieldColors

/**
 * Modifier that optimizes composables for both Android Phone touchscreens
 * and Android TV Box D-Pad remote control navigation.
 * Provides high-visibility focus borders, scaling feedback, and remote center/enter key execution.
 */
fun Modifier.tvFocusable(
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(12.dp),
    focusBorderWidth: Dp = 2.5.dp,
    highlightBackground: Boolean = true,
    scaleOnFocus: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    val colors = LocalTeleShieldColors.current
    val scale by animateFloatAsState(
        targetValue = if (isFocused && scaleOnFocus) 1.03f else 1.0f,
        animationSpec = spring(),
        label = "tvScale"
    )

    this
        .scale(scale)
        .onFocusChanged { focusState ->
            isFocused = focusState.isFocused
        }
        .focusable(enabled = enabled)
        .onKeyEvent { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyUp) {
                when (keyEvent.key) {
                    Key.DirectionCenter, Key.Enter, Key.NumPadEnter, Key.ButtonA -> {
                        onClick()
                        true
                    }
                    else -> false
                }
            } else {
                false
            }
        }
        .then(
            if (isFocused) {
                Modifier
                    .border(focusBorderWidth, colors.tvFocusRing, shape)
                    .then(
                        if (highlightBackground) {
                            Modifier.background(colors.tvFocusRing.copy(alpha = 0.15f), shape)
                        } else Modifier
                    )
            } else Modifier
        )
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
}
