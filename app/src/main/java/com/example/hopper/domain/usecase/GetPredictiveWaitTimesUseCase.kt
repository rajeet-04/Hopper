package com.example.hopper.domain.usecase

import com.example.hopper.data.local.db.dao.HistoricalCrowdPatternDao
import com.example.hopper.domain.model.CrowdBucket
import com.example.hopper.domain.repository.CrowdReportRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

data class HourlyPrediction(
    val hour: Int,
    val predictedBucket: CrowdBucket,
    val isLive: Boolean
)

@Singleton
class GetPredictiveWaitTimesUseCase @Inject constructor(
    private val historicalCrowdPatternDao: HistoricalCrowdPatternDao,
    private val crowdReportRepository: CrowdReportRepository
) {
    suspend fun getHeatTimeline(pandalId: String, dayOfFestival: Int): List<HourlyPrediction> {
        val patterns = historicalCrowdPatternDao.getByPandalAndDay(pandalId, dayOfFestival)
        val liveBucket = crowdReportRepository.getAggregatedCrowd(pandalId).first()
        val currentHour = java.time.LocalTime.now().hour

        return patterns.map { pattern ->
            val isCurrentHour = pattern.hourOfDay == currentHour
            val bucket = if (isCurrentHour && liveBucket != null) {
                liveBucket
            } else {
                CrowdBucket.valueOf(pattern.predictedBucket)
            }
            HourlyPrediction(
                hour = pattern.hourOfDay,
                predictedBucket = bucket,
                isLive = isCurrentHour && liveBucket != null
            )
        }.sortedBy { it.hour }
    }

    suspend fun getPeakSummary(pandalId: String, dayOfFestival: Int): String {
        val timeline = getHeatTimeline(pandalId, dayOfFestival)
        val peakHours = timeline.filter { it.predictedBucket == CrowdBucket.RED }
        return if (peakHours.isEmpty()) {
            "No peak crowd expected"
        } else {
            val start = peakHours.first().hour
            val end = peakHours.last().hour
            "Peak crowd expected ${formatHour(start)}-${formatHour(end)}"
        }
    }

    private fun formatHour(hour: Int): String {
        val displayHour = if (hour > 24) hour - 24 else hour
        return when {
            displayHour == 0 || displayHour == 24 -> "12 AM"
            displayHour < 12 -> "$displayHour AM"
            displayHour == 12 -> "12 PM"
            else -> "${displayHour - 12} PM"
        }
    }
}
