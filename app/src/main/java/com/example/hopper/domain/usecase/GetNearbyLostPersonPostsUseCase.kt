package com.example.hopper.domain.usecase

import com.example.hopper.data.local.db.entity.LostPersonPostEntity
import com.example.hopper.domain.LocationProvider
import com.example.hopper.domain.repository.LostPersonRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetNearbyLostPersonPostsUseCase @Inject constructor(
    private val lostPersonRepository: LostPersonRepository,
    private val locationProvider: LocationProvider
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(radiusMeters: Double = 2000.0): Flow<List<LostPersonPostEntity>> {
        return locationProvider.currentLocation.flatMapLatest { location ->
            if (location != null) {
                lostPersonRepository.getPostsWithinRadius(location, radiusMeters)
            } else {
                flowOf(emptyList())
            }
        }
    }
}
