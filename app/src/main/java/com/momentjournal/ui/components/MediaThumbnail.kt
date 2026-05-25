package com.momentjournal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.momentjournal.data.entity.BlockType
import java.io.File

@Composable
fun MediaThumbnail(
    type: BlockType,
    filePath: String = "",
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(shape)
            .background(Color(0xFFF5F0F2)),
        contentAlignment = Alignment.Center
    ) {
        when (type) {
            BlockType.IMAGE -> {
                if (filePath.isNotEmpty() && File(filePath).exists()) {
                    AsyncImage(
                        model = File(filePath),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("🖼", fontSize = 16.sp)
                }
            }
            BlockType.VIDEO -> Text("🎬", fontSize = 14.sp)
            BlockType.VOICE -> Text("🎙", fontSize = 14.sp)
            else -> {}
        }
    }
}
