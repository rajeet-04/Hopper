package com.example.hopper.domain.usecase

import com.example.hopper.data.local.db.entity.RitualGuideEntity
import com.example.hopper.domain.repository.RitualGuideRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRitualGuideForTithiUseCase @Inject constructor(
    private val ritualGuideRepository: RitualGuideRepository
) {
    suspend operator fun invoke(tithiId: String): RitualGuideEntity? =
        ritualGuideRepository.getByTithiId(tithiId)
}
