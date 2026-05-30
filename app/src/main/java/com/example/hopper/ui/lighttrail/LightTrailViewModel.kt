package com.example.hopper.ui.lighttrail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hopper.data.local.db.entity.LightTrailEntity
import com.example.hopper.domain.usecase.GetLightTrailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel for the Chandannagar Light Trail screen.
 *
 * Exposes the festival-gated list of light trail stops sorted by their
 * sequence order. The underlying use case already filters to Jagaddhatri Puja,
 * so an empty list indicates the feature is unavailable for the active festival.
 */
@HiltViewModel
class LightTrailViewModel @Inject constructor(
    getLightTrailUseCase: GetLightTrailUseCase
) : ViewModel() {

    val trailStops: StateFlow<List<LightTrailEntity>> = getLightTrailUseCase()
        .map { stops -> stops.sortedBy { it.sequenceOrder } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
