package com.example

import com.example.ui.theme.TeleShieldTheme
import com.example.ui.theme.TeleShieldThemeMode
import com.example.ui.theme.getCustomColors
import com.example.ui.theme.getTeleShieldColorScheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeConfigurationTest {

    @Test
    fun testAllThemeModesHaveDistinctPalettes() {
        val modes = TeleShieldThemeMode.entries
        assertEquals(6, modes.size)

        modes.forEach { mode ->
            val customColors = getCustomColors(mode)
            assertNotNull(customColors.background)
            assertNotNull(customColors.primary)
            assertNotNull(customColors.secondary)
            assertNotNull(customColors.lockColor)
            assertNotNull(customColors.tvFocusRing)

            val m3Scheme = getTeleShieldColorScheme(mode)
            assertNotNull(m3Scheme.primary)
            assertNotNull(m3Scheme.surface)
        }
    }

    @Test
    fun testAvailablePalettesMetadata() {
        val metadataList = TeleShieldTheme.availablePalettes
        assertEquals(6, metadataList.size)

        val oledMode = metadataList.find { it.mode == TeleShieldThemeMode.MIDNIGHT_OLED }
        assertNotNull(oledMode)
        assertTrue(oledMode!!.isOledOptimized)

        val stealthMode = metadataList.find { it.mode == TeleShieldThemeMode.STEALTH_MONOCHROME }
        assertNotNull(stealthMode)
        assertTrue(stealthMode!!.badgeLabel.contains("21:1"))
    }
}
