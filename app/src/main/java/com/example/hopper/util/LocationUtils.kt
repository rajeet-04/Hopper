package com.example.hopper.util

import com.example.hopper.domain.model.LatLng

/**
 * Coordinate helper utilities for location-based features.
 *
 * Provides radius checks, distance formatting, and coordinate validation
 * used across the "Puja Near Me", exit routing, and crowd reporting features.
 */
object LocationUtils {

    /** Average walking speed in meters per second (approximately 5 km/h). */
    private const val WALKING_SPEED_MPS = 1.389 // 5 km/h

    /**
     * Checks whether a point is within a given radius of a center point.
     *
     * @param point The point to check
     * @param center The center of the radius
     * @param radiusMeters The radius in meters
     * @return true if the point is within the specified radius
     */
    fun isWithinRadius(point: LatLng, center: LatLng, radiusMeters: Double): Boolean {
        return HaversineCalculator.distanceMeters(point, center) <= radiusMeters
    }

    /**
     * Formats a distance in meters as a human-readable string.
     *
     * - Distances under 1000m are shown as whole meters (e.g., "450 m")
     * - Distances 1000m and above are shown in km with one decimal (e.g., "2.3 km")
     *
     * @param meters Distance in meters
     * @return Formatted distance string
     */
    fun formatDistance(meters: Double): String {
        return if (meters < 1000) {
            "${meters.toInt()} m"
        } else {
            val km = meters / 1000.0
            "%.1f km".format(km)
        }
    }

    /**
     * Estimates walking time in minutes for a given distance.
     *
     * Uses an average walking speed of 5 km/h.
     *
     * @param distanceMeters Distance in meters
     * @return Estimated walking time in minutes (rounded up)
     */
    fun estimateWalkingMinutes(distanceMeters: Double): Int {
        val seconds = distanceMeters / WALKING_SPEED_MPS
        return kotlin.math.ceil(seconds / 60.0).toInt()
    }

    /**
     * Validates that a LatLng has valid coordinate values.
     *
     * @param latLng The coordinate to validate
     * @return true if latitude is in [-90, 90] and longitude is in [-180, 180]
     */
    fun isValidCoordinate(latLng: LatLng): Boolean {
        return latLng.latitude in -90.0..90.0 && latLng.longitude in -180.0..180.0
    }

    /**
     * Sorts a list of LatLng points by distance from a reference point (nearest first).
     *
     * @param points The points to sort
     * @param from The reference point to measure distance from
     * @return Points sorted by ascending distance from the reference
     */
    fun sortByDistance(points: List<LatLng>, from: LatLng): List<LatLng> {
        return points.sortedBy { HaversineCalculator.distanceMeters(from, it) }
    }

    /**
     * Finds the nearest point from a list of candidates.
     *
     * @param from The reference point
     * @param candidates The list of candidate points
     * @return The nearest point, or null if candidates is empty
     */
    fun findNearest(from: LatLng, candidates: List<LatLng>): LatLng? {
        return candidates.minByOrNull { HaversineCalculator.distanceMeters(from, it) }
    }
}
