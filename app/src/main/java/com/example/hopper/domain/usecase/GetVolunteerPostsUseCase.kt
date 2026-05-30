package com.example.hopper.domain.usecase

import com.example.hopper.data.local.db.entity.VolunteerPostEntity
import com.example.hopper.domain.FestivalToggleController
import com.example.hopper.domain.repository.VolunteerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetVolunteerPostsUseCase @Inject constructor(
    private val volunteerRepository: VolunteerRepository,
    private val festivalToggleController: FestivalToggleController
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<VolunteerPostEntity>> {
        return festivalToggleController.activeFestivalContext.flatMapLatest { context ->
            volunteerRepository.getActivePosts(context.festival.name, context.year)
        }
    }
}
