package com.example.hopper.domain.usecase

import com.example.hopper.domain.LocationProvider
import com.example.hopper.domain.model.Pandal
import com.example.hopper.domain.repository.PandalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetNearestPandalsUseCase @Inject constructor(
    private val pandalRepository: PandalRepository,
    private val locationProvider: LocationProvider
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(limit: Int = 10): Flow<List<Pandal>> {
        return locationProvider.currentLocation.flatMapLatest { location ->
            if (location != null) {
                pandalRepository.getNearestPandals(location, limit)
            } else {
                flowOf(emptyList())
            }
        }
    }
}
