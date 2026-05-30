package com.example.hopper.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    mapEngineController: MapLibreEngineController,
    onPandalClick: (String) -> Unit = {},
    onExitNodeClick: (String) -> Unit = {}
) {
    val nightSafetyMode by viewModel.nightSafetyMode.collectAsState()
    val exitRoutes by viewModel.exitRoutes.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        MapLibreMapView(
            modifier = Modifier.fillMaxSize(),
            mapEngineController = mapEngineController,
            onMapReady = {
                mapEngineController.onPandalPinClick { pandalId ->
                    viewModel.onPandalSelected(pandalId)
                    onPandalClick(pandalId)
                }
                mapEngineController.onExitNodePinClick(onExitNodeClick)
            }
        )

        // "Get Me Out" button
        Button(
            onClick = { viewModel.onGetMeOut() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Get Me Out")
        }
    }
}
