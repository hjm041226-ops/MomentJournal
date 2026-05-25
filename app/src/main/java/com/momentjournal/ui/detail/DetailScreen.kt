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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.momentjournal.R
import com.momentjournal.data.entity.BlockType
import coil.compose.AsyncImage
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
                        Text(stringResource(R.string.detail_edit), color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
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
                        val filePath = block.content
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(stringResource(R.string.detail_image), fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                }
                                if (filePath.isNotEmpty()) {
                                    AsyncImage(
                                        model = java.io.File(filePath),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 120.dp, max = 300.dp)
                                            .padding(horizontal = 12.dp)
                                            .padding(bottom = 12.dp)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().height(120.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(stringResource(R.string.detail_image_error), fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                                    }
                                }
                            }
                        }
                    }
                    BlockType.VIDEO -> {
                        val fileName = if (block.content.isNotEmpty()) java.io.File(block.content).name else ""
                        val context = androidx.compose.ui.platform.LocalContext.current
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("🎬", fontSize = 22.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(stringResource(R.string.detail_video), fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                                    if (fileName.isNotEmpty()) {
                                        Text(fileName, fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                    }
                                }
                                TextButton(onClick = {
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                        setDataAndType(
                                            androidx.core.content.FileProvider.getUriForFile(
                                                context, "${context.packageName}.fileprovider",
                                                java.io.File(block.content)
                                            ), "video/*"
                                        )
                                        addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(intent)
                                }) {
                                    Text(stringResource(R.string.detail_play), color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                    BlockType.VOICE -> {
                        val fileName = if (block.content.isNotEmpty()) java.io.File(block.content).name else ""
                        val context = androidx.compose.ui.platform.LocalContext.current
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("🎙", fontSize = 22.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(stringResource(R.string.detail_voice), fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                                    if (fileName.isNotEmpty()) {
                                        Text(fileName, fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                    }
                                }
                                TextButton(onClick = {
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                        setDataAndType(
                                            androidx.core.content.FileProvider.getUriForFile(
                                                context, "${context.packageName}.fileprovider",
                                                java.io.File(block.content)
                                            ), "audio/*"
                                        )
                                        addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(intent)
                                }) {
                                    Text(stringResource(R.string.detail_play), color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
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
            title = { Text(stringResource(R.string.detail_delete_title)) },
            text = { Text(stringResource(R.string.detail_delete_msg)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.delete { navController.popBackStack() }
                }) {
                    Text(stringResource(R.string.detail_delete_confirm), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text(stringResource(R.string.detail_cancel)) }
            }
        )
    }
}
