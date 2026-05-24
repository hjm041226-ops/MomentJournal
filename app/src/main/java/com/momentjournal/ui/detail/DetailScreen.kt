package com.momentjournal.ui.detail

import androidx.compose.foundation.BorderStroke
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
import com.momentjournal.data.entity.BlockType
import com.momentjournal.ui.components.TagChip
import com.momentjournal.ui.navigation.Routes
import com.momentjournal.util.DateTimeUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    recordId: Long,
    navController: androidx.navigation.NavHostController,
    viewModel: DetailViewModel
) {
    val record by viewModel.record.collectAsState()
    val blocks by viewModel.blocks.collectAsState()
    val tags by viewModel.tags.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    record?.let {
                        Text(DateTimeUtil.formatDate(it.dateTime) + " " + DateTimeUtil.formatTime(it.dateTime),
                            fontSize = 14.sp)
                    }
                },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("←", color = MaterialTheme.colorScheme.onSurface)
                    }
                },
                actions = {
                    TextButton(onClick = {
                        navController.navigate(Routes.editor(recordId))
                    }) {
                        Text("✎ 编辑", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                    }
                    TextButton(onClick = { showDeleteConfirm = true }) {
                        Text("🗑", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (tags.isNotEmpty()) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        tags.forEachIndexed { index, tag ->
                            TagChip(label = tag.name, colorIndex = index)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            itemsIndexed(blocks, key = { i, _ -> i }) { _, block ->
                when (block.type) {
                    BlockType.TEXT -> {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        ) {
                            Text(
                                text = block.content,
                                modifier = Modifier.padding(12.dp),
                                fontSize = 15.sp,
                                lineHeight = 24.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                            )
                        }
                    }
                    BlockType.IMAGE -> {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🖼 图片", fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                            }
                        }
                    }
                    BlockType.VIDEO -> {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🎬 视频", fontSize = 14.sp)
                                Spacer(modifier = Modifier.weight(1f))
                                TextButton(onClick = { /* play */ }) {
                                    Text("▶ 播放", color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                    BlockType.VOICE -> {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🎙 语音", fontSize = 14.sp)
                                Spacer(modifier = Modifier.weight(1f))
                                TextButton(onClick = { /* play */ }) {
                                    Text("▶ 播放", color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("确认删除") },
            text = { Text("删除后无法恢复，确定要删除这条记录吗？") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.delete { navController.popBackStack() }
                }) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("取消") }
            }
        )
    }
}
