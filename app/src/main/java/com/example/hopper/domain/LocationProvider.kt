package com.example.hopper.domain

import com.example.hopper.domain.model.LatLng
import kotlinx.coroutines.flow.StateFlow

/**
 * Provides the device's current location as observable state flows.
 *
 * Implementations handle GPS polling, stationary detection, and power-efficient
 * location updates.
 */
interface LocationProvider {

    /** The most recent device location, or null if unavailable. */
    val currentLocation: StateFlow<LatLng?>

    /** Whether location services are currently available (permissions granted and GPS enabled). */
    val isLocationAvailable: StateFlow<Boolean>

    /** Begin requesting location updates from the underlying provider. */
    fun startLocationUpdates()

    /** Stop all location updates and release resources. */
    fun stopLocationUpdates()
}
