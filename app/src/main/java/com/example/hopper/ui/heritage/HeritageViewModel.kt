package com.example.hopper.ui.heritage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hopper.data.local.db.entity.HeritagePointEntity
import com.example.hopper.domain.usecase.GetHeritagePointsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel exposing the festival-gated heritage points of interest.
 *
 * The underlying use case already filters to the active festival, so an
 * empty list indicates heritage content is unavailable for the selection.
 */
@HiltViewModel
class HeritageViewModel @Inject constructor(
    getHeritagePointsUseCase: GetHeritagePointsUseCase
) : ViewModel() {

    val heritagePoints: StateFlow<List<HeritagePointEntity>> = getHeritagePointsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
