package com.example.hopper.domain.usecase

import android.content.Context
import com.example.hopper.domain.model.CrowdBucket
import com.example.hopper.domain.repository.CrowdReportRepository
import com.example.hopper.util.DeviceHashUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubmitCrowdReportUseCase @Inject constructor(
    private val crowdReportRepository: CrowdReportRepository,
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(pandalId: String, bucket: CrowdBucket): Result<Unit> {
        val deviceHash = DeviceHashUtil.getDeviceHash(context)
        return crowdReportRepository.submitReport(pandalId, bucket, deviceHash)
    }
}
