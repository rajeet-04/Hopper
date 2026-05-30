package com.example.hopper.data.local

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.Task
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class LocationProviderImplTest {

    private lateinit var context: Context
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationProvider: LocationProviderImpl
    private lateinit var locationManager: LocationManager

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        fusedLocationClient = mockk(relaxed = true)
        locationManager = mockk(relaxed = true)

        every { context.getSystemService(Context.LOCATION_SERVICE) } returns locationManager

        mockkStatic(Looper::class)
        every { Looper.getMainLooper() } returns mockk(relaxed = true)

        val removeTask = mockk<Task<Void>>(relaxed = true)
        every { fusedLocationClient.removeLocationUpdates(any<LocationCallback>()) } returns removeTask
    }

    @Test
    fun `startLocationUpdates emits false when permission not granted`() {
        every { context.checkPermission(any(), any(), any()) } returns PackageManager.PERMISSION_DENIED

        mockkStatic(androidx.core.content.ContextCompat::class)
        every {
            androidx.core.content.ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        } returns PackageManager.PERMISSION_DENIED
        every {
            androidx.core.content.ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        } returns PackageManager.PERMISSION_DENIED

        locationProvider = LocationProviderImpl(context, fusedLocationClient)
        locationProvider.startLocationUpdates()

        assertThat(locationProvider.isLocationAvailable.value).isFalse()
        assertThat(locationProvider.currentLocation.value).isNull()
    }

    @Test
    fun `startLocationUpdates emits false when GPS disabled`() {
        mockkStatic(androidx.core.content.ContextCompat::class)
        every {
            androidx.core.content.ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        } returns PackageManager.PERMISSION_GRANTED
        every {
            androidx.core.content.ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        } returns PackageManager.PERMISSION_GRANTED

        every { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } returns false
        every { locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) } returns false

        locationProvider = LocationProviderImpl(context, fusedLocationClient)
        locationProvider.startLocationUpdates()

        assertThat(locationProvider.isLocationAvailable.value).isFalse()
        assertThat(locationProvider.currentLocation.value).isNull()
    }

    @Test
    fun `startLocationUpdates requests location when permissions and GPS available`() {
        mockkStatic(androidx.core.content.ContextCompat::class)
        every {
            androidx.core.content.ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        } returns PackageManager.PERMISSION_GRANTED
        every {
            androidx.core.content.ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        } returns PackageManager.PERMISSION_GRANTED

        every { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } returns true

        val requestTask = mockk<Task<Void>>(relaxed = true)
        every {
            fusedLocationClient.requestLocationUpdates(any<LocationRequest>(), any<LocationCallback>(), any())
        } returns requestTask

        locationProvider = LocationProviderImpl(context, fusedLocationClient)
        locationProvider.startLocationUpdates()

        assertThat(locationProvider.isLocationAvailable.value).isTrue()
        verify {
            fusedLocationClient.requestLocationUpdates(any<LocationRequest>(), any<LocationCallback>(), any())
        }
    }

    @Test
    fun `stopLocationUpdates removes callback`() {
        locationProvider = LocationProviderImpl(context, fusedLocationClient)
        locationProvider.stopLocationUpdates()

        verify { fusedLocationClient.removeLocationUpdates(any<LocationCallback>()) }
    }

    @Test
    fun `initial state has null location and false availability`() {
        locationProvider = LocationProviderImpl(context, fusedLocationClient)

        assertThat(locationProvider.currentLocation.value).isNull()
        assertThat(locationProvider.isLocationAvailable.value).isFalse()
    }
}
