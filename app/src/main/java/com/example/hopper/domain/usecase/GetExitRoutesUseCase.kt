package com.example.hopper.domain.usecase

import com.example.hopper.domain.LocationProvider
import com.example.hopper.domain.model.ExitRoute
import com.example.hopper.domain.repository.ExitRouterRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetExitRoutesUseCase @Inject constructor(
    private val exitRouterRepository: ExitRouterRepository,
    private val locationProvider: LocationProvider
) {
    suspend fun getNearestExits(): List<ExitRoute> {
        val location = locationProvider.currentLocation.value ?: return emptyList()
        return exitRouterRepository.getNearestExitPerCategory(location)
    }

    suspend fun getRoutesFromPandal(pandalId: String, nightSafetyMode: Boolean = false): List<ExitRoute> {
        return exitRouterRepository.getRoutesFromPandal(pandalId, nightSafetyMode)
    }

    suspend fun getAlternateRoutes(pandalId: String, exitNodeId: String): List<ExitRoute> {
        return exitRouterRepository.getAlternateRoutes(pandalId, exitNodeId)
    }
}
