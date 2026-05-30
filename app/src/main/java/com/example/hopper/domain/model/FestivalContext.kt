package com.example.hopper.domain.model

/**
 * Represents the active festival and year combination.
 * All repository queries filter by this context.
 */
data class FestivalContext(
    val festival: Festival,
    val year: Int
)
