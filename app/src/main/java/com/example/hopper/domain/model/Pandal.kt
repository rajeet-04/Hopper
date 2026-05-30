package com.example.hopper.domain.model

/**
 * Represents a temporary decorated structure housing a deity idol during the festival.
 * This is the primary venue visitors navigate to.
 */
data class Pandal(
    val id: String,
    val name: String,
    val nameBengali: String?,
    val location: LatLng,
    val city: String,
    val neighborhood: String?,
    val festival: Festival,
    val year: Int,
    val theme: String?,
    val committeeName: String?,
    val establishedYear: Int?,
    val artisanCredits: ArtisanCredits?,
    val awards: List<String>,
    val photos: List<String>,
    val significanceRank: Int,
    val sourceType: SourceType,
    val confidenceLevel: ConfidenceLevel
)
