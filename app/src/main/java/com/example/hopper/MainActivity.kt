package com.example.hopper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.hopper.ui.map.MapLibreEngineController
import com.example.hopper.ui.navigation.HopperNavGraph
import com.example.hopper.ui.theme.HopperTheme
import com.example.hopper.util.LocaleManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var localeManager: LocaleManager

    @Inject
    lateinit var mapEngineController: MapLibreEngineController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HopperTheme(localeManager = localeManager) {
                HopperNavGraph(mapEngineController = mapEngineController)
            }
        }
    }
}
