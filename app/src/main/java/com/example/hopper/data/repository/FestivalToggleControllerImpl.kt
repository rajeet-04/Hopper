package com.example.hopper.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.hopper.data.local.db.dao.CalendarDao
import com.example.hopper.domain.FestivalToggleController
import com.example.hopper.domain.model.Festival
import com.example.hopper.domain.model.FestivalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

/**
 * Implementation of [FestivalToggleController] that persists the selected
 * festival and year in DataStore Preferences and calculates the default
 * festival based on proximity to actual tithi dates from the bundled calendar.
 */
@Singleton
class FestivalToggleControllerImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val calendarDao: CalendarDao,
    private val applicationScope: CoroutineScope
) : FestivalToggleController {

    companion object {
        val KEY_FESTIVAL = stringPreferencesKey("selected_festival")
        val KEY_YEAR = intPreferencesKey("selected_year")
    }

    private val _activeFestivalContext = MutableStateFlow(
        FestivalContext(festival = Festival.DURGA_PUJA, year = LocalDate.now().year)
    )

    override val activeFestivalContext: StateFlow<FestivalContext> =
        _activeFestivalContext.asStateFlow()

    init {
        applicationScope.launch {
            val persisted = loadPersistedContext()
            if (persisted != null) {
                _activeFestivalContext.value = persisted
            } else {
                val default = getDefaultFestival()
                _activeFestivalContext.value = default
                persistContext(default)
            }
        }
    }

    override suspend fun setFestival(festival: Festival, year: Int) {
        val context = FestivalContext(festival = festival, year = year)
        _activeFestivalContext.value = context
        persistContext(context)
    }

    override suspend fun getDefaultFestival(): FestivalContext {
        val today = LocalDate.now()
        val currentYear = today.year
        val todayEpochMs = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        // Try to find the nearest festival dates from the calendar database
        val durgaTithis = calendarDao.getTithisByFestivalAndYear(
            Festival.DURGA_PUJA.name, currentYear
        ).first()

        val jagaddhatriTithis = calendarDao.getTithisByFestivalAndYear(
            Festival.JAGADDHATRI_PUJA.name, currentYear
        ).first()

        // Calculate distance to each festival's date range
        val durgaDistance = calculateDistanceToFestival(todayEpochMs, durgaTithis.map { it.date })
        val jagaddhatriDistance = calculateDistanceToFestival(todayEpochMs, jagaddhatriTithis.map { it.date })

        // If both festivals have dates in the calendar for this year
        if (durgaDistance != null && jagaddhatriDistance != null) {
            // Pick the nearest festival
            val festival = if (durgaDistance <= jagaddhatriDistance) {
                Festival.DURGA_PUJA
            } else {
                Festival.JAGADDHATRI_PUJA
            }
            return FestivalContext(festival = festival, year = currentYear)
        }

        // If only one festival has dates, use that one
        if (durgaDistance != null) {
            return FestivalContext(festival = Festival.DURGA_PUJA, year = currentYear)
        }
        if (jagaddhatriDistance != null) {
            return FestivalContext(festival = Festival.JAGADDHATRI_PUJA, year = currentYear)
        }

        // Fallback: use hardcoded approximate months if no calendar data exists
        // Durga Puja is typically in October, Jagaddhatri Puja in November
        return getDefaultByApproximateMonth(today)
    }

    /**
     * Calculates the minimum absolute distance (in milliseconds) from today
     * to the nearest date in the festival's date range.
     * Returns null if the date list is empty.
     */
    private fun calculateDistanceToFestival(todayEpochMs: Long, festivalDates: List<Long>): Long? {
        if (festivalDates.isEmpty()) return null
        return festivalDates.minOf { date -> abs(todayEpochMs - date) }
    }

    /**
     * Fallback logic using approximate festival months when no calendar data is available.
     * Durga Puja: ~October, Jagaddhatri Puja: ~November
     */
    private fun getDefaultByApproximateMonth(today: LocalDate): FestivalContext {
        val currentYear = today.year
        val month = today.monthValue

        // Approximate midpoints: Durga Puja mid-October (day 15), Jagaddhatri mid-November (day 15)
        val durgaMidpoint = LocalDate.of(currentYear, 10, 15)
        val jagaddhatriMidpoint = LocalDate.of(currentYear, 11, 15)

        val durgaDays = abs(today.toEpochDay() - durgaMidpoint.toEpochDay())
        val jagaddhatriDays = abs(today.toEpochDay() - jagaddhatriMidpoint.toEpochDay())

        val festival = if (durgaDays <= jagaddhatriDays) {
            Festival.DURGA_PUJA
        } else {
            Festival.JAGADDHATRI_PUJA
        }

        // If both festivals are in the past for this year (we're past November),
        // default to Durga Puja of next year
        val lastFestivalDate = jagaddhatriMidpoint.plusDays(5)
        val year = if (today.isAfter(lastFestivalDate)) {
            currentYear + 1
        } else {
            currentYear
        }

        return FestivalContext(festival = festival, year = year)
    }

    private suspend fun loadPersistedContext(): FestivalContext? {
        val prefs = dataStore.data.first()
        val festivalName = prefs[KEY_FESTIVAL] ?: return null
        val year = prefs[KEY_YEAR] ?: return null

        val festival = try {
            Festival.valueOf(festivalName)
        } catch (e: IllegalArgumentException) {
            return null
        }

        return FestivalContext(festival = festival, year = year)
    }

    private suspend fun persistContext(context: FestivalContext) {
        dataStore.edit { prefs ->
            prefs[KEY_FESTIVAL] = context.festival.name
            prefs[KEY_YEAR] = context.year
        }
    }
}
