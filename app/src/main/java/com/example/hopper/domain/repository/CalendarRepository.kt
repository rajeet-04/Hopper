package com.example.hopper.domain.repository

import com.example.hopper.domain.model.Festival
import com.example.hopper.domain.model.Tithi
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for festival calendar and tithi operations.
 * Provides reactive streams of tithi data filtered by festival context.
 */
interface CalendarRepository {

    /**
     * Observes tithis for the currently active festival context.
     * Automatically updates when the active festival/year changes.
     */
    fun getTithisForActiveFestival(): Flow<List<Tithi>>

    /**
     * Returns tithis for a specific festival and year combination.
     */
    fun getTithisByFestivalAndYear(festival: Festival, year: Int): Flow<List<Tithi>>

    /**
     * Returns the tithi matching today's date, or null if none exists.
     */
    suspend fun getCurrentTithi(): Tithi?

    /**
     * Observes tithis marked as peak crowd days for the active festival context.
     */
    fun getPeakCrowdDays(): Flow<List<Tithi>>
}
