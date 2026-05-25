package com.momentjournal.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.momentjournal.R
import com.momentjournal.data.entity.BlockEntity
import com.momentjournal.data.entity.BlockType

@Composable
fun BlockEditor(
    block: BlockEntity,
    onContentChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    bubbleColor: Color = Color.Transparent
) {
    when (block.type) {
        BlockType.TEXT -> {
            var isEditing by remember { mutableStateOf(false) }
            var text by remember(block.id, block.content) { mutableStateOf(block.content) }
            val focusRequester = remember { FocusRequester() }

            LaunchedEffect(isEditing) {
                if (isEditing) focusRequester.requestFocus()
            }

            Surface(
                modifier = modifier
                    .fillMaxWidth()
                    .clickable { isEditing = true },
                shape = RoundedCornerShape(16.dp),
                color = bubbleColor
            ) {
                if (isEditing) {
                    BasicTextField(
                        value = text,
                        onValueChange = { newText ->
                            text = newText
                            onContentChange(newText)
                        },
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        singleLine = false,
                        decorationBox = { innerTextField ->
                            if (text.isEmpty()) {
                                Text(stringResource(R.string.editor_placeholder), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f), fontSize = 14.sp)
                            }
                            innerTextField()
                        }
                    )
                } else {
                    Text(
                        text = if (text.isNotEmpty()) text else stringResource(R.string.editor_placeholder),
                        modifier = Modifier.padding(14.dp),
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = if (text.isNotEmpty())
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                    )
                }
            }
        }

        BlockType.IMAGE -> {
            Surface(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = bubbleColor
            ) {
                if (block.content.isNotEmpty() && java.io.File(block.content).exists()) {
                    AsyncImage(
                        model = java.io.File(block.content),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp, max = 180.dp)
                    )
                } else {
                    Box(modifier = Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.block_image), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                    }
                }
            }
        }

        BlockType.VIDEO -> {
            val fileName = if (block.content.isNotEmpty()) java.io.File(block.content).name else ""
            Surface(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = bubbleColor
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🎬", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (fileName.isNotEmpty()) fileName else stringResource(R.string.block_video), fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }
        }

        BlockType.VOICE -> {
            val fileName = if (block.content.isNotEmpty()) java.io.File(block.content).name else ""
            Surface(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = bubbleColor
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🎙", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (fileName.isNotEmpty()) fileName else stringResource(R.string.block_voice), fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }
        }
    }
}
