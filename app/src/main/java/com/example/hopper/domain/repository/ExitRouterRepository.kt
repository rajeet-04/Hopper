package com.example.hopper.domain.repository

import com.example.hopper.domain.model.ExitRoute
import com.example.hopper.domain.model.LatLng

/**
 * Repository interface for emergency exit routing operations.
 *
 * Provides nearest exit node lookup per category, pandal-to-exit routing
 * with Night Safety Mode support, and alternate route retrieval.
 */
interface ExitRouterRepository {

    /**
     * Returns the nearest exit node for each category (Metro, Railway, Police, Medical)
     * relative to the user's current location, calculated via Haversine distance.
     *
     * @param userLocation The user's current GPS coordinates
     * @return A list of [ExitRoute] objects, one per category (up to 4)
     */
    suspend fun getNearestExitPerCategory(userLocation: LatLng): List<ExitRoute>

    /**
     * Returns routes from a specific pandal to its connected exit nodes.
     *
     * When [nightSafetyMode] is true, routes along well-lit connectors are
     * sorted first (preferred over shorter unlit routes).
     *
     * @param pandalId The ID of the pandal to route from
     * @param nightSafetyMode Whether to prefer well-lit routes
     * @return A list of [ExitRoute] objects for the pandal's connectors
     */
    suspend fun getRoutesFromPandal(
        pandalId: String,
        nightSafetyMode: Boolean = false
    ): List<ExitRoute>

    /**
     * Returns all available routes (primary + alternates) for a specific
     * pandal-to-exit-node pair. Ensures at least 2 connectors are available
     * per pair to handle blocked routes.
     *
     * @param pandalId The ID of the source pandal
     * @param exitNodeId The ID of the target exit node
     * @return A list of [ExitRoute] objects for the specific pair
     */
    suspend fun getAlternateRoutes(pandalId: String, exitNodeId: String): List<ExitRoute>
}
