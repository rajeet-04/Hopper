package com.example.hopper.ui.oralhistory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hopper.data.local.db.entity.OralHistoryEntity
import com.example.hopper.domain.repository.OralHistoryRepository
import com.example.hopper.domain.usecase.GetOralHistoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Oral Histories screen.
 *
 * Exposes the festival-scoped list of oral history entries and supports
 * downloading their audio for offline playback.
 */
@HiltViewModel
class OralHistoryViewModel @Inject constructor(
    getOralHistoriesUseCase: GetOralHistoriesUseCase,
    private val oralHistoryRepository: OralHistoryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val histories: StateFlow<List<OralHistoryEntity>> = getOralHistoriesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Downloads the audio for the given oral history into the app's files
     * directory so it can be played back offline.
     */
    fun downloadAudio(id: String, url: String) {
        viewModelScope.launch {
            oralHistoryRepository.downloadAudio(id, url, context.filesDir)
        }
    }
}
