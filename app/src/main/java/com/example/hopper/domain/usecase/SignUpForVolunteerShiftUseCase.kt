package com.example.hopper.domain.usecase

import com.example.hopper.domain.repository.VolunteerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignUpForVolunteerShiftUseCase @Inject constructor(
    private val volunteerRepository: VolunteerRepository
) {
    suspend operator fun invoke(postId: String, displayName: String, encryptedContact: String): Result<Unit> {
        return volunteerRepository.signUp(postId, displayName, encryptedContact)
    }
}
