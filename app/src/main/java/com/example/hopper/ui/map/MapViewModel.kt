package com.example.hopper.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hopper.domain.FestivalToggleController
import com.example.hopper.domain.LocationProvider
import com.example.hopper.domain.model.CrowdBucket
import com.example.hopper.domain.model.ExitRoute
import com.example.hopper.domain.model.Festival
import com.example.hopper.domain.model.FestivalContext
import com.example.hopper.domain.model.Pandal
import com.example.hopper.domain.repository.PandalRepository
import com.example.hopper.domain.usecase.GetExitRoutesUseCase
import com.example.hopper.domain.usecase.ToggleFestivalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val pandalRepository: PandalRepository,
    private val locationProvider: LocationProvider,
    private val festivalToggleController: FestivalToggleController,
    private val getExitRoutesUseCase: GetExitRoutesUseCase,
    private val toggleFestivalUseCase: ToggleFestivalUseCase
) : ViewModel() {

    val currentLocation = locationProvider.currentLocation
    val activeFestivalContext = festivalToggleController.activeFestivalContext

    val pandals: StateFlow<List<Pandal>> = pandalRepository.getAllPandals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _nightSafetyMode = MutableStateFlow(false)
    val nightSafetyMode: StateFlow<Boolean> = _nightSafetyMode.asStateFlow()

    private val _exitRoutes = MutableStateFlow<List<ExitRoute>>(emptyList())
    val exitRoutes: StateFlow<List<ExitRoute>> = _exitRoutes.asStateFlow()

    private val _selectedPandalId = MutableStateFlow<String?>(null)
    val selectedPandalId: StateFlow<String?> = _selectedPandalId.asStateFlow()

    init {
        viewModelScope.launch {
            locationProvider.startLocationUpdates()
        }
    }

    fun toggleNightSafety() {
        _nightSafetyMode.value = !_nightSafetyMode.value
    }

    fun onGetMeOut() {
        viewModelScope.launch {
            _exitRoutes.value = getExitRoutesUseCase.getNearestExits()
        }
    }

    fun onPandalSelected(pandalId: String) {
        _selectedPandalId.value = pandalId
    }

    fun toggleFestival(festival: Festival, year: Int) {
        viewModelScope.launch {
            toggleFestivalUseCase(festival, year)
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationProvider.stopLocationUpdates()
    }
}
