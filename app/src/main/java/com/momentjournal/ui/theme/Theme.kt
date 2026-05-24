package com.momentjournal.ui.theme

import android.content.Context
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.themeDataStore by preferencesDataStore(name = "theme")

enum class AppThemeType(val label: String) {
    CUTE("可爱风"),
    TOUGH("硬汉风"),
    SUNSHINE("阳光风"),
    COOL("高冷风"),
    QUIRKY("搞怪风")
}

private data class ThemeColors(
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val onPrimary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color
)

private val themeColorMap = mapOf(
    AppThemeType.CUTE to ThemeColors(CutePrimary, CuteSecondary, CuteBackground, CuteSurface, CuteOnPrimary, CuteTextPrimary, CuteTextSecondary, CuteBorder),
    AppThemeType.TOUGH to ThemeColors(ToughPrimary, ToughSecondary, ToughBackground, ToughSurface, ToughOnPrimary, ToughTextPrimary, ToughTextSecondary, ToughBorder),
    AppThemeType.SUNSHINE to ThemeColors(SunPrimary, SunSecondary, SunBackground, SunSurface, SunOnPrimary, SunTextPrimary, SunTextSecondary, SunBorder),
    AppThemeType.COOL to ThemeColors(CoolPrimary, CoolSecondary, CoolBackground, CoolSurface, CoolOnPrimary, CoolTextPrimary, CoolTextSecondary, CoolBorder),
    AppThemeType.QUIRKY to ThemeColors(QuirkyPrimary, QuirkySecondary, QuirkyBackground, QuirkySurface, QuirkyOnPrimary, QuirkyTextPrimary, QuirkyTextSecondary, QuirkyBorder)
)

@Composable
fun MomentJournalTheme(
    themeType: AppThemeType = AppThemeType.CUTE,
    content: @Composable () -> Unit
) {
    val colors = themeColorMap[themeType]!!

    val colorScheme = lightColorScheme(
        primary = colors.primary,
        secondary = colors.secondary,
        background = colors.background,
        surface = colors.surface,
        onPrimary = colors.onPrimary,
        onBackground = colors.textPrimary,
        onSurface = colors.textPrimary,
        outline = colors.border
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
