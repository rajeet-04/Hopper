package com.example.hopper.ui.lostperson

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hopper.data.local.db.entity.LostPersonPostEntity
import com.example.hopper.domain.LocationProvider
import com.example.hopper.domain.repository.LostPersonRepository
import com.example.hopper.domain.usecase.GetNearbyLostPersonPostsUseCase
import com.example.hopper.domain.usecase.SubmitLostPersonPostUseCase
import com.example.hopper.util.DeviceHashUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Lost Person board.
 *
 * Observes active lost-person posts within a 2km radius of the device and lets
 * users post their own location or mark a post resolved. No account is required;
 * posts are attributed by a privacy-preserving device hash.
 */
@HiltViewModel
class LostPersonBoardViewModel @Inject constructor(
    getNearbyLostPersonPostsUseCase: GetNearbyLostPersonPostsUseCase,
    private val submitLostPersonPostUseCase: SubmitLostPersonPostUseCase,
    private val lostPersonRepository: LostPersonRepository,
    private val locationProvider: LocationProvider,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val posts: StateFlow<List<LostPersonPostEntity>> =
        getNearbyLostPersonPostsUseCase(RADIUS_METERS)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Posts the user's current location to the board. The device hash is derived
     * locally and never exposes the raw device identifier.
     */
    fun postMyLocation(displayName: String, description: String) {
        val location = locationProvider.currentLocation.value ?: return
        viewModelScope.launch {
            val deviceHash = DeviceHashUtil.getDeviceHash(context)
            submitLostPersonPostUseCase(displayName, description, location, deviceHash)
        }
    }

    /** Marks the given post as resolved. */
    fun resolvePost(id: String) {
        viewModelScope.launch {
            lostPersonRepository.resolvePost(id)
        }
    }

    companion object {
        private const val RADIUS_METERS = 2000.0
    }
}
