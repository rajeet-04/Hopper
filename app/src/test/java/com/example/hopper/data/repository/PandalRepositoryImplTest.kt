package com.example.hopper.data.repository

import com.example.hopper.data.local.db.dao.CrowdReportDao
import com.example.hopper.data.local.db.dao.PandalDao
import com.example.hopper.data.local.db.entity.CrowdReportEntity
import com.example.hopper.data.local.db.entity.PandalEntity
import com.example.hopper.domain.FestivalToggleController
import com.example.hopper.domain.model.Festival
import com.example.hopper.domain.model.FestivalContext
import com.example.hopper.domain.model.LatLng
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

class PandalRepositoryImplTest {

    private lateinit var pandalDao: PandalDao
    private lateinit var crowdReportDao: CrowdReportDao
    private lateinit var festivalToggleController: FestivalToggleController
    private lateinit var repository: PandalRepositoryImpl

    private val activeFestivalContext = MutableStateFlow(
        FestivalContext(festival = Festival.DURGA_PUJA, year = 2026)
    )

    // Kolkata center as user location
    private val userLocation = LatLng(22.5726, 88.3639)

    private fun createPandalEntity(
        id: String,
        name: String,
        latitude: Double,
        longitude: Double,
        significanceRank: Int = 1,
        festival: String = "DURGA_PUJA",
        year: Int = 2026
    ) = PandalEntity(
        id = id,
        name = name,
        nameBengali = null,
        latitude = latitude,
        longitude = longitude,
        city = "Kolkata",
        neighborhood = null,
        festival = festival,
        year = year,
        theme = "Test Theme",
        committeeName = "Test Committee",
        establishedYear = 1990,
        artisanCreditsJson = null,
        awards = null,
        photos = null,
        significanceRank = significanceRank,
        sourceType = "COMMITTEE",
        confidenceLevel = "HIGH"
    )

    @Before
    fun setup() {
        pandalDao = mockk(relaxed = true)
        crowdReportDao = mockk(relaxed = true)
        festivalToggleController = mockk(relaxed = true)
        every { festivalToggleController.activeFestivalContext } returns activeFestivalContext

        repository = PandalRepositoryImpl(pandalDao, crowdReportDao, festivalToggleController)
    }

    @Test
    fun `getNearestPandals returns pandals sorted by composite score`() = runTest {
        // Pandal A: close, rank 1 (most significant)
        val pandalA = createPandalEntity("a", "Close Pandal", 22.5730, 88.3640, significanceRank = 1)
        // Pandal B: far, rank 3 (least significant)
        val pandalB = createPandalEntity("b", "Far Pandal", 22.6000, 88.4000, significanceRank = 3)
        // Pandal C: medium distance, rank 2
        val pandalC = createPandalEntity("c", "Medium Pandal", 22.5850, 88.3750, significanceRank = 2)

        every { pandalDao.getByFestivalAndYear("DURGA_PUJA", 2026) } returns flowOf(listOf(pandalA, pandalB, pandalC))
        // No crowd reports — all default to GREEN
        every { crowdReportDao.getActiveReportsForPandal(any(), any()) } returns flowOf(emptyList())

        val result = repository.getNearestPandals(userLocation, limit = 10).first()

        assertThat(result).hasSize(3)
        // Close + high significance should rank first
        assertThat(result[0].id).isEqualTo("a")
    }

    @Test
    fun `getNearestPandals respects limit parameter`() = runTest {
        val pandals = (1..5).map { i ->
            createPandalEntity("p$i", "Pandal $i", 22.5726 + i * 0.001, 88.3639, significanceRank = i)
        }

        every { pandalDao.getByFestivalAndYear("DURGA_PUJA", 2026) } returns flowOf(pandals)
        every { crowdReportDao.getActiveReportsForPandal(any(), any()) } returns flowOf(emptyList())

        val result = repository.getNearestPandals(userLocation, limit = 3).first()

        assertThat(result).hasSize(3)
    }

    @Test
    fun `getNearestPandals applies crowd penalty to RED pandals`() = runTest {
        // Two pandals at same distance and same significance
        val pandalGreen = createPandalEntity("green", "Green Pandal", 22.5800, 88.3700, significanceRank = 1)
        val pandalRed = createPandalEntity("red", "Red Pandal", 22.5800, 88.3700, significanceRank = 1)

        every { pandalDao.getByFestivalAndYear("DURGA_PUJA", 2026) } returns flowOf(listOf(pandalGreen, pandalRed))

        // Green pandal has no crowd reports (defaults to GREEN)
        every { crowdReportDao.getActiveReportsForPandal("green", any()) } returns flowOf(emptyList())
        // Red pandal has RED crowd reports
        every { crowdReportDao.getActiveReportsForPandal("red", any()) } returns flowOf(
            listOf(
                CrowdReportEntity(
                    id = "cr1",
                    pandalId = "red",
                    bucket = "RED",
                    deviceHash = "hash1",
                    reportedAtEpochMs = System.currentTimeMillis(),
                    expiresAtEpochMs = System.currentTimeMillis() + 1_200_000,
                    isSynced = true
                )
            )
        )

        val result = repository.getNearestPandals(userLocation, limit = 10).first()

        assertThat(result).hasSize(2)
        // Green pandal should rank higher (lower score) due to no crowd penalty
        assertThat(result[0].id).isEqualTo("green")
        assertThat(result[1].id).isEqualTo("red")
    }

    @Test
    fun `getNearestPandals returns empty list when no pandals exist`() = runTest {
        every { pandalDao.getByFestivalAndYear("DURGA_PUJA", 2026) } returns flowOf(emptyList())

        val result = repository.getNearestPandals(userLocation, limit = 10).first()

        assertThat(result).isEmpty()
    }

    @Test
    fun `getPandalById returns mapped pandal when found`() = runTest {
        val entity = createPandalEntity("p1", "Test Pandal", 22.5726, 88.3639, significanceRank = 5)
        coEvery { pandalDao.getById("p1") } returns entity

        val result = repository.getPandalById("p1")

        assertThat(result).isNotNull()
        assertThat(result!!.id).isEqualTo("p1")
        assertThat(result.name).isEqualTo("Test Pandal")
        assertThat(result.location).isEqualTo(LatLng(22.5726, 88.3639))
        assertThat(result.festival).isEqualTo(Festival.DURGA_PUJA)
        assertThat(result.significanceRank).isEqualTo(5)
    }

    @Test
    fun `getPandalById returns null when not found`() = runTest {
        coEvery { pandalDao.getById("nonexistent") } returns null

        val result = repository.getPandalById("nonexistent")

        assertThat(result).isNull()
    }

    @Test
    fun `searchPandals filters by active festival context`() = runTest {
        val durgaPandal = createPandalEntity("d1", "Durga Pandal", 22.57, 88.36, festival = "DURGA_PUJA")
        val jagaddhatriPandal = createPandalEntity("j1", "Jagaddhatri Pandal", 22.87, 88.37, festival = "JAGADDHATRI_PUJA")

        every { pandalDao.search("Pandal") } returns flowOf(listOf(durgaPandal, jagaddhatriPandal))

        val result = repository.searchPandals("Pandal").first()

        // Only Durga Puja pandals should be returned (active context)
        assertThat(result).hasSize(1)
        assertThat(result[0].id).isEqualTo("d1")
    }

    @Test
    fun `getAllPandals returns all pandals for active context`() = runTest {
        val pandals = listOf(
            createPandalEntity("p1", "Pandal 1", 22.57, 88.36),
            createPandalEntity("p2", "Pandal 2", 22.58, 88.37)
        )

        every { pandalDao.getByFestivalAndYear("DURGA_PUJA", 2026) } returns flowOf(pandals)

        val result = repository.getAllPandals().first()

        assertThat(result).hasSize(2)
        assertThat(result[0].id).isEqualTo("p1")
        assertThat(result[1].id).isEqualTo("p2")
    }

    @Test
    fun `getAllPandals reacts to festival context changes`() = runTest {
        val durgaPandals = listOf(createPandalEntity("d1", "Durga 1", 22.57, 88.36))
        val jagaddhatriPandals = listOf(createPandalEntity("j1", "Jagaddhatri 1", 22.87, 88.37, festival = "JAGADDHATRI_PUJA"))

        every { pandalDao.getByFestivalAndYear("DURGA_PUJA", 2026) } returns flowOf(durgaPandals)
        every { pandalDao.getByFestivalAndYear("JAGADDHATRI_PUJA", 2026) } returns flowOf(jagaddhatriPandals)

        // Initially Durga Puja
        val durgaResult = repository.getAllPandals().first()
        assertThat(durgaResult).hasSize(1)
        assertThat(durgaResult[0].id).isEqualTo("d1")

        // Switch to Jagaddhatri Puja
        activeFestivalContext.value = FestivalContext(Festival.JAGADDHATRI_PUJA, 2026)
        val jagaddhatriResult = repository.getAllPandals().first()
        assertThat(jagaddhatriResult).hasSize(1)
        assertThat(jagaddhatriResult[0].id).isEqualTo("j1")
    }

    @Test
    fun `toDomain correctly parses artisan credits JSON`() = runTest {
        val entity = createPandalEntity("p1", "Test", 22.57, 88.36).copy(
            artisanCreditsJson = """{"idol_maker":"Sankar Sen","lighting_designer":"Raju Das","theme_designer":"Amit Roy"}"""
        )
        coEvery { pandalDao.getById("p1") } returns entity

        val result = repository.getPandalById("p1")

        assertThat(result).isNotNull()
        assertThat(result!!.artisanCredits).isNotNull()
        assertThat(result.artisanCredits!!.idolMaker).isEqualTo("Sankar Sen")
        assertThat(result.artisanCredits!!.lightingDesigner).isEqualTo("Raju Das")
        assertThat(result.artisanCredits!!.themeDesigner).isEqualTo("Amit Roy")
    }

    @Test
    fun `toDomain correctly parses awards JSON array`() = runTest {
        val entity = createPandalEntity("p1", "Test", 22.57, 88.36).copy(
            awards = """["Best Theme 2025","Best Lighting 2024"]"""
        )
        coEvery { pandalDao.getById("p1") } returns entity

        val result = repository.getPandalById("p1")

        assertThat(result).isNotNull()
        assertThat(result!!.awards).containsExactly("Best Theme 2025", "Best Lighting 2024")
    }

    @Test
    fun `toDomain handles null artisan credits gracefully`() = runTest {
        val entity = createPandalEntity("p1", "Test", 22.57, 88.36).copy(
            artisanCreditsJson = null
        )
        coEvery { pandalDao.getById("p1") } returns entity

        val result = repository.getPandalById("p1")

        assertThat(result).isNotNull()
        assertThat(result!!.artisanCredits).isNull()
    }

    @Test
    fun `crowd bucket mode determines penalty correctly`() = runTest {
        // Pandal with YELLOW crowd reports should rank between GREEN and RED
        val pandalClose = createPandalEntity("close", "Close", 22.5730, 88.3640, significanceRank = 1)
        val pandalYellow = createPandalEntity("yellow", "Yellow", 22.5730, 88.3640, significanceRank = 1)

        every { pandalDao.getByFestivalAndYear("DURGA_PUJA", 2026) } returns flowOf(listOf(pandalClose, pandalYellow))
        every { crowdReportDao.getActiveReportsForPandal("close", any()) } returns flowOf(emptyList())
        every { crowdReportDao.getActiveReportsForPandal("yellow", any()) } returns flowOf(
            listOf(
                CrowdReportEntity("cr1", "yellow", "YELLOW", "h1", System.currentTimeMillis(), System.currentTimeMillis() + 1_200_000, true),
                CrowdReportEntity("cr2", "yellow", "YELLOW", "h2", System.currentTimeMillis(), System.currentTimeMillis() + 1_200_000, true)
            )
        )

        val result = repository.getNearestPandals(userLocation, limit = 10).first()

        assertThat(result).hasSize(2)
        // GREEN pandal should rank first
        assertThat(result[0].id).isEqualTo("close")
        assertThat(result[1].id).isEqualTo("yellow")
    }
}
