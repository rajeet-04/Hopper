package com.example.hopper.data.local

import com.example.hopper.domain.repository.BhogRepository
import com.example.hopper.domain.repository.CrowdReportRepository
import com.example.hopper.domain.repository.LostPersonRepository
import com.example.hopper.domain.repository.VolunteerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Runs expiry cleanup for all time-sensitive data stores. Invoked on app launch
 * and can be re-run periodically.
 *
 * - Crowd reports: 20-minute expiry
 * - Bhog pins: min(endTime, reportedAt + 2h)
 * - Lost person posts: 2-hour expiry
 * - Volunteer posts: past timeSlotEnd
 */
@Singleton
class CleanupScheduler @Inject constructor(
    private val crowdReportRepository: CrowdReportRepository,
    private val bhogRepository: BhogRepository,
    private val lostPersonRepository: LostPersonRepository,
    private val volunteerRepository: VolunteerRepository
) {
    fun runCleanup(scope: CoroutineScope) {
        scope.launch {
            runCatching { crowdReportRepository.cleanupExpiredReports() }
            runCatching { bhogRepository.deleteExpired() }
            runCatching { lostPersonRepository.deleteExpired() }
            runCatching { volunteerRepository.deleteExpired() }
        }
    }
}
