package com.momentjournal.ui.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.momentjournal.ui.components.BlockEditor
import com.momentjournal.ui.components.TagSelectorDialog
import com.momentjournal.ui.components.MediaPickerDialog
import com.momentjournal.util.DateTimeUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    navController: androidx.navigation.NavHostController,
    viewModel: EditorViewModel
) {
    val blocks by viewModel.blocks.collectAsState()
    val dateTime by viewModel.recordDateTime.collectAsState()
    var showTagDialog by remember { mutableStateOf(false) }
    var showMediaPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(DateTimeUtil.formatDate(dateTime) + " " + DateTimeUtil.formatTime(dateTime), fontSize = 14.sp) },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("取消", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                },
                actions = {
                    TextButton(onClick = {
                        if (blocks.isNotEmpty()) showTagDialog = true
                    }) {
                        Text("提交", color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 4.dp,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ToolbarButton("Aa 文字", onClick = { viewModel.addTextBlock() })
                    ToolbarButton("🖼 图片", onClick = { showMediaPicker = true })
                    ToolbarButton("🎬 视频", onClick = { showMediaPicker = true })
                    ToolbarButton("🎙 录音", onClick = { viewModel.addVoiceBlock("voice_${System.currentTimeMillis()}.m4a") })
                }
            }
        }
    ) { padding ->
        if (blocks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("✨ 点击下方工具栏开始记录吧", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(blocks, key = { i, block -> "${block.type}_$i" }) { index, block ->
                    BlockEditor(
                        block = block,
                        onContentChange = { content -> viewModel.updateBlockContent(index, content) },
                        onDelete = { viewModel.deleteBlock(index) }
                    )
                }
            }
        }
    }

    if (showTagDialog) {
        TagSelectorDialog(
            selectedTagIds = viewModel.selectedTagIds.collectAsState().value,
            onToggleTag = { viewModel.toggleTag(it) },
            onDismiss = { showTagDialog = false },
            onConfirm = {
                showTagDialog = false
                viewModel.save { navController.popBackStack() }
            }
        )
    }

    if (showMediaPicker) {
        MediaPickerDialog(
            onDismiss = { showMediaPicker = false },
            onFromCamera = {
                showMediaPicker = false
                viewModel.addImageBlock("img_${System.currentTimeMillis()}.jpg")
            },
            onFromGallery = {
                showMediaPicker = false
                viewModel.addImageBlock("img_${System.currentTimeMillis()}.jpg")
            }
        )
    }
}

@Composable
fun ToolbarButton(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
