package com.example.hopper.domain.model

import java.time.Instant

/**
 * Represents a user-submitted crowd level report for a pandal.
 * Privacy-preserving: uses device hash instead of PII.
 */
data class CrowdReport(
    val id: String,
    val pandalId: String,
    val bucket: CrowdBucket,
    val deviceHash: String,
    val reportedAt: Instant,
    val expiresAt: Instant,
    val isSynced: Boolean
)
