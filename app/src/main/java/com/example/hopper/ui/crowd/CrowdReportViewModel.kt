package com.example.hopper.ui.crowd

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hopper.domain.model.CrowdBucket
import com.example.hopper.domain.repository.CrowdReportRepository
import com.example.hopper.domain.repository.ReputationRepository
import com.example.hopper.domain.usecase.SubmitCrowdReportUseCase
import com.example.hopper.util.DeviceHashUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Result of a crowd report submission attempt, surfaced to the UI.
 */
sealed interface SubmitState {
    data object Idle : SubmitState
    data object Success : SubmitState
    data object RateLimited : SubmitState
    data class Error(val message: String) : SubmitState
}

/**
 * ViewModel backing the [CrowdReportSheet]. Submits crowd reports, surfaces
 * submission state (including rate-limit feedback), and exposes the reporter's
 * current reputation badge tier.
 */
@HiltViewModel
class CrowdReportViewModel @Inject constructor(
    private val submitCrowdReportUseCase: SubmitCrowdReportUseCase,
    private val crowdReportRepository: CrowdReportRepository,
    private val reputationRepository: ReputationRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState: StateFlow<SubmitState> = _submitState.asStateFlow()

    private val _badgeTier = MutableStateFlow(DEFAULT_BADGE_TIER)
    val badgeTier: StateFlow<String> = _badgeTier.asStateFlow()

    private val deviceHash: String by lazy { DeviceHashUtil.getDeviceHash(context) }

    init {
        loadBadgeTier()
    }

    private fun loadBadgeTier() {
        viewModelScope.launch {
            val reputation = reputationRepository.getReputation(deviceHash)
            _badgeTier.value = reputation?.badgeTier ?: DEFAULT_BADGE_TIER
        }
    }

    /**
     * Submits a crowd report for the given pandal. On failure, distinguishes a
     * rate-limited rejection from a generic error.
     */
    fun submitReport(pandalId: String, bucket: CrowdBucket) {
        viewModelScope.launch {
            val result = submitCrowdReportUseCase(pandalId, bucket)
            _submitState.value = result.fold(
                onSuccess = { SubmitState.Success },
                onFailure = { error ->
                    if (crowdReportRepository.isRateLimited(pandalId, deviceHash)) {
                        SubmitState.RateLimited
                    } else {
                        SubmitState.Error(error.message ?: "Could not submit report")
                    }
                }
            )
        }
    }

    private companion object {
        const val DEFAULT_BADGE_TIER = "Newcomer"
    }
}
