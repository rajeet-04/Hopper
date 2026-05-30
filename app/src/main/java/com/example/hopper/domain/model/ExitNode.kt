package com.example.hopper.domain.model

/**
 * Represents a pre-seeded emergency exit point such as a Metro station,
 * Railway station, Police booth, or Medical camp.
 */
data class ExitNode(
    val id: String,
    val name: String,
    val nameBengali: String?,
    val category: ExitNodeCategory,
    val location: LatLng,
    val contactNumber: String?,
    val is24Hr: Boolean,
    val isWellLit: Boolean
)
