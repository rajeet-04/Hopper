package com.example.hopper.ui.map

import com.example.hopper.domain.model.CrowdBucket
import com.example.hopper.domain.model.ExitNode
import com.example.hopper.domain.model.LatLng
import com.example.hopper.domain.model.Pandal

interface MapEngineController {
    fun initialize(onMapReady: () -> Unit)
    fun setCenter(location: LatLng, zoom: Double = 14.0)
    fun setUserLocation(location: LatLng)
    fun addPandalPins(pandals: List<Pandal>, crowdBuckets: Map<String, CrowdBucket>)
    fun addExitNodePins(exitNodes: List<ExitNode>)
    fun showRoutePolyline(coordinates: List<LatLng>, isWellLit: Boolean = true)
    fun clearRoutePolylines()
    fun setNightSafetyStyle(enabled: Boolean)
    fun loadOfflineRegion(bounds: MapBounds, minZoom: Int = 10, maxZoom: Int = 16)
    fun setGeoJsonSource(sourceId: String, geoJson: String)
    fun clearAllOverlays()
    fun onPandalPinClick(callback: (pandalId: String) -> Unit)
    fun onExitNodePinClick(callback: (exitNodeId: String) -> Unit)
    fun destroy()
}

data class MapBounds(
    val north: Double,
    val south: Double,
    val east: Double,
    val west: Double
)
