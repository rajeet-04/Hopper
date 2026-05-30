package com.example.hopper.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.MapLibre
import org.maplibre.android.maps.MapView

@Composable
fun MapLibreMapView(
    modifier: Modifier = Modifier,
    mapEngineController: MapLibreEngineController,
    onMapReady: () -> Unit = {}
) {
    val context = LocalContext.current

    val mapView = remember {
        MapLibre.getInstance(context)
        MapView(context)
    }

    DisposableEffect(mapView) {
        mapView.onCreate(null)
        mapView.onStart()
        mapView.onResume()
        mapEngineController.attachMapView(mapView)
        mapEngineController.initialize(onMapReady)

        onDispose {
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
            mapEngineController.destroy()
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
}
