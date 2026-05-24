package com.momentjournal.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.momentjournal.data.repository.RecordRepository

class DetailViewModel : ViewModel() {
    class Factory(
        @Suppress("UNUSED_PARAMETER") private val recordId: Long,
        @Suppress("UNUSED_PARAMETER") private val repository: RecordRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailViewModel() as T
        }
    }
}
