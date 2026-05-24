package com.momentjournal.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.momentjournal.data.entity.BlockEntity
import com.momentjournal.data.entity.BlockType
import com.momentjournal.data.entity.RecordEntity
import com.momentjournal.data.entity.TagEntity
import com.momentjournal.ui.theme.TagColors
import com.momentjournal.util.DateTimeUtil

@Composable
fun TimelineCard(
    record: RecordEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    blocks: List<BlockEntity> = emptyList(),
    tags: List<TagEntity> = emptyList()
) {
    val textPreview = blocks
        .firstOrNull { it.type == BlockType.TEXT }?.content ?: ""
    val imageBlocks = blocks.filter { it.type == BlockType.IMAGE }
    val hasVideo = blocks.any { it.type == BlockType.VIDEO }
    val hasVoice = blocks.any { it.type == BlockType.VOICE }

    // Border accent color from first tag
    val accentColor = if (tags.isNotEmpty())
        TagColors.getColor(tags.first().id.toInt() % TagColors.colors.size)
    else
        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)

    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.Top
    ) {
        // Time column — fixed width on the left
        Text(
            text = DateTimeUtil.formatTime(record.dateTime),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
            fontSize = 12.sp,
            modifier = Modifier.width(44.dp).padding(top = 10.dp)
        )

        // Card content — fills remaining space, with left color stripe
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            tonalElevation = 1.dp
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                // Color stripe on the left
                Box(
                    modifier = Modifier
                        .width(5.dp)
                        .fillMaxHeight()
                        .background(accentColor)
                )
                Column(modifier = Modifier.padding(start = 8.dp, end = 10.dp, top = 10.dp, bottom = 10.dp)) {
                    if (textPreview.isNotEmpty()) {
                        Text(
                            text = textPreview,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                    if (imageBlocks.isNotEmpty() || hasVideo || hasVoice) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            imageBlocks.take(3).forEach { _ ->
                                MediaThumbnail(type = BlockType.IMAGE)
                            }
                            if (imageBlocks.size > 3) {
                                Text("+${imageBlocks.size - 3}", fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            }
                            if (hasVideo) MediaThumbnail(type = BlockType.VIDEO)
                            if (hasVoice) MediaThumbnail(type = BlockType.VOICE)
                        }
                    }
                    if (tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            tags.take(4).forEachIndexed { index, tag ->
                                TagChip(label = tag.name, colorIndex = index)
                            }
                        }
                    }
                }
            }
        }
    }
}
