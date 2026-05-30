package com.example.hopper.domain

import com.example.hopper.domain.model.Festival
import com.example.hopper.domain.model.FestivalContext
import kotlinx.coroutines.flow.StateFlow

/**
 * Controls the active festival and year context for the application.
 * All repository queries filter by the active FestivalContext.
 * The controller is reactive — when the user toggles festival, all dependent
 * queries automatically update via StateFlow observation.
 */
interface FestivalToggleController {
    /**
     * The currently active festival context (festival + year).
     * UI and repositories observe this to filter data accordingly.
     */
    val activeFestivalContext: StateFlow<FestivalContext>

    /**
     * Sets the active festival and year, persisting the selection.
     */
    suspend fun setFestival(festival: Festival, year: Int)

    /**
     * Calculates the default festival context based on proximity
     * to festival dates from the bundled calendar.
     * Returns the nearest upcoming (or most recently passed) festival
     * with the appropriate year.
     */
    suspend fun getDefaultFestival(): FestivalContext
}
