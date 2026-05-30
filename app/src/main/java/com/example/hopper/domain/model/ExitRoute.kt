package com.example.hopper.domain.model

/**
 * Represents a walking route from a pandal to an exit node,
 * including the polyline path and estimated walking time.
 */
data class ExitRoute(
    val exitNode: ExitNode,
    val distanceMeters: Double,
    val estimatedWalkingMinutes: Int,
    val polyline: List<LatLng>,
    val isAlternate: Boolean
)
