package com.momentjournal.ui.tag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.momentjournal.data.entity.TagEntity
import com.momentjournal.data.repository.TagRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TagViewModel(private val tagRepository: TagRepository) : ViewModel() {
    val presetTags: StateFlow<List<TagEntity>> = tagRepository.getPresetTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customTags: StateFlow<List<TagEntity>> = tagRepository.getCustomTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTag(name: String) {
        viewModelScope.launch {
            tagRepository.addCustomTag(name)
        }
    }

    fun deleteTag(tag: TagEntity) {
        viewModelScope.launch {
            tagRepository.deleteTag(tag)
        }
    }

    class Factory(private val tagRepository: TagRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TagViewModel(tagRepository) as T
        }
    }
}
