package com.example.hopper.data.local

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.hopper.domain.LocationProvider
import com.example.hopper.domain.model.LatLng
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [LocationProvider] using Google Play Services FusedLocationProviderClient.
 *
 * Features:
 * - 10-second active polling interval with HIGH_ACCURACY priority
 * - Stationary detection: switches to passive mode (60s interval) after 2 minutes of no movement (< 5m)
 * - Resumes active mode when movement (> 5m) is detected
 */
@Singleton
class LocationProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationProvider {

    companion object {
        private const val ACTIVE_INTERVAL_MS = 10_000L
        private const val ACTIVE_FASTEST_INTERVAL_MS = 5_000L
        private const val PASSIVE_INTERVAL_MS = 60_000L
        private const val PASSIVE_FASTEST_INTERVAL_MS = 30_000L
        private const val STATIONARY_THRESHOLD_METERS = 5f
        private const val STATIONARY_TIMEOUT_MS = 2 * 60 * 1000L // 2 minutes
    }

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    override val currentLocation: StateFlow<LatLng?> = _currentLocation.asStateFlow()

    private val _isLocationAvailable = MutableStateFlow(false)
    override val isLocationAvailable: StateFlow<Boolean> = _isLocationAvailable.asStateFlow()

    private var lastSignificantLocation: android.location.Location? = null
    private var lastSignificantLocationTime: Long = 0L
    private var isInPassiveMode = false
    private var isReceivingUpdates = false

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation ?: return
            _currentLocation.value = LatLng(location.latitude, location.longitude)
            _isLocationAvailable.value = true
            handleStationaryDetection(location)
        }
    }

    override fun startLocationUpdates() {
        if (!hasLocationPermission()) {
            _isLocationAvailable.value = false
            _currentLocation.value = null
            return
        }

        if (!isGpsEnabled()) {
            _isLocationAvailable.value = false
            _currentLocation.value = null
            return
        }

        _isLocationAvailable.value = true
        isInPassiveMode = false
        lastSignificantLocation = null
        lastSignificantLocationTime = 0L
        requestLocationUpdates(createActiveLocationRequest())
    }

    override fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        isReceivingUpdates = false
        isInPassiveMode = false
        lastSignificantLocation = null
        lastSignificantLocationTime = 0L
    }

    private fun handleStationaryDetection(location: android.location.Location) {
        val now = System.currentTimeMillis()

        if (lastSignificantLocation == null) {
            lastSignificantLocation = location
            lastSignificantLocationTime = now
            return
        }

        val distance = location.distanceTo(lastSignificantLocation!!)

        if (distance > STATIONARY_THRESHOLD_METERS) {
            // Movement detected — update reference point and switch to active if needed
            lastSignificantLocation = location
            lastSignificantLocationTime = now
            if (isInPassiveMode) {
                switchToActiveMode()
            }
        } else {
            // No significant movement — check if stationary timeout exceeded
            val elapsed = now - lastSignificantLocationTime
            if (elapsed >= STATIONARY_TIMEOUT_MS && !isInPassiveMode) {
                switchToPassiveMode()
            }
        }
    }

    private fun switchToPassiveMode() {
        isInPassiveMode = true
        fusedLocationClient.removeLocationUpdates(locationCallback)
        requestLocationUpdates(createPassiveLocationRequest())
    }

    private fun switchToActiveMode() {
        isInPassiveMode = false
        fusedLocationClient.removeLocationUpdates(locationCallback)
        requestLocationUpdates(createActiveLocationRequest())
    }

    @Suppress("MissingPermission")
    private fun requestLocationUpdates(request: LocationRequest) {
        if (!hasLocationPermission()) {
            _isLocationAvailable.value = false
            _currentLocation.value = null
            return
        }
        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
        isReceivingUpdates = true
    }

    private fun createActiveLocationRequest(): LocationRequest {
        return LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, ACTIVE_INTERVAL_MS)
            .setMinUpdateIntervalMillis(ACTIVE_FASTEST_INTERVAL_MS)
            .build()
    }

    private fun createPassiveLocationRequest(): LocationRequest {
        return LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, PASSIVE_INTERVAL_MS)
            .setMinUpdateIntervalMillis(PASSIVE_FASTEST_INTERVAL_MS)
            .build()
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isGpsEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
            locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
    }
}
