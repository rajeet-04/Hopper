package com.example.hopper.domain.model

import java.time.LocalDate

/**
 * Represents a Hindu calendar date (lunar day) with cultural significance.
 * Used to determine festival dates and ritual timings.
 */
data class Tithi(
    val id: String,
    val festival: Festival,
    val year: Int,
    val name: String,
    val nameBengali: String,
    val date: LocalDate,
    val significance: String?,
    val significanceBengali: String?,
    val isPeakCrowd: Boolean
)
