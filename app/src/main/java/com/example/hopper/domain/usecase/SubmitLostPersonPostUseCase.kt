package com.example.hopper.domain.usecase

import com.example.hopper.data.local.db.entity.LostPersonPostEntity
import com.example.hopper.domain.model.LatLng
import com.example.hopper.domain.repository.LostPersonRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubmitLostPersonPostUseCase @Inject constructor(
    private val lostPersonRepository: LostPersonRepository
) {
    suspend operator fun invoke(
        displayName: String,
        description: String,
        location: LatLng,
        deviceHash: String
    ): Result<Unit> {
        return try {
            val now = System.currentTimeMillis()
            val post = LostPersonPostEntity(
                id = UUID.randomUUID().toString(),
                displayName = displayName,
                description = description,
                latitude = location.latitude,
                longitude = location.longitude,
                reportedByDeviceHash = deviceHash,
                reportedAtEpochMs = now,
                expiresAtEpochMs = now + 2 * 60 * 60 * 1000, // 2 hours
                isResolved = false,
                isSynced = false
            )
            lostPersonRepository.submitPost(post)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
