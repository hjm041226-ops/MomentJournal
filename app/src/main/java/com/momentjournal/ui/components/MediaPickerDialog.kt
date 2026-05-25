package com.momentjournal.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.momentjournal.R
import com.momentjournal.data.entity.BlockType

@Composable
fun MediaPickerDialog(
    mediaType: BlockType,
    onDismiss: () -> Unit,
    onFromCamera: () -> Unit,
    onFromGallery: () -> Unit
) {
    val (title, option1, option2) = when (mediaType) {
        BlockType.IMAGE -> Triple(stringResource(R.string.media_image_title), stringResource(R.string.media_image_camera), stringResource(R.string.media_image_gallery))
        BlockType.VIDEO -> Triple(stringResource(R.string.media_video_title), stringResource(R.string.media_video_camera), stringResource(R.string.media_video_gallery))
        BlockType.VOICE -> Triple(stringResource(R.string.media_voice_title), stringResource(R.string.media_voice_record), stringResource(R.string.media_voice_file))
        else -> Triple(stringResource(R.string.media_image_title), stringResource(R.string.media_image_camera), stringResource(R.string.media_image_gallery))
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
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.media_cancel)) } }
    )
}
