package com.example.hopper.data.remote.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class TithiDto(
    val id: String,
    val festival: String,
    val year: Int,
    val name: String,
    val nameBengali: String? = null,
    val date: Long,
    val culturalSignificance: String? = null,
    val isPeakCrowd: Boolean = false
)
