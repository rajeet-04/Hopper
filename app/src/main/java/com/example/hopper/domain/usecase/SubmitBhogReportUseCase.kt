package com.example.hopper.domain.usecase

import com.example.hopper.data.local.db.entity.BhogPinEntity
import com.example.hopper.domain.repository.BhogRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubmitBhogReportUseCase @Inject constructor(
    private val bhogRepository: BhogRepository
) {
    suspend operator fun invoke(pin: BhogPinEntity): Result<Unit> {
        return try {
            bhogRepository.submitBhogReport(pin)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
