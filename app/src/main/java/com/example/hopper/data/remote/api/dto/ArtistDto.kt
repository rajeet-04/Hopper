package com.example.hopper.data.remote.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ArtistDto(
    val id: String,
    val name: String,
    val nameBengali: String? = null,
    val specialty: String? = null,
    val pandalIds: List<String> = emptyList()
)
