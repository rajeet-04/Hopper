package com.example.hopper.ui.bishorjon

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hopper.data.local.db.entity.ProcessionEntity
import com.example.hopper.domain.FestivalToggleController
import com.example.hopper.domain.model.LatLng
import com.example.hopper.domain.usecase.ProcessionTrackerUseCase
import com.example.hopper.util.DeviceHashUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the bishorjon (immersion) procession tracker.
 *
 * Observes active processions for the currently selected festival year and
 * lets users crowd-report sightings to power proximity alerts.
 */
@HiltViewModel
class BishorjonTrackerViewModel @Inject constructor(
    private val processionTrackerUseCase: ProcessionTrackerUseCase,
    festivalToggleController: FestivalToggleController,
    @ApplicationContext private val context: Context
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val processions: StateFlow<List<ProcessionEntity>> =
        festivalToggleController.activeFestivalContext
            .flatMapLatest { festivalContext ->
                processionTrackerUseCase.observeProcessions(festivalContext.year)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Reports a sighting of the given procession at [location]. The device hash
     * is derived locally and never exposes the raw device identifier.
     */
    fun reportSighting(processionId: String, location: LatLng) {
        viewModelScope.launch {
            val deviceHash = DeviceHashUtil.getDeviceHash(context)
            processionTrackerUseCase.reportSighting(processionId, location, deviceHash)
        }
    }
}
