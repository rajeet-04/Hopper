package com.example.hopper.domain.usecase

import com.example.hopper.domain.LocationProvider
import com.example.hopper.domain.model.CrowdBucket
import com.example.hopper.domain.model.LatLng
import com.example.hopper.domain.model.Pandal
import com.example.hopper.domain.repository.CrowdReportRepository
import com.example.hopper.domain.repository.PandalRepository
import com.example.hopper.util.HaversineCalculator
import com.example.hopper.util.LocationUtils
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class ItineraryStop(
    val pandal: Pandal,
    val sequenceOrder: Int,
    val distanceFromPreviousMeters: Double,
    val estimatedArrivalMinutes: Int
)

data class Itinerary(
    val id: String,
    val stops: List<ItineraryStop>,
    val totalDistanceKm: Double,
    val totalTimeMinutes: Int
)

@Singleton
class BuildItineraryUseCase @Inject constructor(
    private val pandalRepository: PandalRepository,
    private val crowdReportRepository: CrowdReportRepository,
    private val locationProvider: LocationProvider
) {
    suspend operator fun invoke(pandalIds: List<String>): Itinerary {
        val pandals = pandalIds.mapNotNull { pandalRepository.getPandalById(it) }
        if (pandals.isEmpty()) return Itinerary(UUID.randomUUID().toString(), emptyList(), 0.0, 0)

        val startLocation = locationProvider.currentLocation.value
            ?: pandals.first().location

        val ordered = buildNearestNeighborRoute(pandals, startLocation)
        return ordered
    }

    private suspend fun buildNearestNeighborRoute(
        pandals: List<Pandal>,
        startLocation: LatLng
    ): Itinerary {
        val remaining = pandals.toMutableList()
        val stops = mutableListOf<ItineraryStop>()
        var currentLocation = startLocation
        var totalDistance = 0.0
        var totalTime = 0
        var sequence = 1

        while (remaining.isNotEmpty()) {
            val next = remaining.minByOrNull { pandal ->
                val distance = HaversineCalculator.distanceMeters(currentLocation, pandal.location)
                val multiplier = getCrowdMultiplier(pandal.id)
                distance * multiplier
            } ?: break

            remaining.remove(next)
            val distance = HaversineCalculator.distanceMeters(currentLocation, next.location)
            val walkingMinutes = LocationUtils.estimateWalkingMinutes(distance)
            totalDistance += distance
            totalTime += walkingMinutes

            stops.add(
                ItineraryStop(
                    pandal = next,
                    sequenceOrder = sequence,
                    distanceFromPreviousMeters = distance,
                    estimatedArrivalMinutes = totalTime
                )
            )

            currentLocation = next.location
            sequence++
        }

        return Itinerary(
            id = UUID.randomUUID().toString(),
            stops = stops,
            totalDistanceKm = totalDistance / 1000.0,
            totalTimeMinutes = totalTime
        )
    }

    private suspend fun getCrowdMultiplier(pandalId: String): Double {
        val bucket = crowdReportRepository.getAggregatedCrowd(pandalId).first()
        return when (bucket) {
            CrowdBucket.GREEN, null -> 1.0
            CrowdBucket.YELLOW -> 1.5
            CrowdBucket.RED -> 2.0
        }
    }
}
