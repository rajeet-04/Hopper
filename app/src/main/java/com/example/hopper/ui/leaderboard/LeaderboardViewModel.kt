package com.example.hopper.ui.leaderboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hopper.data.local.db.entity.ReputationEntity
import com.example.hopper.domain.repository.ReputationRepository
import com.example.hopper.util.DeviceHashUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Community Leaderboard screen.
 *
 * Exposes the anonymized reputation leaderboard along with the current
 * device's own reputation status. No personal identity is ever surfaced —
 * reputations are keyed only by an opaque device hash.
 */
@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    reputationRepository: ReputationRepository,
    @ApplicationContext context: Context
) : ViewModel() {

    val leaderboard: StateFlow<List<ReputationEntity>> = reputationRepository.getLeaderboard()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _myReputation = MutableStateFlow<ReputationEntity?>(null)
    val myReputation: StateFlow<ReputationEntity?> = _myReputation.asStateFlow()

    init {
        val deviceHash = DeviceHashUtil.getDeviceHash(context)
        viewModelScope.launch {
            _myReputation.value = reputationRepository.getReputation(deviceHash)
        }
    }
}
