package com.example.hopper.data.remote.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class CrowdReportDto(
    val pandalId: String,
    val bucket: String,
    val deviceHash: String,
    val reportedAt: Long
)

@Serializable
data class AggregatedCrowdDto(
    val pandalId: String,
    val bucket: String,
    val reportCount: Int
)
