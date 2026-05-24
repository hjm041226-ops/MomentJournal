package com.momentjournal.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.momentjournal.data.entity.RecordEntity
import com.momentjournal.data.repository.RecordRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.momentjournal.util.DateTimeUtil

class HomeViewModel(private val recordRepository: RecordRepository) : ViewModel() {
    private val _selectedDayStart = MutableStateFlow(DateTimeUtil.getDayStart(System.currentTimeMillis() / 1000))
    val selectedDayStart: StateFlow<Long> = _selectedDayStart.asStateFlow()

    val daysWithRecords: StateFlow<Set<Long>> = recordRepository.getDistinctDays()
        .map { days -> days.map { DateTimeUtil.getDayStart(it) }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val recordsForSelectedDay: StateFlow<List<RecordEntity>> = _selectedDayStart
        .flatMapLatest { recordRepository.getRecordsForDay(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectDay(dayStart: Long) {
        _selectedDayStart.value = dayStart
    }

    fun selectToday() {
        _selectedDayStart.value = DateTimeUtil.getDayStart(System.currentTimeMillis() / 1000)
    }

    class Factory(private val recordRepository: RecordRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(recordRepository) as T
        }
    }
}
