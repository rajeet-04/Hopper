package com.example.hopper.data.repository

import com.example.hopper.data.local.db.dao.ConnectorDao
import com.example.hopper.data.local.db.dao.ExitNodeDao
import com.example.hopper.data.local.db.entity.ConnectorEntity
import com.example.hopper.data.local.db.entity.ExitNodeEntity
import com.example.hopper.domain.model.ExitNode
import com.example.hopper.domain.model.ExitNodeCategory
import com.example.hopper.domain.model.ExitRoute
import com.example.hopper.domain.model.LatLng
import com.example.hopper.domain.repository.ExitRouterRepository
import com.example.hopper.util.HaversineCalculator
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ceil

/**
 * Implementation of [ExitRouterRepository] providing offline emergency exit routing.
 *
 * Uses precomputed walking connector polylines from Room and Haversine distance
 * calculations for nearest-exit lookups. Walking time is estimated at 5 km/h average.
 */
@Singleton
class ExitRouterRepositoryImpl @Inject constructor(
    private val exitNodeDao: ExitNodeDao,
    private val connectorDao: ConnectorDao
) : ExitRouterRepository {

    companion object {
        /** Average walking speed: 5 km/h = 5000 meters per 60 minutes. */
        private const val WALKING_SPEED_METERS_PER_MINUTE = 5000.0 / 60.0
    }

    override suspend fun getNearestExitPerCategory(userLocation: LatLng): List<ExitRoute> {
        val allNodes = exitNodeDao.getAll().first()

        return allNodes
            .groupBy { it.category }
            .mapNotNull { (_, nodes) ->
                findNearestNode(userLocation, nodes)
            }
    }

    override suspend fun getRoutesFromPandal(
        pandalId: String,
        nightSafetyMode: Boolean
    ): List<ExitRoute> {
        val connectors = connectorDao.getByPandalId(pandalId)
        val allNodes = exitNodeDao.getAll().first()
        val nodeMap = allNodes.associateBy { it.id }

        val routes = connectors.mapNotNull { connector ->
            val exitNodeEntity = nodeMap[connector.exitNodeId] ?: return@mapNotNull null
            mapToExitRoute(connector, exitNodeEntity)
        }

        return if (nightSafetyMode) {
            // Prefer well-lit routes: sort well-lit connectors first, then by distance
            routes.sortedWith(
                compareByDescending<ExitRoute> { it.exitNode.isWellLit }
                    .thenBy { it.distanceMeters }
            )
        } else {
            routes.sortedBy { it.distanceMeters }
        }
    }

    override suspend fun getAlternateRoutes(
        pandalId: String,
        exitNodeId: String
    ): List<ExitRoute> {
        val connectors = connectorDao.getByPandalAndExit(pandalId, exitNodeId)
        val allNodes = exitNodeDao.getAll().first()
        val nodeMap = allNodes.associateBy { it.id }

        val exitNodeEntity = nodeMap[exitNodeId] ?: return emptyList()

        return connectors.map { connector ->
            mapToExitRoute(connector, exitNodeEntity)
        }.sortedBy { it.distanceMeters }
    }

    /**
     * Finds the nearest exit node to the user's location within a group of nodes
     * and returns it as an [ExitRoute].
     */
    private fun findNearestNode(
        userLocation: LatLng,
        nodes: List<ExitNodeEntity>
    ): ExitRoute? {
        if (nodes.isEmpty()) return null

        val nearest = nodes.minByOrNull { node ->
            HaversineCalculator.distanceMeters(
                userLocation,
                LatLng(node.latitude, node.longitude)
            )
        } ?: return null

        val distanceMeters = HaversineCalculator.distanceMeters(
            userLocation,
            LatLng(nearest.latitude, nearest.longitude)
        )

        val walkingMinutes = calculateWalkingTime(distanceMeters)

        return ExitRoute(
            exitNode = nearest.toDomainModel(),
            distanceMeters = distanceMeters,
            estimatedWalkingMinutes = walkingMinutes,
            polyline = listOf(
                userLocation,
                LatLng(nearest.latitude, nearest.longitude)
            ),
            isAlternate = false
        )
    }

    /**
     * Maps a [ConnectorEntity] and its associated [ExitNodeEntity] to an [ExitRoute].
     */
    private fun mapToExitRoute(
        connector: ConnectorEntity,
        exitNodeEntity: ExitNodeEntity
    ): ExitRoute {
        return ExitRoute(
            exitNode = exitNodeEntity.toDomainModel(),
            distanceMeters = connector.distanceMeters,
            estimatedWalkingMinutes = calculateWalkingTime(connector.distanceMeters),
            polyline = parsePolylineJson(connector.polylineJson),
            isAlternate = connector.isAlternate
        )
    }

    /**
     * Calculates estimated walking time in minutes at 5 km/h average speed.
     * Formula: ceil(distanceMeters / (5000/60))
     */
    private fun calculateWalkingTime(distanceMeters: Double): Int {
        return ceil(distanceMeters / WALKING_SPEED_METERS_PER_MINUTE).toInt()
    }

    /**
     * Parses a polyline JSON string into a list of [LatLng] coordinates.
     * Expected format: [[lat, lng], [lat, lng], ...]
     */
    private fun parsePolylineJson(polylineJson: String): List<LatLng> {
        return try {
            // Simple JSON array parsing for coordinate pairs
            val cleaned = polylineJson.trim()
            if (cleaned.isEmpty() || cleaned == "[]") return emptyList()

            val coordinatePairs = cleaned
                .removePrefix("[[")
                .removeSuffix("]]")
                .split("],[")
                .mapNotNull { pair ->
                    val parts = pair.split(",")
                    if (parts.size == 2) {
                        val lat = parts[0].trim().toDoubleOrNull()
                        val lng = parts[1].trim().toDoubleOrNull()
                        if (lat != null && lng != null) LatLng(lat, lng) else null
                    } else {
                        null
                    }
                }
            coordinatePairs
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Maps an [ExitNodeEntity] to the [ExitNode] domain model.
     */
    private fun ExitNodeEntity.toDomainModel(): ExitNode {
        return ExitNode(
            id = id,
            name = name,
            nameBengali = nameBengali,
            category = try {
                ExitNodeCategory.valueOf(category.uppercase())
            } catch (e: IllegalArgumentException) {
                ExitNodeCategory.METRO // fallback
            },
            location = LatLng(latitude, longitude),
            contactNumber = contactNumber,
            is24Hr = is24Hr,
            isWellLit = isWellLit
        )
    }
}
