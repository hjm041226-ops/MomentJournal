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

@Composable
fun MediaPickerDialog(
    onDismiss: () -> Unit,
    onFromCamera: () -> Unit,
    onFromGallery: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择来源", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable(onClick = onFromCamera),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text("📷 拍摄", modifier = Modifier.padding(16.dp), fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable(onClick = onFromGallery),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text("🖼 从相册选择", modifier = Modifier.padding(16.dp), fontSize = 16.sp)
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}
