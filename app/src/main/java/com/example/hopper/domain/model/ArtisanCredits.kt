package com.example.hopper.domain.model

/**
 * Artist/craftsman attribution for pandal construction.
 * Tracks the key artisans responsible for different aspects of the pandal.
 */
data class ArtisanCredits(
    val idolMaker: String?,
    val lightingDesigner: String?,
    val themeDesigner: String?
)
