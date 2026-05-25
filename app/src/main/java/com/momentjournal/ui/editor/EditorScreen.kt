package com.momentjournal.ui.editor

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.ui.input.pointer.pointerInput
import androidx.core.content.FileProvider
import com.momentjournal.data.entity.BlockType
import com.momentjournal.ui.components.BlockEditor
import com.momentjournal.ui.components.TagSelectorDialog
import com.momentjournal.ui.components.MediaPickerDialog
import com.momentjournal.util.DateTimeUtil
import kotlin.math.roundToInt
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
            val bubbleScales by viewModel.bubbleScales.collectAsState()

            // Drag state
            var draggedIndex by remember { mutableIntStateOf(-1) }
            var dragOffsetX by remember { mutableFloatStateOf(0f) }
            var dragOffsetY by remember { mutableFloatStateOf(0f) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Custom row-based layout: equal width within each row, uniform height
                val bubbleSpacing = 8.dp

                // Calculate rows: greedy fill, 2 items per row
                val rows = remember(blocks) {
                    val result = mutableListOf<MutableList<Int>>()
                    var currentRow = mutableListOf<Int>()
                    result.add(currentRow)
                    blocks.forEachIndexed { index, _ ->
                        currentRow.add(index)
                        if (currentRow.size >= 2) {
                            currentRow = mutableListOf()
                            result.add(currentRow)
                        }
                    }
                    result.filter { it.isNotEmpty() }
                }

                rows.forEach { rowIndices ->
                    Row(
                        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(bubbleSpacing)
                    ) {
                        rowIndices.forEach row@{ index ->
                            val block = blocks.getOrNull(index) ?: return@row
                            val isDragging = draggedIndex == index

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .zIndex(if (isDragging) 10f else 0f)
                                    .graphicsLayer {
                                        translationX = if (isDragging) dragOffsetX else 0f
                                        translationY = if (isDragging) dragOffsetY else 0f
                                        shadowElevation = if (isDragging) 8f else 0f
                                    }
                                    .pointerInput(index) {
                                        detectTransformGestures { _, _, zoom, _ ->
                                            if (draggedIndex < 0) {
                                                val newScale = ((bubbleScales[index] ?: 1f) * zoom).coerceIn(0.5f, 2f)
                                                viewModel.updateBubbleScale(index, newScale)
                                            }
                                        }
                                    }
                                    .pointerInput(index) {
                                        detectDragGesturesAfterLongPress(
                                            onDragStart = {
                                                draggedIndex = index
                                                dragOffsetX = 0f
                                                dragOffsetY = 0f
                                            },
                                            onDragEnd = {
                                                val rowShift = (dragOffsetY / 80f).roundToInt()
                                                val colShift = (dragOffsetX / 120f).roundToInt()
                                                val indexShift = rowShift * 2 + colShift
                                                val targetIndex = (index + indexShift).coerceIn(0, blocks.size - 1)
                                                if (targetIndex != index) {
                                                    viewModel.moveBlock(index, targetIndex)
                                                }
                                                draggedIndex = -1
                                                dragOffsetX = 0f
                                                dragOffsetY = 0f
                                            },
                                            onDragCancel = {
                                                draggedIndex = -1
                                                dragOffsetX = 0f
                                                dragOffsetY = 0f
                                            },
                                            onDrag = { change, offset ->
                                                change.consume()
                                                dragOffsetX += offset.x
                                                dragOffsetY += offset.y
                                            }
                                        )
                                    }
                            ) {
                                val bubbleColor = when (block.type) {
                                    com.momentjournal.data.entity.BlockType.TEXT -> Color(0xFFFFF0F0)
                                    com.momentjournal.data.entity.BlockType.IMAGE -> Color(0xFFF0F4FF)
                                    com.momentjournal.data.entity.BlockType.VIDEO -> Color(0xFFF0FFF4)
                                    com.momentjournal.data.entity.BlockType.VOICE -> Color(0xFFFFF8F0)
                                }

                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    shadowElevation = if (isDragging) 4.dp else 0.dp
                                ) {
                                    Box {
                                        Text(
                                            "✕", fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .clickable { viewModel.deleteBlock(index) }
                                                .padding(4.dp)
                                        )
                                        BlockEditor(
                                            block = block,
                                            onContentChange = { content -> viewModel.updateBlockContent(index, content) },
                                            onDelete = { viewModel.deleteBlock(index) },
                                            bubbleColor = bubbleColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                    // Spacer between rows
                    Spacer(modifier = Modifier.height(bubbleSpacing))
                }

                // Hint
                Text(
                    "💡 长按拖拽排序 · 松手自动排列 · 点击文字气泡可编辑",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    modifier = Modifier.padding(top = 12.dp)
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
