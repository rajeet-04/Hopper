package com.example.hopper.domain.usecase

import com.example.hopper.data.local.db.entity.OralHistoryEntity
import com.example.hopper.domain.repository.OralHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetOralHistoriesUseCase @Inject constructor(
    private val oralHistoryRepository: OralHistoryRepository
) {
    operator fun invoke(): Flow<List<OralHistoryEntity>> =
        oralHistoryRepository.getAll()

    fun getByPandal(pandalId: String): Flow<List<OralHistoryEntity>> =
        oralHistoryRepository.getByPandalId(pandalId)
}
