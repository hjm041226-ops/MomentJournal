package com.momentjournal.ui.editor

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.FileProvider
import com.momentjournal.data.entity.BlockType
import com.momentjournal.ui.components.BlockEditor
import com.momentjournal.ui.components.TagSelectorDialog
import com.momentjournal.ui.components.MediaPickerDialog
import com.momentjournal.util.DateTimeUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.momentjournal.util.MediaManager
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
    val audioPermissionGranted = remember { mutableStateOf(
        androidx.core.content.ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.RECORD_AUDIO
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    ) }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        audioPermissionGranted.value = granted
    }
    val cameraPermissionGranted = remember { mutableStateOf(
        androidx.core.content.ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.CAMERA
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    ) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        cameraPermissionGranted.value = granted
    }
    val mediaManager = remember { MediaManager(context) }

    // Pending action to execute after permission is granted
    var pendingMediaAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    // Auto-trigger pending action when permissions are granted
    LaunchedEffect(audioPermissionGranted.value, cameraPermissionGranted.value) {
        if (audioPermissionGranted.value || cameraPermissionGranted.value) {
            pendingMediaAction?.invoke()
            pendingMediaAction = null
        }
    }

    // Delete draft file (call on submit or cancel)
    fun clearDraft() {
        val draftFile = java.io.File(mediaManager.getMediaDir(), "editor_draft.json")
        if (draftFile.exists()) draftFile.delete()
    }

    // Save blocks to temp file before external intent (protection against process death)
    fun saveDraft() {
        val currentBlocks = viewModel.blocks.value
        if (currentBlocks.isNotEmpty()) {
            val draftFile = java.io.File(mediaManager.getMediaDir(), "editor_draft.json")
            val gson = com.google.gson.Gson()
            draftFile.writeText(gson.toJson(currentBlocks.map {
                mapOf("type" to it.type.name, "content" to it.content, "sortOrder" to it.sortOrder)
            }))
        }
    }

    // Restore draft on init
    LaunchedEffect(Unit) {
        val draftFile = java.io.File(mediaManager.getMediaDir(), "editor_draft.json")
        if (draftFile.exists() && viewModel.blocks.value.isEmpty()) {
            try {
                val gson = com.google.gson.Gson()
                val json = draftFile.readText()
                val listType = object : TypeToken<Array<Map<String, Any>>>() {}.type
                val list = gson.fromJson<Array<Map<String, Any>>>(json, listType)
                list?.forEach { item ->
                    val type = com.momentjournal.data.entity.BlockType.valueOf(item["type"] as String)
                    val content = item["content"] as String
                    when (type) {
                        com.momentjournal.data.entity.BlockType.TEXT -> {
                            viewModel.addTextBlock()
                            val idx = viewModel.blocks.value.size - 1
                            if (idx >= 0) viewModel.updateBlockContent(idx, content)
                        }
                        com.momentjournal.data.entity.BlockType.IMAGE -> viewModel.addImageBlock(content)
                        com.momentjournal.data.entity.BlockType.VIDEO -> viewModel.addVideoBlock(content)
                        com.momentjournal.data.entity.BlockType.VOICE -> viewModel.addVoiceBlock(content)
                    }
                }
                draftFile.delete()
            } catch (_: Exception) { }
        }
    }
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
                    TextButton(onClick = { clearDraft(); navController.popBackStack() }) {
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
            val scrollState = rememberScrollState()
            val blockWidths by viewModel.blockWidths.collectAsState()
            var draggedIndex by remember { mutableIntStateOf(-1) }
            var dragAccumulatedY by remember { mutableFloatStateOf(0f) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // FlowRow for blocks
                androidx.compose.foundation.layout.FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    blocks.forEachIndexed { index, block ->
                        val widthFraction = blockWidths[index] ?: 1f
                        val isDragging = draggedIndex == index

                        Row(
                            modifier = Modifier
                                .then(
                                    if (widthFraction < 1f)
                                        Modifier.fillMaxWidth(0.48f)
                                    else
                                        Modifier.fillMaxWidth()
                                )
                                .zIndex(if (isDragging) 1f else 0f)
                                .graphicsLayer {
                                    translationY = if (isDragging) dragAccumulatedY else 0f
                                    scaleX = if (isDragging) 1.03f else 1f
                                    scaleY = if (isDragging) 1.03f else 1f
                                },
                            verticalAlignment = Alignment.Top
                        ) {
                            // Left control bar: drag handle + up/down + width toggle
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(top = 6.dp)
                            ) {
                                // Width toggle button
                                Text(
                                    text = if (widthFraction < 1f) "⤢" else "⤡",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .clickable { viewModel.toggleBlockWidth(index) }
                                        .padding(2.dp)
                                )
                                // Move up
                                Text("▲", fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = if (index > 0) 0.4f else 0.15f),
                                    modifier = Modifier
                                        .clickable(enabled = index > 0) { viewModel.moveBlock(index, index - 1) }
                                        .padding(2.dp)
                                )
                                // Drag handle
                                Text("⠿", fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                                    modifier = Modifier.width(24.dp)
                                )
                                // Move down
                                Text("▼", fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = if (index < blocks.size - 1) 0.4f else 0.15f),
                                    modifier = Modifier
                                        .clickable(enabled = index < blocks.size - 1) { viewModel.moveBlock(index, index + 1) }
                                        .padding(2.dp)
                                )
                            }

                            // The block editor content
                            Box(modifier = Modifier.weight(1f)) {
                                BlockEditor(
                                    block = block,
                                    onContentChange = { content -> viewModel.updateBlockContent(index, content) },
                                    onDelete = { viewModel.deleteBlock(index) }
                                )
                            }
                        }
                    }
                }

                // Hint text
                Text(
                    "💡 点击 ⤡ 切换半宽/全宽，点击 ▲▼ 调整顺序，长按 ⠿ 拖拽",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                    modifier = Modifier.padding(top = 8.dp)
                )
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
                clearDraft()
                viewModel.save { navController.popBackStack() }
            }
        )
    }

    // Media picker dialog — contextual based on type
    showMediaPicker?.let { mediaType ->
        when (mediaType) {
            BlockType.IMAGE -> {
                MediaPickerDialog(
                    mediaType = BlockType.IMAGE,
                    onDismiss = { showMediaPicker = null },
                    onFromCamera = {
                        showMediaPicker = null
                        saveDraft()
                        if (!cameraPermissionGranted.value) {
                            pendingMediaAction = {
                                val file = mediaManager.createImageFile()
                                pendingCameraFile = file
                                val uri = FileProvider.getUriForFile(
                                    context, "${context.packageName}.fileprovider", file
                                )
                                cameraLauncher.launch(uri)
                            }
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        } else {
                            val file = mediaManager.createImageFile()
                            pendingCameraFile = file
                            val uri = FileProvider.getUriForFile(
                                context, "${context.packageName}.fileprovider", file
                            )
                            cameraLauncher.launch(uri)
                        }
                    },
                    onFromGallery = {
                        showMediaPicker = null
                        saveDraft()
                        galleryLauncher.launch("image/*")
                    }
                )
            }
            BlockType.VIDEO -> {
                MediaPickerDialog(
                    mediaType = BlockType.VIDEO,
                    onDismiss = { showMediaPicker = null },
                    onFromCamera = {
                        showMediaPicker = null
                        saveDraft()
                        if (!cameraPermissionGranted.value) {
                            pendingMediaAction = {
                                val file = mediaManager.createVideoFile()
                                pendingVideoFile = file
                                val uri = FileProvider.getUriForFile(
                                    context, "${context.packageName}.fileprovider", file
                                )
                                videoLauncher.launch(uri)
                            }
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        } else {
                            val file = mediaManager.createVideoFile()
                            pendingVideoFile = file
                            val uri = FileProvider.getUriForFile(
                                context, "${context.packageName}.fileprovider", file
                            )
                            videoLauncher.launch(uri)
                        }
                    },
                    onFromGallery = {
                        showMediaPicker = null
                        saveDraft()
                        videoGalleryLauncher.launch("video/*")
                    }
                )
            }
            BlockType.VOICE -> {
                MediaPickerDialog(
                    mediaType = BlockType.VOICE,
                    onDismiss = { showMediaPicker = null },
                    onFromCamera = {
                        showMediaPicker = null
                        saveDraft()
                        if (!audioPermissionGranted.value) {
                            pendingMediaAction = {
                                val file = mediaManager.createVoiceFile()
                                recordingFile = file
                                isRecording = true
                                mediaManager.startRecording(file)
                            }
                            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            val file = mediaManager.createVoiceFile()
                            recordingFile = file
                            isRecording = true
                            mediaManager.startRecording(file)
                        }
                    },
                    onFromGallery = {
                        showMediaPicker = null
                        saveDraft()
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
