package com.example.hopper.data.repository

import com.example.hopper.data.local.db.dao.CalendarDao
import com.example.hopper.data.local.db.entity.TithiEntity
import com.example.hopper.domain.FestivalToggleController
import com.example.hopper.domain.model.Festival
import com.example.hopper.domain.model.FestivalContext
import com.example.hopper.domain.model.Tithi
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class CalendarRepositoryImplTest {

    private lateinit var calendarDao: CalendarDao
    private lateinit var festivalToggleController: FestivalToggleController
    private lateinit var repository: CalendarRepositoryImpl

    private val activeFestivalContext = MutableStateFlow(
        FestivalContext(festival = Festival.DURGA_PUJA, year = 2026)
    )

    private val sampleEntities = listOf(
        TithiEntity(
            id = "tithi_1",
            festival = "DURGA_PUJA",
            year = 2026,
            name = "Shashti",
            nameBengali = "ষষ্ঠী",
            date = LocalDate.of(2026, 10, 15)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli(),
            culturalSignificance = "Welcoming of the Goddess",
            culturalSignificanceBengali = "দেবীর আগমন",
            isPeakCrowd = false
        ),
        TithiEntity(
            id = "tithi_2",
            festival = "DURGA_PUJA",
            year = 2026,
            name = "Ashtami",
            nameBengali = "অষ্টমী",
            date = LocalDate.of(2026, 10, 17)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli(),
            culturalSignificance = "Peak worship day",
            culturalSignificanceBengali = "মহাপূজার দিন",
            isPeakCrowd = true
        ),
        TithiEntity(
            id = "tithi_3",
            festival = "DURGA_PUJA",
            year = 2026,
            name = "Navami",
            nameBengali = "নবমী",
            date = LocalDate.of(2026, 10, 18)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli(),
            culturalSignificance = "Final worship day",
            culturalSignificanceBengali = "শেষ পূজার দিন",
            isPeakCrowd = true
        )
    )

    @Before
    fun setup() {
        calendarDao = mockk(relaxed = true)
        festivalToggleController = mockk(relaxed = true)
        every { festivalToggleController.activeFestivalContext } returns activeFestivalContext

        repository = CalendarRepositoryImpl(calendarDao, festivalToggleController)
    }

    @Test
    fun `getTithisForActiveFestival returns mapped tithis for active context`() = runTest {
        every {
            calendarDao.getTithisByFestivalAndYear("DURGA_PUJA", 2026)
        } returns flowOf(sampleEntities)

        val result = repository.getTithisForActiveFestival().first()

        assertThat(result).hasSize(3)
        assertThat(result[0].id).isEqualTo("tithi_1")
        assertThat(result[0].festival).isEqualTo(Festival.DURGA_PUJA)
        assertThat(result[0].name).isEqualTo("Shashti")
        assertThat(result[0].nameBengali).isEqualTo("ষষ্ঠী")
        assertThat(result[0].date).isEqualTo(LocalDate.of(2026, 10, 15))
        assertThat(result[0].significance).isEqualTo("Welcoming of the Goddess")
        assertThat(result[0].isPeakCrowd).isFalse()
    }

    @Test
    fun `getTithisByFestivalAndYear returns mapped tithis for specified festival`() = runTest {
        every {
            calendarDao.getTithisByFestivalAndYear("JAGADDHATRI_PUJA", 2026)
        } returns flowOf(emptyList())

        val result = repository.getTithisByFestivalAndYear(Festival.JAGADDHATRI_PUJA, 2026).first()

        assertThat(result).isEmpty()
    }

    @Test
    fun `getCurrentTithi returns mapped tithi when date matches`() = runTest {
        val todayEpochMs = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val todayEntity = TithiEntity(
            id = "tithi_today",
            festival = "DURGA_PUJA",
            year = 2026,
            name = "Saptami",
            nameBengali = "সপ্তমী",
            date = todayEpochMs,
            culturalSignificance = "First major day",
            culturalSignificanceBengali = "প্রথম প্রধান দিন",
            isPeakCrowd = false
        )

        coEvery { calendarDao.getTithiByDate(todayEpochMs) } returns todayEntity

        val result = repository.getCurrentTithi()

        assertThat(result).isNotNull()
        assertThat(result!!.id).isEqualTo("tithi_today")
        assertThat(result.name).isEqualTo("Saptami")
        assertThat(result.date).isEqualTo(LocalDate.now())
    }

    @Test
    fun `getCurrentTithi returns null when no tithi matches today`() = runTest {
        val todayEpochMs = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        coEvery { calendarDao.getTithiByDate(todayEpochMs) } returns null

        val result = repository.getCurrentTithi()

        assertThat(result).isNull()
    }

    @Test
    fun `getPeakCrowdDays returns only tithis with isPeakCrowd true`() = runTest {
        every {
            calendarDao.getTithisByFestivalAndYear("DURGA_PUJA", 2026)
        } returns flowOf(sampleEntities)

        val result = repository.getPeakCrowdDays().first()

        assertThat(result).hasSize(2)
        assertThat(result.all { it.isPeakCrowd }).isTrue()
        assertThat(result[0].name).isEqualTo("Ashtami")
        assertThat(result[1].name).isEqualTo("Navami")
    }

    @Test
    fun `getTithisForActiveFestival reacts to context changes`() = runTest {
        val jagaddhatriEntities = listOf(
            TithiEntity(
                id = "tithi_j1",
                festival = "JAGADDHATRI_PUJA",
                year = 2026,
                name = "Shashti",
                nameBengali = "ষষ্ঠী",
                date = LocalDate.of(2026, 11, 10)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant().toEpochMilli(),
                culturalSignificance = "Start of Jagaddhatri Puja",
                culturalSignificanceBengali = "জগদ্ধাত্রী পূজার শুরু",
                isPeakCrowd = false
            )
        )

        every {
            calendarDao.getTithisByFestivalAndYear("DURGA_PUJA", 2026)
        } returns flowOf(sampleEntities)
        every {
            calendarDao.getTithisByFestivalAndYear("JAGADDHATRI_PUJA", 2026)
        } returns flowOf(jagaddhatriEntities)

        // Initially Durga Puja
        val durgaResult = repository.getTithisForActiveFestival().first()
        assertThat(durgaResult).hasSize(3)

        // Switch to Jagaddhatri Puja
        activeFestivalContext.value = FestivalContext(Festival.JAGADDHATRI_PUJA, 2026)
        val jagaddhatriResult = repository.getTithisForActiveFestival().first()
        assertThat(jagaddhatriResult).hasSize(1)
        assertThat(jagaddhatriResult[0].festival).isEqualTo(Festival.JAGADDHATRI_PUJA)
    }

    @Test
    fun `toDomain maps null nameBengali to empty string`() = runTest {
        val entityWithNullBengali = TithiEntity(
            id = "tithi_null",
            festival = "DURGA_PUJA",
            year = 2026,
            name = "Dashami",
            nameBengali = null,
            date = LocalDate.of(2026, 10, 19)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli(),
            culturalSignificance = null,
            culturalSignificanceBengali = null,
            isPeakCrowd = false
        )

        every {
            calendarDao.getTithisByFestivalAndYear("DURGA_PUJA", 2026)
        } returns flowOf(listOf(entityWithNullBengali))

        val result = repository.getTithisByFestivalAndYear(Festival.DURGA_PUJA, 2026).first()

        assertThat(result).hasSize(1)
        assertThat(result[0].nameBengali).isEqualTo("")
        assertThat(result[0].significance).isNull()
        assertThat(result[0].significanceBengali).isNull()
    }
}
