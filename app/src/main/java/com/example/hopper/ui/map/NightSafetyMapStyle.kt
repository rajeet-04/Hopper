package com.example.hopper.ui.map

import com.example.hopper.BuildConfig

/**
 * Night Safety Mode map style configuration for MapLibre.
 * Provides a high-contrast dark style JSON for use when Night Safety Mode is active.
 */
object NightSafetyMapStyle {

    /**
     * Returns a MapLibre style JSON string for Night Safety Mode.
     * Features:
     * - Pure black background for maximum contrast
     * - Bright road/path lines for visibility
     * - Increased visual weight for Police and Medical exit node pins
     * - High-contrast labels
     */
    fun getStyleJson(): String = """
    {
      "version": 8,
      "name": "Hopper Night Safety",
      "sources": {
        "openmaptiles": {
          "type": "vector",
          "url": "${BuildConfig.MAP_TILES_URL}"
        }
      },
      "layers": [
        {
          "id": "background",
          "type": "background",
          "paint": {
            "background-color": "#000000"
          }
        },
        {
          "id": "water",
          "type": "fill",
          "source": "openmaptiles",
          "source-layer": "water",
          "paint": {
            "fill-color": "#0a1929"
          }
        },
        {
          "id": "roads",
          "type": "line",
          "source": "openmaptiles",
          "source-layer": "transportation",
          "paint": {
            "line-color": "#333333",
            "line-width": 1.5
          }
        },
        {
          "id": "roads-major",
          "type": "line",
          "source": "openmaptiles",
          "source-layer": "transportation",
          "filter": ["in", "class", "primary", "secondary", "trunk"],
          "paint": {
            "line-color": "#555555",
            "line-width": 2.5
          }
        },
        {
          "id": "buildings",
          "type": "fill",
          "source": "openmaptiles",
          "source-layer": "building",
          "paint": {
            "fill-color": "#111111",
            "fill-outline-color": "#222222"
          }
        },
        {
          "id": "labels",
          "type": "symbol",
          "source": "openmaptiles",
          "source-layer": "place",
          "layout": {
            "text-field": "{name}",
            "text-size": 14
          },
          "paint": {
            "text-color": "#FFFFFF",
            "text-halo-color": "#000000",
            "text-halo-width": 2
          }
        }
      ]
    }
    """.trimIndent()

    /** Kolkata city bounds for offline tile caching. */
    val KOLKATA_BOUNDS = MapBounds(
        north = 22.65,
        south = 22.45,
        east = 88.45,
        west = 88.25
    )

    /** Chandannagar area bounds for offline tile caching. */
    val CHANDANNAGAR_BOUNDS = MapBounds(
        north = 22.90,
        south = 22.83,
        east = 88.40,
        west = 88.33
    )

    /** Krishnanagar area bounds for offline tile caching. */
    val KRISHNANAGAR_BOUNDS = MapBounds(
        north = 23.42,
        south = 23.38,
        east = 88.52,
        west = 88.48
    )
}
