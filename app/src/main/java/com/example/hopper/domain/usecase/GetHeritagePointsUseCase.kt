package com.example.hopper.domain.usecase

import com.example.hopper.data.local.db.entity.HeritagePointEntity
import com.example.hopper.domain.FestivalToggleController
import com.example.hopper.domain.model.Festival
import com.example.hopper.domain.repository.HeritageRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetHeritagePointsUseCase @Inject constructor(
    private val heritageRepository: HeritageRepository,
    private val festivalToggleController: FestivalToggleController
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<HeritagePointEntity>> {
        return festivalToggleController.activeFestivalContext.flatMapLatest { context ->
            if (context.festival == Festival.JAGADDHATRI_PUJA) {
                heritageRepository.getByFestival(Festival.JAGADDHATRI_PUJA.name)
            } else {
                flowOf(emptyList())
            }
        }
    }
}
