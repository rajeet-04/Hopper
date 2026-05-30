package com.example.hopper.domain.usecase

import com.example.hopper.data.local.db.entity.BhogPinEntity
import com.example.hopper.domain.repository.BhogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetBhogPinsUseCase @Inject constructor(
    private val bhogRepository: BhogRepository
) {
    operator fun invoke(category: String): Flow<List<BhogPinEntity>> =
        bhogRepository.getActivePinsByCategory(category)
}
