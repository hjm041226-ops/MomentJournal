package com.momentjournal.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.momentjournal.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePickerScreen(
    currentTheme: AppThemeType,
    onThemeSelected: (AppThemeType) -> Unit,
    onBack: () -> Unit
) {
    val themes = AppThemeType.entries

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.theme_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("←") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(stringResource(R.string.theme_subtitle),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 4.dp))
            }

            items(themes) { theme ->
                val isSelected = theme == currentTheme
                val previewColors = themePreviewColors(theme)

                Surface(
                    modifier = Modifier.fillMaxWidth().clickable { onThemeSelected(theme) },
                    shape = RoundedCornerShape(18.dp),
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else
                        MaterialTheme.colorScheme.surface,
                    border = if (isSelected)
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    else
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    tonalElevation = if (isSelected) 2.dp else 0.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(AppThemeType.label(theme, LocalContext.current), fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    AppThemeType.description(theme, LocalContext.current),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                            if (isSelected) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("✓", color = Color.White, fontSize = 14.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Color preview swatches
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            previewColors.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .then(
                                            if (color == Color.White)
                                                Modifier.background(Color.White, CircleShape)
                                                    .then(Modifier.border(1.dp, Color(0xFFE0E0E0), CircleShape))
                                            else Modifier
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun themePreviewColors(theme: AppThemeType): List<Color> {
    return when (theme) {
        AppThemeType.CUTE -> listOf(Color(0xFFF4A0A0), Color(0xFFFFC8C8), Color(0xFFFFF8F6), Color(0xFF6B4E5A))
        AppThemeType.TOUGH -> listOf(Color(0xFF3A3A3A), Color(0xFF8B4513), Color(0xFF1A1A1A), Color(0xFFE0E0E0))
        AppThemeType.SUNSHINE -> listOf(Color(0xFFFFA726), Color(0xFFFFCC02), Color(0xFFFFFDE7), Color(0xFF5D4037))
        AppThemeType.COOL -> listOf(Color(0xFF607D8B), Color(0xFF90A4AE), Color(0xFFF5F5F5), Color(0xFF37474F))
        AppThemeType.QUIRKY -> listOf(Color(0xFF9C27B0), Color(0xFF00BCD4), Color(0xFFFFF8E1), Color(0xFF4A148C))
    }
}

