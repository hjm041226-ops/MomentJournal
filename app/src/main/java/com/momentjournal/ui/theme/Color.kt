package com.momentjournal.ui.theme

import androidx.compose.ui.graphics.Color

// Cute (default) - Warm Sakura
val CutePrimary = Color(0xFFF4A0A0)       // softer warm pink
val CuteSecondary = Color(0xFFFFC8C8)     // light blush
val CuteBackground = Color(0xFFFFF8F6)    // warm cream white
val CuteSurface = Color(0xFFFFFFFF)
val CuteOnPrimary = Color(0xFFFFFFFF)
val CuteTextPrimary = Color(0xFF6B4E5A)   // warm brown-gray (keep)
val CuteTextSecondary = Color(0xFFB09898) // warmer gray
val CuteBorder = Color(0xFFFFE0DC)        // warm border

// Tough - Dark rugged
val ToughPrimary = Color(0xFF3A3A3A)
val ToughSecondary = Color(0xFF8B4513)
val ToughBackground = Color(0xFF1A1A1A)
val ToughSurface = Color(0xFF2D2D2D)
val ToughOnPrimary = Color(0xFFFFFFFF)
val ToughTextPrimary = Color(0xFFE0E0E0)
val ToughTextSecondary = Color(0xFF9E9E9E)
val ToughBorder = Color(0xFF555555)

// Sunshine - Warm bright
val SunPrimary = Color(0xFFFFA726)
val SunSecondary = Color(0xFFFFCC02)
val SunBackground = Color(0xFFFFFDE7)
val SunSurface = Color(0xFFFFFFFF)
val SunOnPrimary = Color(0xFFFFFFFF)
val SunTextPrimary = Color(0xFF5D4037)
val SunTextSecondary = Color(0xFF8D6E63)
val SunBorder = Color(0xFFFFE0B2)

// Cool - Minimal monochrome
val CoolPrimary = Color(0xFF607D8B)
val CoolSecondary = Color(0xFF90A4AE)
val CoolBackground = Color(0xFFF5F5F5)
val CoolSurface = Color(0xFFFFFFFF)
val CoolOnPrimary = Color(0xFFFFFFFF)
val CoolTextPrimary = Color(0xFF37474F)
val CoolTextSecondary = Color(0xFF78909C)
val CoolBorder = Color(0xFFCFD8DC)

// Quirky - Playful colorful
val QuirkyPrimary = Color(0xFF9C27B0)
val QuirkySecondary = Color(0xFF00BCD4)
val QuirkyBackground = Color(0xFFFFF8E1)
val QuirkySurface = Color(0xFFFFFFFF)
val QuirkyOnPrimary = Color(0xFFFFFFFF)
val QuirkyTextPrimary = Color(0xFF4A148C)
val QuirkyTextSecondary = Color(0xFF00838F)
val QuirkyBorder = Color(0xFFF8BBD0)

// Tag colors (shared across themes)
object TagColors {
    val colors = listOf(
        Color(0xFFF9C7B7),
        Color(0xFFA4C8F0),
        Color(0xFFB8E0D2),
        Color(0xFFE8C4E0),
        Color(0xFFFDD9A5),
        Color(0xFFFFD4B2),
    )

    fun getColor(index: Int): Color = colors[index % colors.size]
}
