package com.example.hopper.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hopper.domain.model.Tithi
import com.example.hopper.domain.repository.CalendarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    calendarRepository: CalendarRepository
) : ViewModel() {

    val tithis: StateFlow<List<Tithi>> = calendarRepository.getTithisForActiveFestival()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val peakDays: StateFlow<List<Tithi>> = calendarRepository.getPeakCrowdDays()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
