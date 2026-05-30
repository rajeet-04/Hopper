package com.example.hopper.data.repository

import com.example.hopper.data.local.db.dao.CrowdReportDao
import com.example.hopper.data.local.db.dao.PandalDao
import com.example.hopper.data.local.db.entity.PandalEntity
import com.example.hopper.domain.FestivalToggleController
import com.example.hopper.domain.model.ArtisanCredits
import com.example.hopper.domain.model.ConfidenceLevel
import com.example.hopper.domain.model.CrowdBucket
import com.example.hopper.domain.model.Festival
import com.example.hopper.domain.model.LatLng
import com.example.hopper.domain.model.Pandal
import com.example.hopper.domain.model.SourceType
import com.example.hopper.domain.repository.PandalRepository
import com.example.hopper.util.HaversineCalculator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [PandalRepository] that queries Room via [PandalDao],
 * filters by the active festival/year context from [FestivalToggleController],
 * and computes composite scoring for nearest-pandal ranking.
 *
 * Composite score formula:
 *   score = 0.5 * normalizedDistance + 0.3 * crowdPenalty + 0.2 * (1 - normalizedSignificance)
 *
 * Lower score = better (closer, less crowded, more significant).
 */
@Singleton
class PandalRepositoryImpl @Inject constructor(
    private val pandalDao: PandalDao,
    private val crowdReportDao: CrowdReportDao,
    private val festivalToggleController: FestivalToggleController
) : PandalRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getNearestPandals(userLocation: LatLng, limit: Int): Flow<List<Pandal>> {
        return festivalToggleController.activeFestivalContext.flatMapLatest { context ->
            pandalDao.getByFestivalAndYear(context.festival.name, context.year)
                .map { entities ->
                    rankByCompositeScore(entities, userLocation, limit)
                }
        }
    }

    override suspend fun getPandalById(id: String): Pandal? {
        return pandalDao.getById(id)?.toDomain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun searchPandals(query: String): Flow<List<Pandal>> {
        return festivalToggleController.activeFestivalContext.flatMapLatest { context ->
            pandalDao.search(query).map { entities ->
                entities
                    .filter { it.festival == context.festival.name && it.year == context.year }
                    .map { it.toDomain() }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllPandals(): Flow<List<Pandal>> {
        return festivalToggleController.activeFestivalContext.flatMapLatest { context ->
            pandalDao.getByFestivalAndYear(context.festival.name, context.year)
                .map { entities -> entities.map { it.toDomain() } }
        }
    }

    /**
     * Ranks pandals by composite score combining distance, crowd level, and significance.
     *
     * Score = 0.5 * normalizedDistance + 0.3 * crowdPenalty + 0.2 * (1 - normalizedSignificance)
     *
     * - normalizedDistance: distance / maxDistance in the set (0..1)
     * - crowdPenalty: GREEN=0.0, YELLOW=0.5, RED=1.0
     * - normalizedSignificance: significanceRank / maxRank (lower rank = more significant,
     *   so we use 1 - normalized to reward higher significance)
     */
    private suspend fun rankByCompositeScore(
        entities: List<PandalEntity>,
        userLocation: LatLng,
        limit: Int
    ): List<Pandal> {
        if (entities.isEmpty()) return emptyList()

        val currentTimeMs = System.currentTimeMillis()

        // Calculate distances for all pandals
        val pandalsWithDistance = entities.map { entity ->
            val pandalLocation = LatLng(entity.latitude, entity.longitude)
            val distance = HaversineCalculator.distanceMeters(userLocation, pandalLocation)
            entity to distance
        }

        val maxDistance = pandalsWithDistance.maxOf { it.second }.coerceAtLeast(1.0)
        val maxRank = entities.maxOf { it.significanceRank }.coerceAtLeast(1)

        // Calculate composite score for each pandal
        val scoredPandals = pandalsWithDistance.map { (entity, distance) ->
            val crowdBucket = getCurrentCrowdBucket(entity.id, currentTimeMs)
            val normalizedDistance = distance / maxDistance
            val crowdPenalty = crowdBucketToPenalty(crowdBucket)
            val normalizedSignificance = entity.significanceRank.toDouble() / maxRank

            val compositeScore = 0.5 * normalizedDistance +
                    0.3 * crowdPenalty +
                    0.2 * (1.0 - normalizedSignificance)

            entity.toDomain() to compositeScore
        }

        return scoredPandals
            .sortedBy { it.second }
            .take(limit)
            .map { it.first }
    }

    /**
     * Gets the current crowd bucket for a pandal based on active (non-expired) reports.
     * Uses weighted median of active reports. Defaults to GREEN if no reports exist.
     */
    private suspend fun getCurrentCrowdBucket(pandalId: String, currentTimeMs: Long): CrowdBucket {
        val activeReports = crowdReportDao
            .getActiveReportsForPandal(pandalId, currentTimeMs)
            .first()

        if (activeReports.isEmpty()) return CrowdBucket.GREEN

        // Use the most common bucket (mode) among active reports
        val bucketCounts = activeReports.groupingBy { it.bucket }.eachCount()
        val dominantBucketName = bucketCounts.maxByOrNull { it.value }?.key
            ?: return CrowdBucket.GREEN

        return try {
            CrowdBucket.valueOf(dominantBucketName)
        } catch (e: IllegalArgumentException) {
            CrowdBucket.GREEN
        }
    }

    /**
     * Maps a [CrowdBucket] to its penalty value for composite scoring.
     * GREEN = 0.0 (no penalty), YELLOW = 0.5, RED = 1.0 (maximum penalty).
     */
    private fun crowdBucketToPenalty(bucket: CrowdBucket): Double {
        return when (bucket) {
            CrowdBucket.GREEN -> 0.0
            CrowdBucket.YELLOW -> 0.5
            CrowdBucket.RED -> 1.0
        }
    }

    /**
     * Maps a [PandalEntity] to the [Pandal] domain model.
     */
    private fun PandalEntity.toDomain(): Pandal {
        return Pandal(
            id = id,
            name = name,
            nameBengali = nameBengali,
            location = LatLng(latitude, longitude),
            city = city,
            neighborhood = neighborhood,
            festival = Festival.valueOf(festival),
            year = year,
            theme = theme,
            committeeName = committeeName,
            establishedYear = establishedYear,
            artisanCredits = parseArtisanCredits(artisanCreditsJson),
            awards = parseStringList(awards),
            photos = parseStringList(photos),
            significanceRank = significanceRank,
            sourceType = SourceType.valueOf(sourceType),
            confidenceLevel = ConfidenceLevel.valueOf(confidenceLevel)
        )
    }

    /**
     * Parses a JSON string into [ArtisanCredits], or returns null if the input is null/invalid.
     */
    private fun parseArtisanCredits(json: String?): ArtisanCredits? {
        if (json.isNullOrBlank()) return null
        return try {
            val obj = JSONObject(json)
            ArtisanCredits(
                idolMaker = obj.optString("idol_maker").takeIf { it.isNotEmpty() },
                lightingDesigner = obj.optString("lighting_designer").takeIf { it.isNotEmpty() },
                themeDesigner = obj.optString("theme_designer").takeIf { it.isNotEmpty() }
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Parses a JSON array string into a list of strings, or returns an empty list.
     */
    private fun parseStringList(json: String?): List<String> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            val jsonArray = org.json.JSONArray(json)
            (0 until jsonArray.length()).map { jsonArray.getString(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
