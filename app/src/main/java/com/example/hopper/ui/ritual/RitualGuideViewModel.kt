package com.example.hopper.ui.ritual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hopper.data.local.db.entity.RitualGuideEntity
import com.example.hopper.domain.usecase.GetRitualGuidesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel for the Ritual Guides screen.
 *
 * Exposes the festival-scoped list of ritual guides. The underlying use case
 * filters to the active festival and year.
 */
@HiltViewModel
class RitualGuideViewModel @Inject constructor(
    getRitualGuidesUseCase: GetRitualGuidesUseCase
) : ViewModel() {

    val guides: StateFlow<List<RitualGuideEntity>> = getRitualGuidesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
