package com.momentjournal.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.momentjournal.data.repository.RecordRepository

class EditorViewModel : ViewModel() {
    class Factory(
        @Suppress("UNUSED_PARAMETER") private val repository: RecordRepository,
        @Suppress("UNUSED_PARAMETER") private val recordId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EditorViewModel() as T
        }
    }
}
