package com.example.hopper.domain.usecase

import com.example.hopper.data.local.db.entity.LightTrailEntity
import com.example.hopper.domain.FestivalToggleController
import com.example.hopper.domain.model.Festival
import com.example.hopper.domain.repository.LightTrailRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLightTrailUseCase @Inject constructor(
    private val lightTrailRepository: LightTrailRepository,
    private val festivalToggleController: FestivalToggleController
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<LightTrailEntity>> {
        return festivalToggleController.activeFestivalContext.flatMapLatest { context ->
            if (context.festival == Festival.JAGADDHATRI_PUJA) {
                lightTrailRepository.getTrailsByYear(context.year)
            } else {
                flowOf(emptyList())
            }
        }
    }
}
