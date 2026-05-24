package com.momentjournal.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.momentjournal.data.entity.BlockType

@Composable
fun MediaPickerDialog(
    mediaType: BlockType,
    onDismiss: () -> Unit,
    onFromCamera: () -> Unit,
    onFromGallery: () -> Unit
) {
    val (title, option1, option2) = when (mediaType) {
        BlockType.IMAGE -> Triple("添加图片", "📷 拍摄照片", "🖼 从相册选择")
        BlockType.VIDEO -> Triple("添加视频", "🎬 拍摄视频", "🎞 从相册选择")
        BlockType.VOICE -> Triple("添加录音", "🎙 即时录音", "📁 选取音频文件")
        else -> Triple("选择来源", "拍摄", "从相册选择")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable(onClick = onFromCamera),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(option1, modifier = Modifier.padding(16.dp), fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable(onClick = onFromGallery),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(option2, modifier = Modifier.padding(16.dp), fontSize = 16.sp)
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}
