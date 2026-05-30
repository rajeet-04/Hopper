package com.example.hopper.ui.bhog

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hopper.data.local.db.entity.BhogPinEntity
import com.example.hopper.domain.LocationProvider
import com.example.hopper.domain.model.LatLng
import com.example.hopper.domain.usecase.GetBhogPinsUseCase
import com.example.hopper.domain.usecase.SubmitBhogReportUseCase
import com.example.hopper.util.DeviceHashUtil
import com.example.hopper.util.HaversineCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for the Bhog Finder bottom sheet.
 *
 * Observes community-reported food pins for the selected category and sorts
 * them by distance from the device's current location (computed offline via
 * the Haversine formula). Lets users drop a quick report at their location;
 * no account is required because reports are attributed by a privacy-preserving
 * device hash.
 */
@HiltViewModel
class BhogFinderViewModel @Inject constructor(
    private val getBhogPinsUseCase: GetBhogPinsUseCase,
    private val submitBhogReportUseCase: SubmitBhogReportUseCase,
    private val locationProvider: LocationProvider,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _category = MutableStateFlow(CATEGORY_BHOG_DISTRIBUTION)
    val category: StateFlow<String> = _category.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val pins: StateFlow<List<BhogPinEntity>> =
        _category
            .flatMapLatest { category -> getBhogPinsUseCase(category) }
            .map { pins -> pins.sortedByDistance() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Switches the active category and refreshes the pin list. */
    fun setCategory(category: String) {
        _category.value = category
    }

    /**
     * Drops a quick food pin at the current location for the active category.
     * The device hash is derived locally and never exposes the raw identifier.
     */
    fun submitQuickReport(name: String) {
        val location = locationProvider.currentLocation.value ?: return
        viewModelScope.launch {
            val deviceHash = DeviceHashUtil.getDeviceHash(context)
            val pin = BhogPinEntity(
                id = UUID.randomUUID().toString(),
                name = name,
                description = null,
                category = _category.value,
                latitude = location.latitude,
                longitude = location.longitude,
                pandalId = null,
                reportedByDeviceHash = deviceHash,
                reportedAtEpochMs = System.currentTimeMillis(),
                endTimeEpochMs = null,
                rating = null,
                isSynced = false
            )
            submitBhogReportUseCase(pin)
        }
    }

    private fun List<BhogPinEntity>.sortedByDistance(): List<BhogPinEntity> {
        val current = locationProvider.currentLocation.value ?: return this
        return sortedBy { pin ->
            HaversineCalculator.distanceMeters(
                from = current,
                to = LatLng(pin.latitude, pin.longitude)
            )
        }
    }

    companion object {
        const val CATEGORY_BHOG_DISTRIBUTION = "BHOG_DISTRIBUTION"
        const val CATEGORY_STREET_FOOD = "STREET_FOOD"
    }
}
