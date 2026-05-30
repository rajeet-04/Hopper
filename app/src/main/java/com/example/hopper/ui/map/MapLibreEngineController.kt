package com.example.hopper.ui.map

import android.content.Context
import com.example.hopper.domain.model.CrowdBucket
import com.example.hopper.domain.model.ExitNode
import com.example.hopper.domain.model.ExitNodeCategory
import com.example.hopper.domain.model.LatLng
import com.example.hopper.domain.model.Pandal
import dagger.hilt.android.qualifiers.ApplicationContext
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.offline.OfflineManager
import org.maplibre.android.offline.OfflineRegionDefinition
import org.maplibre.android.offline.OfflineTilePyramidRegionDefinition
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapLibreEngineController @Inject constructor(
    @ApplicationContext private val context: Context
) : MapEngineController {

    private var mapView: MapView? = null
    private var mapLibreMap: MapLibreMap? = null
    private var pandalClickCallback: ((String) -> Unit)? = null
    private var exitNodeClickCallback: ((String) -> Unit)? = null
    private var isNightMode = false

    companion object {
        private const val STYLE_URL = "https://demotiles.maplibre.org/style.json"
        private const val NIGHT_STYLE_URL = "https://demotiles.maplibre.org/style.json" // placeholder
        private const val MAX_OFFLINE_SIZE_BYTES = 50L * 1024 * 1024 // 50MB
    }

    fun attachMapView(mapView: MapView) {
        this.mapView = mapView
    }

    override fun initialize(onMapReady: () -> Unit) {
        MapLibre.getInstance(context)
        mapView?.getMapAsync { map ->
            mapLibreMap = map
            val styleUrl = if (isNightMode) NIGHT_STYLE_URL else STYLE_URL
            map.setStyle(Style.Builder().fromUri(styleUrl)) {
                onMapReady()
            }
        }
    }

    override fun setCenter(location: LatLng, zoom: Double) {
        mapLibreMap?.cameraPosition = CameraPosition.Builder()
            .target(org.maplibre.android.geometry.LatLng(location.latitude, location.longitude))
            .zoom(zoom)
            .build()
    }

    override fun setUserLocation(location: LatLng) {
        // User location marker handled via MapLibre's location component
        mapLibreMap?.let { map ->
            map.cameraPosition = CameraPosition.Builder()
                .target(org.maplibre.android.geometry.LatLng(location.latitude, location.longitude))
                .build()
        }
    }

    override fun addPandalPins(pandals: List<Pandal>, crowdBuckets: Map<String, CrowdBucket>) {
        // Convert pandals to GeoJSON and add as source/layer
        val geoJson = buildPandalGeoJson(pandals, crowdBuckets)
        setGeoJsonSource("pandals-source", geoJson)
    }

    override fun addExitNodePins(exitNodes: List<ExitNode>) {
        val geoJson = buildExitNodeGeoJson(exitNodes)
        setGeoJsonSource("exit-nodes-source", geoJson)
    }

    override fun showRoutePolyline(coordinates: List<LatLng>, isWellLit: Boolean) {
        val geoJson = buildRouteGeoJson(coordinates, isWellLit)
        setGeoJsonSource("route-source", geoJson)
    }

    override fun clearRoutePolylines() {
        setGeoJsonSource("route-source", """{"type":"FeatureCollection","features":[]}""")
    }

    override fun setNightSafetyStyle(enabled: Boolean) {
        isNightMode = enabled
        val styleUrl = if (enabled) NIGHT_STYLE_URL else STYLE_URL
        mapLibreMap?.setStyle(Style.Builder().fromUri(styleUrl))
    }

    override fun loadOfflineRegion(bounds: MapBounds, minZoom: Int, maxZoom: Int) {
        val offlineManager = OfflineManager.getInstance(context)
        val definition = OfflineTilePyramidRegionDefinition(
            STYLE_URL,
            LatLngBounds.Builder()
                .include(org.maplibre.android.geometry.LatLng(bounds.north, bounds.east))
                .include(org.maplibre.android.geometry.LatLng(bounds.south, bounds.west))
                .build(),
            minZoom.toDouble(),
            maxZoom.toDouble(),
            context.resources.displayMetrics.density
        )
        offlineManager.createOfflineRegion(
            definition,
            byteArrayOf(),
            object : OfflineManager.CreateOfflineRegionCallback {
                override fun onCreate(offlineRegion: org.maplibre.android.offline.OfflineRegion) {
                    offlineRegion.setDownloadState(org.maplibre.android.offline.OfflineRegion.STATE_ACTIVE)
                }
                override fun onError(error: String) {}
            }
        )
    }

    override fun setGeoJsonSource(sourceId: String, geoJson: String) {
        mapLibreMap?.style?.let { style ->
            val source = style.getSourceAs<org.maplibre.android.style.sources.GeoJsonSource>(sourceId)
            if (source != null) {
                source.setGeoJson(geoJson)
            } else {
                style.addSource(org.maplibre.android.style.sources.GeoJsonSource(sourceId, geoJson))
            }
        }
    }

    override fun clearAllOverlays() {
        clearRoutePolylines()
        setGeoJsonSource("pandals-source", """{"type":"FeatureCollection","features":[]}""")
        setGeoJsonSource("exit-nodes-source", """{"type":"FeatureCollection","features":[]}""")
    }

    override fun onPandalPinClick(callback: (pandalId: String) -> Unit) {
        pandalClickCallback = callback
    }

    override fun onExitNodePinClick(callback: (exitNodeId: String) -> Unit) {
        exitNodeClickCallback = callback
    }

    override fun destroy() {
        mapView = null
        mapLibreMap = null
        pandalClickCallback = null
        exitNodeClickCallback = null
    }

    private fun buildPandalGeoJson(pandals: List<Pandal>, crowdBuckets: Map<String, CrowdBucket>): String {
        val features = pandals.joinToString(",") { pandal ->
            val bucket = crowdBuckets[pandal.id]?.name ?: "GREEN"
            """{"type":"Feature","geometry":{"type":"Point","coordinates":[${pandal.location.longitude},${pandal.location.latitude}]},"properties":{"id":"${pandal.id}","name":"${pandal.name}","crowd":"$bucket"}}"""
        }
        return """{"type":"FeatureCollection","features":[$features]}"""
    }

    private fun buildExitNodeGeoJson(exitNodes: List<ExitNode>): String {
        val features = exitNodes.joinToString(",") { node ->
            """{"type":"Feature","geometry":{"type":"Point","coordinates":[${node.location.longitude},${node.location.latitude}]},"properties":{"id":"${node.id}","name":"${node.name}","category":"${node.category.name}"}}"""
        }
        return """{"type":"FeatureCollection","features":[$features]}"""
    }

    private fun buildRouteGeoJson(coordinates: List<LatLng>, isWellLit: Boolean): String {
        val coords = coordinates.joinToString(",") { "[${it.longitude},${it.latitude}]" }
        return """{"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"LineString","coordinates":[$coords]},"properties":{"isWellLit":$isWellLit}}]}"""
    }
}
