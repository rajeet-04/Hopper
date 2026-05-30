package com.example.hopper.data.remote.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class PandalDto(
    val id: String,
    val name: String,
    val nameBengali: String? = null,
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val neighborhood: String? = null,
    val festival: String,
    val year: Int,
    val theme: String? = null,
    val committeeName: String? = null,
    val establishedYear: Int? = null,
    val significanceRank: Int = 0,
    val sourceType: String = "UNKNOWN",
    val confidenceLevel: String = "LOW"
)
