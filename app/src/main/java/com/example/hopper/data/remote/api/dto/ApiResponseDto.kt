package com.example.hopper.data.remote.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponseDto<T>(
    val data: T,
    val meta: MetaDto
)

@Serializable
data class MetaDto(
    val timestamp: Long,
    val version: String? = null,
    val count: Int? = null
)
