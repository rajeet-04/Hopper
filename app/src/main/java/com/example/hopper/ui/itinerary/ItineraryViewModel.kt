package com.example.hopper.ui.itinerary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hopper.domain.usecase.BuildItineraryUseCase
import com.example.hopper.domain.usecase.Itinerary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItineraryViewModel @Inject constructor(
    private val buildItineraryUseCase: BuildItineraryUseCase
) : ViewModel() {

    private val _itinerary = MutableStateFlow<Itinerary?>(null)
    val itinerary: StateFlow<Itinerary?> = _itinerary.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun buildItinerary(pandalIds: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            _itinerary.value = buildItineraryUseCase(pandalIds)
            _isLoading.value = false
        }
    }
}
