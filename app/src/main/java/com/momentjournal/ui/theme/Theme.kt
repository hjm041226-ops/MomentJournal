package com.momentjournal.ui.theme

import android.content.Context
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.momentjournal.R
import kotlinx.coroutines.flow.map

val Context.themeDataStore by preferencesDataStore(name = "theme")

enum class AppThemeType {
    CUTE, TOUGH, SUNSHINE, COOL, QUIRKY;

    companion object {
        fun label(theme: AppThemeType, context: android.content.Context): String {
            val resId = when (theme) {
                CUTE -> R.string.theme_cute
                TOUGH -> R.string.theme_tough
                SUNSHINE -> R.string.theme_sunshine
                COOL -> R.string.theme_cool
                QUIRKY -> R.string.theme_quirky
            }
            return context.getString(resId)
        }

        fun description(theme: AppThemeType, context: android.content.Context): String {
            val resId = when (theme) {
                CUTE -> R.string.theme_cute_desc
                TOUGH -> R.string.theme_tough_desc
                SUNSHINE -> R.string.theme_sunshine_desc
                COOL -> R.string.theme_cool_desc
                QUIRKY -> R.string.theme_quirky_desc
            }
            return context.getString(resId)
        }
    }
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
fun rememberAppThemeType(): MutableState<AppThemeType> {
    val context = LocalContext.current
    val savedTheme = remember {
        val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        val ordinal = prefs.getInt("theme_type", 0)
        mutableStateOf(AppThemeType.entries.getOrElse(ordinal) { AppThemeType.CUTE })
    }
    LaunchedEffect(savedTheme.value) {
        context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
            .edit()
            .putInt("theme_type", savedTheme.value.ordinal)
            .apply()
    }
    return savedTheme
}

@Composable
fun MomentJournalTheme(
    themeType: AppThemeType = AppThemeType.CUTE,
    content: @Composable () -> Unit
) {
    val colors = themeColorMap[themeType]!!
    val isDark = themeType == AppThemeType.TOUGH

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = colors.primary,
            secondary = colors.secondary,
            background = colors.background,
            surface = colors.surface,
            onPrimary = colors.onPrimary,
            onBackground = colors.textPrimary,
            onSurface = colors.textPrimary,
            outline = colors.border,
            surfaceVariant = colors.surface,
            onSurfaceVariant = colors.textSecondary
        )
    } else {
        lightColorScheme(
            primary = colors.primary,
            secondary = colors.secondary,
            background = colors.background,
            surface = colors.surface,
            onPrimary = colors.onPrimary,
            onBackground = colors.textPrimary,
            onSurface = colors.textPrimary,
            outline = colors.border,
            surfaceVariant = colors.surface,
            onSurfaceVariant = colors.textSecondary
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
