package com.momentjournal.ui.editor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.momentjournal.data.entity.BlockType
import com.momentjournal.ui.components.BlockEditor
import com.momentjournal.ui.components.TagSelectorDialog
import com.momentjournal.ui.components.MediaPickerDialog
import com.momentjournal.util.DateTimeUtil
import com.momentjournal.util.MediaManager
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    navController: androidx.navigation.NavHostController,
    viewModel: EditorViewModel
) {
    val blocks by viewModel.blocks.collectAsState()
    val dateTime by viewModel.recordDateTime.collectAsState()
    var showTagDialog by remember { mutableStateOf(false) }
    var showMediaPicker by remember { mutableStateOf<BlockType?>(null) }
    val context = LocalContext.current
    val mediaManager = remember { MediaManager(context) }
    var isRecording by remember { mutableStateOf(false) }
    var recordingFile by remember { mutableStateOf<File?>(null) }

    // Track pending files for camera/video capture since the callback only receives a Boolean
    var pendingCameraFile by remember { mutableStateOf<File?>(null) }
    var pendingVideoFile by remember { mutableStateOf<File?>(null) }

    // Camera photo launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingCameraFile?.let { viewModel.addImageBlock(it.absolutePath) }
        }
        pendingCameraFile = null
    }

    // Gallery image launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val dest = mediaManager.createImageFile()
            context.contentResolver.openInputStream(it)?.use { input ->
                dest.outputStream().use { output -> input.copyTo(output) }
            }
            viewModel.addImageBlock(dest.absolutePath)
        }
    }

    // Video capture launcher
    val videoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success) {
            pendingVideoFile?.let { viewModel.addVideoBlock(it.absolutePath) }
        }
        pendingVideoFile = null
    }

    // Gallery video launcher
    val videoGalleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val dest = mediaManager.createVideoFile()
            context.contentResolver.openInputStream(it)?.use { input ->
                dest.outputStream().use { output -> input.copyTo(output) }
            }
            viewModel.addVideoBlock(dest.absolutePath)
        }
    }

    // Audio file picker
    val audioPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val dest = mediaManager.createVoiceFile()
            context.contentResolver.openInputStream(it)?.use { input ->
                dest.outputStream().use { output -> input.copyTo(output) }
            }
            viewModel.addVoiceBlock(dest.absolutePath)
        }
    }

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
                    ToolbarButton("🖼 图片", onClick = { showMediaPicker = BlockType.IMAGE })
                    ToolbarButton("🎬 视频", onClick = { showMediaPicker = BlockType.VIDEO })
                    ToolbarButton("🎙 录音", onClick = { showMediaPicker = BlockType.VOICE })
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

    // Recording UI overlay
    if (isRecording) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("🔴 正在录音...") },
            text = { Text("点击停止完成录音") },
            confirmButton = {
                TextButton(onClick = {
                    mediaManager.stopRecording()
                    isRecording = false
                    recordingFile?.let { viewModel.addVoiceBlock(it.absolutePath) }
                    recordingFile = null
                }) {
                    Text("停止录音", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {}
        )
    }

    // Tag selector dialog
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

    // Media picker dialog — contextual based on type
    showMediaPicker?.let { mediaType ->
        when (mediaType) {
            BlockType.IMAGE -> {
                MediaPickerDialog(
                    onDismiss = { showMediaPicker = null },
                    onFromCamera = {
                        showMediaPicker = null
                        val file = mediaManager.createImageFile()
                        pendingCameraFile = file
                        val uri = FileProvider.getUriForFile(
                            context, "${context.packageName}.fileprovider", file
                        )
                        cameraLauncher.launch(uri)
                    },
                    onFromGallery = {
                        showMediaPicker = null
                        galleryLauncher.launch("image/*")
                    }
                )
            }
            BlockType.VIDEO -> {
                MediaPickerDialog(
                    onDismiss = { showMediaPicker = null },
                    onFromCamera = {
                        showMediaPicker = null
                        val file = mediaManager.createVideoFile()
                        pendingVideoFile = file
                        val uri = FileProvider.getUriForFile(
                            context, "${context.packageName}.fileprovider", file
                        )
                        videoLauncher.launch(uri)
                    },
                    onFromGallery = {
                        showMediaPicker = null
                        videoGalleryLauncher.launch("video/*")
                    }
                )
            }
            BlockType.VOICE -> {
                MediaPickerDialog(
                    onDismiss = { showMediaPicker = null },
                    onFromCamera = {
                        showMediaPicker = null
                        // Start instant recording
                        val file = mediaManager.createVoiceFile()
                        recordingFile = file
                        isRecording = true
                        mediaManager.startRecording(file)
                    },
                    onFromGallery = {
                        showMediaPicker = null
                        audioPickerLauncher.launch("audio/*")
                    }
                )
            }
            else -> {}
        }
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
