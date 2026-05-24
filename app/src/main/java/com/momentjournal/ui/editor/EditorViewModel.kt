package com.momentjournal.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.momentjournal.data.entity.BlockEntity
import com.momentjournal.data.entity.BlockType
import com.momentjournal.data.repository.RecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditorViewModel(
    private val recordRepository: RecordRepository,
    private val existingRecordId: Long = -1
) : ViewModel() {

    private val _blocks = MutableStateFlow<List<BlockEntity>>(emptyList())
    val blocks: StateFlow<List<BlockEntity>> = _blocks.asStateFlow()

    private val _recordDateTime = MutableStateFlow(System.currentTimeMillis() / 1000)
    val recordDateTime: StateFlow<Long> = _recordDateTime.asStateFlow()

    private val _selectedTagIds = MutableStateFlow<List<Long>>(emptyList())
    val selectedTagIds: StateFlow<List<Long>> = _selectedTagIds.asStateFlow()

    private val _blockWidths = MutableStateFlow<Map<Int, Float>>(emptyMap())
    val blockWidths: StateFlow<Map<Int, Float>> = _blockWidths.asStateFlow()

    fun toggleBlockWidth(index: Int) {
        val current = _blockWidths.value
        val currentWidth = current[index] ?: 1f
        _blockWidths.value = current + (index to if (currentWidth > 0.5f) 0.5f else 1f)
    }

    init {
        if (existingRecordId > 0) {
            viewModelScope.launch {
                val record = recordRepository.getRecordById(existingRecordId)
                if (record != null) {
                    _recordDateTime.value = record.dateTime
                    _blocks.value = recordRepository.getBlocksForRecord(existingRecordId)
                    _selectedTagIds.value = recordRepository.getTagsForRecord(existingRecordId).map { it.id }
                }
            }
        }
    }

    fun addTextBlock() {
        _blocks.value = _blocks.value + BlockEntity(
            recordId = 0, type = BlockType.TEXT, content = "", sortOrder = _blocks.value.size
        )
    }

    fun addImageBlock(filePath: String) {
        _blocks.value = _blocks.value + BlockEntity(
            recordId = 0, type = BlockType.IMAGE, content = filePath, sortOrder = _blocks.value.size
        )
    }

    fun addVideoBlock(filePath: String) {
        _blocks.value = _blocks.value + BlockEntity(
            recordId = 0, type = BlockType.VIDEO, content = filePath, sortOrder = _blocks.value.size
        )
    }

    fun addVoiceBlock(filePath: String) {
        _blocks.value = _blocks.value + BlockEntity(
            recordId = 0, type = BlockType.VOICE, content = filePath, sortOrder = _blocks.value.size
        )
    }

    fun updateBlockContent(index: Int, content: String) {
        val list = _blocks.value.toMutableList()
        if (index in list.indices) {
            list[index] = list[index].copy(content = content)
            _blocks.value = list
        }
    }

    fun deleteBlock(index: Int) {
        _blocks.value = _blocks.value.toMutableList().also { it.removeAt(index) }
    }

    fun moveBlock(fromIndex: Int, toIndex: Int) {
        val list = _blocks.value.toMutableList()
        if (fromIndex in list.indices && toIndex in list.indices && fromIndex != toIndex) {
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            _blocks.value = list
        }
    }

    fun toggleTag(tagId: Long) {
        val current = _selectedTagIds.value
        _selectedTagIds.value = if (tagId in current) current - tagId else current + tagId
    }

    fun save(onSaved: () -> Unit) {
        viewModelScope.launch {
            recordRepository.saveRecord(
                dateTime = _recordDateTime.value,
                blocks = _blocks.value,
                tagIds = _selectedTagIds.value,
                existingRecordId = if (existingRecordId > 0) existingRecordId else null
            )
            onSaved()
        }
    }

    class Factory(
        private val recordRepository: RecordRepository,
        private val existingRecordId: Long = -1
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EditorViewModel(recordRepository, existingRecordId) as T
        }
    }
}
