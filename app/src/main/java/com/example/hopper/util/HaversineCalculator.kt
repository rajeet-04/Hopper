package com.example.hopper.util

import com.example.hopper.domain.model.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Calculates great-circle distances between geographic coordinates
 * using the Haversine formula.
 *
 * Used for "nearest pandal" and "nearest exit node" features where
 * distances are computed entirely offline from cached GPS coordinates.
 */
object HaversineCalculator {

    /** Earth's mean radius in meters. */
    private const val EARTH_RADIUS_METERS = 6_371_000.0

    /**
     * Calculates the great-circle distance between two points on Earth.
     *
     * @param from Starting coordinate
     * @param to Ending coordinate
     * @return Distance in meters between the two points
     */
    fun distanceMeters(from: LatLng, to: LatLng): Double {
        val lat1Rad = Math.toRadians(from.latitude)
        val lat2Rad = Math.toRadians(to.latitude)
        val deltaLat = Math.toRadians(to.latitude - from.latitude)
        val deltaLng = Math.toRadians(to.longitude - from.longitude)

        val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(deltaLng / 2) * sin(deltaLng / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS_METERS * c
    }

    /**
     * Calculates the great-circle distance in kilometers.
     *
     * @param from Starting coordinate
     * @param to Ending coordinate
     * @return Distance in kilometers between the two points
     */
    fun distanceKm(from: LatLng, to: LatLng): Double {
        return distanceMeters(from, to) / 1000.0
    }
}
