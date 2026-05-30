package com.example.hopper.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hopper.domain.model.CrowdBucket
import com.example.hopper.domain.model.Pandal
import com.example.hopper.domain.repository.CrowdReportRepository
import com.example.hopper.domain.repository.PandalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel backing the [PandalDetailSheet]. Loads a [Pandal] by id and exposes
 * the aggregated crowd bucket for the currently selected pandal.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PandalDetailViewModel @Inject constructor(
    private val pandalRepository: PandalRepository,
    private val crowdReportRepository: CrowdReportRepository
) : ViewModel() {

    private val _pandal = MutableStateFlow<Pandal?>(null)
    val pandal: StateFlow<Pandal?> = _pandal.asStateFlow()

    private val selectedPandalId = MutableStateFlow<String?>(null)

    /**
     * Aggregated crowd bucket for the currently selected pandal. Emits null when
     * no pandal is selected or no active reports exist.
     */
    val crowdBucket: StateFlow<CrowdBucket?> = selectedPandalId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else crowdReportRepository.getAggregatedCrowd(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    /**
     * Loads the pandal with the given id and begins observing its crowd level.
     */
    fun loadPandal(pandalId: String) {
        selectedPandalId.value = pandalId
        viewModelScope.launch {
            _pandal.value = pandalRepository.getPandalById(pandalId)
        }
    }
}
