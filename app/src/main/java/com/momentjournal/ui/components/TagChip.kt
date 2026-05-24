package com.momentjournal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.momentjournal.ui.theme.TagColors

@Composable
fun TagChip(
    label: String,
    colorIndex: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = label,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(TagColors.getColor(colorIndex).copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        color = TagColors.getColor(colorIndex)
    )
}
