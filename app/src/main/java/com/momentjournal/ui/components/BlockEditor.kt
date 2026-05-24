package com.momentjournal.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.momentjournal.data.entity.BlockEntity
import com.momentjournal.data.entity.BlockType

@Composable
fun BlockEditor(
    block: BlockEntity,
    onContentChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)

    when (block.type) {
        BlockType.TEXT -> {
            var text by remember(block.id, block.content) { mutableStateOf(block.content) }
            Surface(
                modifier = modifier
                    .fillMaxWidth()
                    .border(1.5.dp, SolidColor(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)), shape),
                shape = shape,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("「文字」", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 2.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = text,
                        onValueChange = { newText ->
                            text = newText
                            onContentChange(newText)
                        },
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { innerTextField ->
                            if (text.isEmpty()) {
                                Text("输入文字...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), fontSize = 14.sp)
                            }
                            innerTextField()
                        }
                    )
                    Text(
                        "✕",
                        modifier = Modifier.clickable(onClick = onDelete).padding(4.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        fontSize = 14.sp
                    )
                }
            }
        }

        BlockType.IMAGE -> {
            Surface(
                modifier = modifier.fillMaxWidth().border(1.5.dp, SolidColor(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)), shape),
                shape = shape,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🖼 图片", fontSize = 13.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("✕", modifier = Modifier.clickable(onClick = onDelete).padding(4.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                }
            }
        }

        BlockType.VIDEO -> {
            Surface(
                modifier = modifier.fillMaxWidth().border(1.5.dp, SolidColor(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)), shape),
                shape = shape,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🎬 视频", fontSize = 13.sp)
                    if (block.content.isNotEmpty()) {
                        Text(" ${block.content}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text("✕", modifier = Modifier.clickable(onClick = onDelete).padding(4.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                }
            }
        }

        BlockType.VOICE -> {
            Surface(
                modifier = modifier.fillMaxWidth().border(1.5.dp, SolidColor(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)), shape),
                shape = shape,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🎙 语音", fontSize = 13.sp)
                    if (block.content.isNotEmpty()) {
                        Text(" ${block.content}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text("✕", modifier = Modifier.clickable(onClick = onDelete).padding(4.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                }
            }
        }
    }
}
