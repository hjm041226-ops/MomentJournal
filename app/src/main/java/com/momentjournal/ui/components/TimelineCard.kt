package com.momentjournal.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.momentjournal.data.entity.BlockEntity
import com.momentjournal.data.entity.BlockType
import com.momentjournal.data.entity.RecordEntity
import com.momentjournal.data.entity.TagEntity
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

    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = DateTimeUtil.formatTime(record.dateTime),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            if (textPreview.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
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
