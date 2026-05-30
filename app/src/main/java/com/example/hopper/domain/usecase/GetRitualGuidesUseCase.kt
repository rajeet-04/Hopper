package com.example.hopper.domain.usecase

import com.example.hopper.data.local.db.entity.RitualGuideEntity
import com.example.hopper.domain.FestivalToggleController
import com.example.hopper.domain.repository.RitualGuideRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRitualGuidesUseCase @Inject constructor(
    private val ritualGuideRepository: RitualGuideRepository,
    private val festivalToggleController: FestivalToggleController
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<RitualGuideEntity>> {
        return festivalToggleController.activeFestivalContext.flatMapLatest { context ->
            ritualGuideRepository.getByFestivalAndYear(context.festival.name, context.year)
        }
    }
}
