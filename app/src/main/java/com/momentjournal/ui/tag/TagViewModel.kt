package com.momentjournal.ui.tag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.momentjournal.data.repository.TagRepository

class TagViewModel : ViewModel() {
    class Factory(
        @Suppress("UNUSED_PARAMETER") private val repository: TagRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TagViewModel() as T
        }
    }
}
