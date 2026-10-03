package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * High-Contrast Security Theme Modes available in TeleShield.
 * Designed for military-grade security aesthetics, OLED power conservation,
 * and high-visibility 10-foot Android TV Box viewing.
 */
enum class TeleShieldThemeMode(
    val displayName: String,
    val description: String,
    val contrastRatioText: String
) {
    CYBER_SHIELD(
        displayName = "Cyber Shield",
        description = "Electric Cyan & Emerald - Signature military-grade security terminal",
        contrastRatioText = "17.4:1 High Contrast"
    ),
    MIDNIGHT_OLED(
        displayName = "Midnight OLED",
        description = "Pure pitch black with Shield Blue - Zero light bleed & max battery efficiency",
        contrastRatioText = "20.1:1 OLED Max"
    ),
    EMERALD_CIPHER(
        displayName = "Emerald Cipher",
        description = "Terminal Matrix Green - Cryptographic cypherpunk CLI aesthetic",
        contrastRatioText = "16.8:1 Terminal"
    ),
    OBSIDIAN_CRIMSON(
        displayName = "Obsidian Crimson",
        description = "Tactical Threat Red - High-alert security monitoring and defense mode",
        contrastRatioText = "15.9:1 High Alert"
    ),
    MONOKAI_GUARD(
        displayName = "Monokai Guard",
        description = "Amber Code & Cyan - Developer console security with high legibility",
        contrastRatioText = "16.2:1 High Legibility"
    ),
    STEALTH_MONOCHROME(
        displayName = "Stealth Monochrome",
        description = "Stark White on Carbon Black with Strobe Yellow - WCAG AAA 21:1 compliance",
        contrastRatioText = "21.0:1 WCAG AAA"
    )
}

/**
 * Extended Design Tokens tailored for End-to-End Encrypted messaging,
 * security indicators, self-destruct timers, and TV remote D-Pad focus rings.
 */
data class TeleShieldColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val border: Color,
    val accent: Color,
    val textPrimary: Color = HighContrastTextPrimary,
    val textSecondary: Color = HighContrastTextSecondary,
    val textMuted: Color = HighContrastTextMuted,
    val lockColor: Color = E2EEncryptedLock,
    val threatAlert: Color = SecurityRed,
    val burnFlame: Color = SecurityYellow,
    val tvFocusRing: Color = TvFocusOutline,
    val cipherCodeText: Color = SecurityGreen
)

data class SecurityPaletteMetadata(
    val mode: TeleShieldThemeMode,
    val swatchColors: List<Color>,
    val badgeLabel: String,
    val isOledOptimized: Boolean
)

val LocalTeleShieldColors = staticCompositionLocalOf {
    TeleShieldColors(
        background = CyberBackground,
        surface = CyberSurface,
        surfaceVariant = CyberSurfaceVariant,
        primary = CyberPrimary,
        secondary = CyberSecondary,
        tertiary = CyberTertiary,
        border = CyberBorder,
        accent = CyberAccent
    )
}

val LocalTeleShieldThemeMode = staticCompositionLocalOf {
    TeleShieldThemeMode.CYBER_SHIELD
}

/**
 * Maps custom Security Colors to Material 3 standard ColorScheme for system interoperability.
 */
fun getTeleShieldColorScheme(mode: TeleShieldThemeMode): ColorScheme {
    return when (mode) {
        TeleShieldThemeMode.CYBER_SHIELD -> darkColorScheme(
            primary = CyberPrimary,
            onPrimary = Color.Black,
            secondary = CyberSecondary,
            onSecondary = Color.Black,
            tertiary = CyberTertiary,
            background = CyberBackground,
            onBackground = HighContrastTextPrimary,
            surface = CyberSurface,
            onSurface = HighContrastTextPrimary,
            surfaceVariant = CyberSurfaceVariant,
            onSurfaceVariant = HighContrastTextSecondary,
            outline = CyberBorder
        )
        TeleShieldThemeMode.MIDNIGHT_OLED -> darkColorScheme(
            primary = OledPrimary,
            onPrimary = Color.White,
            secondary = OledSecondary,
            onSecondary = Color.Black,
            tertiary = OledTertiary,
            background = OledBackground,
            onBackground = HighContrastTextPrimary,
            surface = OledSurface,
            onSurface = HighContrastTextPrimary,
            surfaceVariant = OledSurfaceVariant,
            onSurfaceVariant = HighContrastTextSecondary,
            outline = OledBorder
        )
        TeleShieldThemeMode.EMERALD_CIPHER -> darkColorScheme(
            primary = EmeraldPrimary,
            onPrimary = Color.Black,
            secondary = EmeraldSecondary,
            onSecondary = Color.Black,
            tertiary = EmeraldTertiary,
            background = EmeraldBackground,
            onBackground = HighContrastTextPrimary,
            surface = EmeraldSurface,
            onSurface = HighContrastTextPrimary,
            surfaceVariant = EmeraldSurfaceVariant,
            onSurfaceVariant = HighContrastTextSecondary,
            outline = EmeraldBorder
        )
        TeleShieldThemeMode.OBSIDIAN_CRIMSON -> darkColorScheme(
            primary = ObsidianPrimary,
            onPrimary = Color.White,
            secondary = ObsidianSecondary,
            onSecondary = Color.Black,
            tertiary = ObsidianTertiary,
            background = ObsidianBackground,
            onBackground = HighContrastTextPrimary,
            surface = ObsidianSurface,
            onSurface = HighContrastTextPrimary,
            surfaceVariant = ObsidianSurfaceVariant,
            onSurfaceVariant = HighContrastTextSecondary,
            outline = ObsidianBorder
        )
        TeleShieldThemeMode.MONOKAI_GUARD -> darkColorScheme(
            primary = MonokaiPrimary,
            onPrimary = Color.Black,
            secondary = MonokaiSecondary,
            onSecondary = Color.Black,
            tertiary = MonokaiTertiary,
            background = MonokaiBackground,
            onBackground = HighContrastTextPrimary,
            surface = MonokaiSurface,
            onSurface = HighContrastTextPrimary,
            surfaceVariant = MonokaiSurfaceVariant,
            onSurfaceVariant = HighContrastTextSecondary,
            outline = MonokaiBorder
        )
        TeleShieldThemeMode.STEALTH_MONOCHROME -> darkColorScheme(
            primary = StealthPrimary,
            onPrimary = Color.Black,
            secondary = StealthSecondary,
            onSecondary = Color.Black,
            tertiary = StealthTertiary,
            background = StealthBackground,
            onBackground = Color.White,
            surface = StealthSurface,
            onSurface = Color.White,
            surfaceVariant = StealthSurfaceVariant,
            onSurfaceVariant = Color(0xFFD0D0D8),
            outline = StealthBorder
        )
    }
}

/**
 * Returns raw security design tokens for the specified theme mode.
 */
fun getCustomColors(mode: TeleShieldThemeMode): TeleShieldColors {
    return when (mode) {
        TeleShieldThemeMode.CYBER_SHIELD -> TeleShieldColors(
            background = CyberBackground,
            surface = CyberSurface,
            surfaceVariant = CyberSurfaceVariant,
            primary = CyberPrimary,
            secondary = CyberSecondary,
            tertiary = CyberTertiary,
            border = CyberBorder,
            accent = CyberAccent,
            lockColor = E2EEncryptedLock,
            threatAlert = SecurityRed,
            burnFlame = SecurityYellow,
            tvFocusRing = CyberPrimary,
            cipherCodeText = CyberSecondary
        )
        TeleShieldThemeMode.MIDNIGHT_OLED -> TeleShieldColors(
            background = OledBackground,
            surface = OledSurface,
            surfaceVariant = OledSurfaceVariant,
            primary = OledPrimary,
            secondary = OledSecondary,
            tertiary = OledTertiary,
            border = OledBorder,
            accent = OledSecondary,
            lockColor = OledSecondary,
            threatAlert = SecurityRed,
            burnFlame = Color(0xFFFF9100),
            tvFocusRing = OledPrimary,
            cipherCodeText = OledSecondary
        )
        TeleShieldThemeMode.EMERALD_CIPHER -> TeleShieldColors(
            background = EmeraldBackground,
            surface = EmeraldSurface,
            surfaceVariant = EmeraldSurfaceVariant,
            primary = EmeraldPrimary,
            secondary = EmeraldSecondary,
            tertiary = EmeraldTertiary,
            border = EmeraldBorder,
            accent = EmeraldSecondary,
            lockColor = EmeraldSecondary,
            threatAlert = SecurityRed,
            burnFlame = Color(0xFFFFAB00),
            tvFocusRing = EmeraldPrimary,
            cipherCodeText = EmeraldPrimary
        )
        TeleShieldThemeMode.OBSIDIAN_CRIMSON -> TeleShieldColors(
            background = ObsidianBackground,
            surface = ObsidianSurface,
            surfaceVariant = ObsidianSurfaceVariant,
            primary = ObsidianPrimary,
            secondary = ObsidianSecondary,
            tertiary = ObsidianTertiary,
            border = ObsidianBorder,
            accent = ObsidianSecondary,
            lockColor = ObsidianSecondary,
            threatAlert = ObsidianPrimary,
            burnFlame = Color(0xFFFF9100),
            tvFocusRing = ObsidianPrimary,
            cipherCodeText = ObsidianSecondary
        )
        TeleShieldThemeMode.MONOKAI_GUARD -> TeleShieldColors(
            background = MonokaiBackground,
            surface = MonokaiSurface,
            surfaceVariant = MonokaiSurfaceVariant,
            primary = MonokaiPrimary,
            secondary = MonokaiSecondary,
            tertiary = MonokaiTertiary,
            border = MonokaiBorder,
            accent = MonokaiSecondary,
            lockColor = MonokaiSecondary,
            threatAlert = SecurityRed,
            burnFlame = MonokaiPrimary,
            tvFocusRing = MonokaiPrimary,
            cipherCodeText = MonokaiSecondary
        )
        TeleShieldThemeMode.STEALTH_MONOCHROME -> TeleShieldColors(
            background = StealthBackground,
            surface = StealthSurface,
            surfaceVariant = StealthSurfaceVariant,
            primary = StealthPrimary,
            secondary = StealthSecondary,
            tertiary = StealthTertiary,
            border = StealthBorder,
            accent = StealthAccent,
            textPrimary = Color.White,
            textSecondary = Color(0xFFD4D4D8),
            textMuted = Color(0xFFA1A1AA),
            lockColor = StealthSecondary,
            threatAlert = Color(0xFFFF1744),
            burnFlame = StealthSecondary,
            tvFocusRing = StealthSecondary,
            cipherCodeText = StealthSecondary
        )
    }
}

/**
 * Smoothly animates all color tokens when switching security palettes.
 * Prevents abrupt flashing or eye strain during live theme switching.
 */
@Composable
fun animateTeleShieldColors(target: TeleShieldColors): TeleShieldColors {
    val animDuration = 260
    val animSpec = tween<Color>(durationMillis = animDuration)

    val background = animateColorAsState(target.background, animSpec, label = "bg").value
    val surface = animateColorAsState(target.surface, animSpec, label = "surf").value
    val surfaceVariant = animateColorAsState(target.surfaceVariant, animSpec, label = "surfVar").value
    val primary = animateColorAsState(target.primary, animSpec, label = "primary").value
    val secondary = animateColorAsState(target.secondary, animSpec, label = "secondary").value
    val tertiary = animateColorAsState(target.tertiary, animSpec, label = "tertiary").value
    val border = animateColorAsState(target.border, animSpec, label = "border").value
    val accent = animateColorAsState(target.accent, animSpec, label = "accent").value
    val textPrimary = animateColorAsState(target.textPrimary, animSpec, label = "textP").value
    val textSecondary = animateColorAsState(target.textSecondary, animSpec, label = "textS").value
    val textMuted = animateColorAsState(target.textMuted, animSpec, label = "textM").value
    val lockColor = animateColorAsState(target.lockColor, animSpec, label = "lock").value
    val threatAlert = animateColorAsState(target.threatAlert, animSpec, label = "threat").value
    val burnFlame = animateColorAsState(target.burnFlame, animSpec, label = "burn").value
    val tvFocusRing = animateColorAsState(target.tvFocusRing, animSpec, label = "tvFocus").value
    val cipherCodeText = animateColorAsState(target.cipherCodeText, animSpec, label = "cipher").value

    return TeleShieldColors(
        background = background,
        surface = surface,
        surfaceVariant = surfaceVariant,
        primary = primary,
        secondary = secondary,
        tertiary = tertiary,
        border = border,
        accent = accent,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        textMuted = textMuted,
        lockColor = lockColor,
        threatAlert = threatAlert,
        burnFlame = burnFlame,
        tvFocusRing = tvFocusRing,
        cipherCodeText = cipherCodeText
    )
}

/**
 * Accessor object for TeleShield theme tokens.
 * Usage:
 *   TeleShieldTheme.colors.primary
 *   TeleShieldTheme.currentMode
 */
object TeleShieldTheme {
    val colors: TeleShieldColors
        @Composable
        @ReadOnlyComposable
        get() = LocalTeleShieldColors.current

    val currentMode: TeleShieldThemeMode
        @Composable
        @ReadOnlyComposable
        get() = LocalTeleShieldThemeMode.current

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    /**
     * Complete metadata list of available palettes for theme selector interfaces.
     */
    val availablePalettes: List<SecurityPaletteMetadata>
        get() = listOf(
            SecurityPaletteMetadata(
                mode = TeleShieldThemeMode.CYBER_SHIELD,
                swatchColors = listOf(CyberPrimary, CyberSecondary, CyberSurface, CyberBackground),
                badgeLabel = "CYBER DEFAULT",
                isOledOptimized = false
            ),
            SecurityPaletteMetadata(
                mode = TeleShieldThemeMode.MIDNIGHT_OLED,
                swatchColors = listOf(OledPrimary, OledSecondary, OledSurface, OledBackground),
                badgeLabel = "PURE OLED",
                isOledOptimized = true
            ),
            SecurityPaletteMetadata(
                mode = TeleShieldThemeMode.EMERALD_CIPHER,
                swatchColors = listOf(EmeraldPrimary, EmeraldSecondary, EmeraldSurface, EmeraldBackground),
                badgeLabel = "CIPHER MATRIX",
                isOledOptimized = false
            ),
            SecurityPaletteMetadata(
                mode = TeleShieldThemeMode.OBSIDIAN_CRIMSON,
                swatchColors = listOf(ObsidianPrimary, ObsidianSecondary, ObsidianSurface, ObsidianBackground),
                badgeLabel = "THREAT ALERT",
                isOledOptimized = false
            ),
            SecurityPaletteMetadata(
                mode = TeleShieldThemeMode.MONOKAI_GUARD,
                swatchColors = listOf(MonokaiPrimary, MonokaiSecondary, MonokaiSurface, MonokaiBackground),
                badgeLabel = "CODE SHIELD",
                isOledOptimized = false
            ),
            SecurityPaletteMetadata(
                mode = TeleShieldThemeMode.STEALTH_MONOCHROME,
                swatchColors = listOf(StealthPrimary, StealthSecondary, StealthSurface, StealthBackground),
                badgeLabel = "WCAG AAA 21:1",
                isOledOptimized = true
            )
        )
}

/**
 * Primary Custom Theme Composable that applies Material 3 color schemes,
 * animated TeleShield custom tokens, and propagates composition locals.
 */
@Composable
fun TeleShieldTheme(
    themeMode: TeleShieldThemeMode = TeleShieldThemeMode.CYBER_SHIELD,
    animated: Boolean = true,
    content: @Composable () -> Unit
) {
    val targetColors = getCustomColors(themeMode)
    val effectiveColors = if (animated) animateTeleShieldColors(targetColors) else targetColors
    val colorScheme = getTeleShieldColorScheme(themeMode)

    CompositionLocalProvider(
        LocalTeleShieldThemeMode provides themeMode,
        LocalTeleShieldColors provides effectiveColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
