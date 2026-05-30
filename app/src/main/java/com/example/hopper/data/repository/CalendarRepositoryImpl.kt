package com.example.hopper.data.repository

import com.example.hopper.data.local.db.dao.CalendarDao
import com.example.hopper.data.local.db.entity.TithiEntity
import com.example.hopper.domain.FestivalToggleController
import com.example.hopper.domain.model.Festival
import com.example.hopper.domain.model.Tithi
import com.example.hopper.domain.repository.CalendarRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [CalendarRepository] backed by Room database.
 * Observes the active festival context to reactively filter tithi data.
 */
@Singleton
class CalendarRepositoryImpl @Inject constructor(
    private val calendarDao: CalendarDao,
    private val festivalToggleController: FestivalToggleController
) : CalendarRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getTithisForActiveFestival(): Flow<List<Tithi>> {
        return festivalToggleController.activeFestivalContext.flatMapLatest { context ->
            calendarDao.getTithisByFestivalAndYear(context.festival.name, context.year)
                .map { entities -> entities.map { it.toDomain() } }
        }
    }

    override fun getTithisByFestivalAndYear(festival: Festival, year: Int): Flow<List<Tithi>> {
        return calendarDao.getTithisByFestivalAndYear(festival.name, year)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getCurrentTithi(): Tithi? {
        val todayEpochMs = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        return calendarDao.getTithiByDate(todayEpochMs)?.toDomain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPeakCrowdDays(): Flow<List<Tithi>> {
        return festivalToggleController.activeFestivalContext.flatMapLatest { context ->
            calendarDao.getTithisByFestivalAndYear(context.festival.name, context.year)
                .map { entities ->
                    entities.filter { it.isPeakCrowd }.map { it.toDomain() }
                }
        }
    }

    private fun TithiEntity.toDomain(): Tithi {
        return Tithi(
            id = id,
            festival = Festival.valueOf(festival),
            year = year,
            name = name,
            nameBengali = nameBengali ?: "",
            date = Instant.ofEpochMilli(date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate(),
            significance = culturalSignificance,
            significanceBengali = culturalSignificanceBengali,
            isPeakCrowd = isPeakCrowd
        )
    }
}
