package com.momentjournal.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.momentjournal.data.entity.BlockEntity
import com.momentjournal.data.entity.RecordEntity
import com.momentjournal.data.entity.TagEntity
import com.momentjournal.data.repository.RecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val recordId: Long,
    private val recordRepository: RecordRepository
) : ViewModel() {
    private val _record = MutableStateFlow<RecordEntity?>(null)
    val record: StateFlow<RecordEntity?> = _record.asStateFlow()

    private val _blocks = MutableStateFlow<List<BlockEntity>>(emptyList())
    val blocks: StateFlow<List<BlockEntity>> = _blocks.asStateFlow()

    private val _tags = MutableStateFlow<List<TagEntity>>(emptyList())
    val tags: StateFlow<List<TagEntity>> = _tags.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _record.value = recordRepository.getRecordById(recordId)
            _blocks.value = recordRepository.getBlocksForRecord(recordId)
            _tags.value = recordRepository.getTagsForRecord(recordId)
        }
    }

    fun delete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            recordRepository.deleteRecord(recordId)
            onDeleted()
        }
    }

    class Factory(
        private val recordId: Long,
        private val recordRepository: RecordRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailViewModel(recordId, recordRepository) as T
        }
    }
}
