package com.example.hopper.domain.repository

import com.example.hopper.domain.model.LatLng
import com.example.hopper.domain.model.Pandal
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for pandal data access.
 * All queries are filtered by the active festival/year context.
 */
interface PandalRepository {

    /**
     * Returns the nearest pandals sorted by a composite score combining
     * distance, current crowd level, and significance ranking.
     *
     * @param userLocation The user's current GPS coordinates
     * @param limit Maximum number of pandals to return (default 10)
     * @return Flow emitting the sorted list of nearest pandals
     */
    fun getNearestPandals(userLocation: LatLng, limit: Int = 10): Flow<List<Pandal>>

    /**
     * Returns a single pandal by its unique identifier.
     *
     * @param id The pandal's unique ID
     * @return The pandal if found, null otherwise
     */
    suspend fun getPandalById(id: String): Pandal?

    /**
     * Searches pandals by name (English or Bengali).
     *
     * @param query The search query string
     * @return Flow emitting matching pandals
     */
    fun searchPandals(query: String): Flow<List<Pandal>>

    /**
     * Returns all pandals for the active festival/year context.
     *
     * @return Flow emitting all pandals in the current context
     */
    fun getAllPandals(): Flow<List<Pandal>>
}
