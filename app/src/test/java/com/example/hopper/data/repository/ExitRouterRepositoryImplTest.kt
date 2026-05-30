package com.example.hopper.data.repository

import com.example.hopper.data.local.db.dao.ConnectorDao
import com.example.hopper.data.local.db.dao.ExitNodeDao
import com.example.hopper.data.local.db.entity.ConnectorEntity
import com.example.hopper.data.local.db.entity.ExitNodeEntity
import com.example.hopper.domain.model.ExitNodeCategory
import com.example.hopper.domain.model.LatLng
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ExitRouterRepositoryImplTest {

    private lateinit var exitNodeDao: ExitNodeDao
    private lateinit var connectorDao: ConnectorDao
    private lateinit var repository: ExitRouterRepositoryImpl

    // Test location: central Kolkata (approx Esplanade)
    private val userLocation = LatLng(22.5626, 88.3510)

    // Exit nodes at known distances from userLocation
    private val metroNode1 = ExitNodeEntity(
        id = "metro-1",
        name = "Park Street Metro",
        nameBengali = "পার্ক স্ট্রিট মেট্রো",
        category = "METRO",
        latitude = 22.5560,
        longitude = 88.3520,
        contactNumber = "033-1234567",
        is24Hr = true,
        isWellLit = true
    )

    private val metroNode2 = ExitNodeEntity(
        id = "metro-2",
        name = "Maidan Metro",
        nameBengali = "ময়দান মেট্রো",
        category = "METRO",
        latitude = 22.5480,
        longitude = 88.3470,
        contactNumber = "033-2345678",
        is24Hr = true,
        isWellLit = true
    )

    private val railwayNode = ExitNodeEntity(
        id = "railway-1",
        name = "Sealdah Station",
        nameBengali = "শিয়ালদহ স্টেশন",
        category = "RAILWAY",
        latitude = 22.5654,
        longitude = 88.3700,
        contactNumber = "033-3456789",
        is24Hr = true,
        isWellLit = true
    )

    private val policeNode = ExitNodeEntity(
        id = "police-1",
        name = "Lalbazar Police",
        nameBengali = "লালবাজার পুলিশ",
        category = "POLICE",
        latitude = 22.5700,
        longitude = 88.3550,
        contactNumber = "100",
        is24Hr = true,
        isWellLit = true
    )

    private val medicalNode = ExitNodeEntity(
        id = "medical-1",
        name = "SSKM Hospital",
        nameBengali = "এসএসকেএম হাসপাতাল",
        category = "MEDICAL",
        latitude = 22.5400,
        longitude = 88.3430,
        contactNumber = "033-4567890",
        is24Hr = true,
        isWellLit = false
    )

    private val allNodes = listOf(metroNode1, metroNode2, railwayNode, policeNode, medicalNode)

    @Before
    fun setup() {
        exitNodeDao = mockk()
        connectorDao = mockk()
        repository = ExitRouterRepositoryImpl(exitNodeDao, connectorDao)
    }

    // --- getNearestExitPerCategory tests ---

    @Test
    fun `getNearestExitPerCategory returns one exit per category`() = runTest {
        coEvery { exitNodeDao.getAll() } returns flowOf(allNodes)

        val results = repository.getNearestExitPerCategory(userLocation)

        // Should have one per category: METRO, RAILWAY, POLICE, MEDICAL
        val categories = results.map { it.exitNode.category }.toSet()
        assertThat(categories).containsExactly(
            ExitNodeCategory.METRO,
            ExitNodeCategory.RAILWAY,
            ExitNodeCategory.POLICE,
            ExitNodeCategory.MEDICAL
        )
        assertThat(results).hasSize(4)
    }

    @Test
    fun `getNearestExitPerCategory picks closest metro node`() = runTest {
        coEvery { exitNodeDao.getAll() } returns flowOf(allNodes)

        val results = repository.getNearestExitPerCategory(userLocation)

        val metroRoute = results.first { it.exitNode.category == ExitNodeCategory.METRO }
        // metroNode1 is closer to userLocation than metroNode2
        assertThat(metroRoute.exitNode.id).isEqualTo("metro-1")
    }

    @Test
    fun `getNearestExitPerCategory calculates positive distance`() = runTest {
        coEvery { exitNodeDao.getAll() } returns flowOf(allNodes)

        val results = repository.getNearestExitPerCategory(userLocation)

        results.forEach { route ->
            assertThat(route.distanceMeters).isGreaterThan(0.0)
        }
    }

    @Test
    fun `getNearestExitPerCategory calculates walking time correctly`() = runTest {
        coEvery { exitNodeDao.getAll() } returns flowOf(allNodes)

        val results = repository.getNearestExitPerCategory(userLocation)

        results.forEach { route ->
            // Walking time = ceil(distance / (5000/60))
            val expectedMinutes = kotlin.math.ceil(
                route.distanceMeters / (5000.0 / 60.0)
            ).toInt()
            assertThat(route.estimatedWalkingMinutes).isEqualTo(expectedMinutes)
        }
    }

    @Test
    fun `getNearestExitPerCategory returns empty list when no nodes exist`() = runTest {
        coEvery { exitNodeDao.getAll() } returns flowOf(emptyList())

        val results = repository.getNearestExitPerCategory(userLocation)

        assertThat(results).isEmpty()
    }

    @Test
    fun `getNearestExitPerCategory marks routes as not alternate`() = runTest {
        coEvery { exitNodeDao.getAll() } returns flowOf(allNodes)

        val results = repository.getNearestExitPerCategory(userLocation)

        results.forEach { route ->
            assertThat(route.isAlternate).isFalse()
        }
    }

    // --- getRoutesFromPandal tests ---

    @Test
    fun `getRoutesFromPandal returns mapped routes`() = runTest {
        val connectors = listOf(
            createConnector("c1", "pandal-1", "metro-1", 500.0, isWellLit = true, isAlternate = false),
            createConnector("c2", "pandal-1", "railway-1", 800.0, isWellLit = false, isAlternate = false)
        )
        coEvery { connectorDao.getByPandalId("pandal-1") } returns connectors
        coEvery { exitNodeDao.getAll() } returns flowOf(allNodes)

        val results = repository.getRoutesFromPandal("pandal-1")

        assertThat(results).hasSize(2)
        assertThat(results[0].distanceMeters).isEqualTo(500.0)
        assertThat(results[1].distanceMeters).isEqualTo(800.0)
    }

    @Test
    fun `getRoutesFromPandal with nightSafetyMode sorts well-lit first`() = runTest {
        val connectors = listOf(
            createConnector("c1", "pandal-1", "medical-1", 300.0, isWellLit = false, isAlternate = false),
            createConnector("c2", "pandal-1", "metro-1", 700.0, isWellLit = true, isAlternate = false),
            createConnector("c3", "pandal-1", "railway-1", 500.0, isWellLit = true, isAlternate = false)
        )
        coEvery { connectorDao.getByPandalId("pandal-1") } returns connectors
        coEvery { exitNodeDao.getAll() } returns flowOf(allNodes)

        val results = repository.getRoutesFromPandal("pandal-1", nightSafetyMode = true)

        // Well-lit routes should come first
        assertThat(results[0].exitNode.isWellLit).isTrue()
        assertThat(results[1].exitNode.isWellLit).isTrue()
        // Among well-lit, sorted by distance
        assertThat(results[0].distanceMeters).isLessThan(results[1].distanceMeters)
        // Non-well-lit last
        assertThat(results[2].exitNode.isWellLit).isFalse()
    }

    @Test
    fun `getRoutesFromPandal without nightSafetyMode sorts by distance`() = runTest {
        val connectors = listOf(
            createConnector("c1", "pandal-1", "metro-1", 700.0, isWellLit = true, isAlternate = false),
            createConnector("c2", "pandal-1", "medical-1", 300.0, isWellLit = false, isAlternate = false)
        )
        coEvery { connectorDao.getByPandalId("pandal-1") } returns connectors
        coEvery { exitNodeDao.getAll() } returns flowOf(allNodes)

        val results = repository.getRoutesFromPandal("pandal-1", nightSafetyMode = false)

        assertThat(results[0].distanceMeters).isEqualTo(300.0)
        assertThat(results[1].distanceMeters).isEqualTo(700.0)
    }

    @Test
    fun `getRoutesFromPandal calculates walking time for connectors`() = runTest {
        val connectors = listOf(
            createConnector("c1", "pandal-1", "metro-1", 1000.0, isWellLit = true, isAlternate = false)
        )
        coEvery { connectorDao.getByPandalId("pandal-1") } returns connectors
        coEvery { exitNodeDao.getAll() } returns flowOf(allNodes)

        val results = repository.getRoutesFromPandal("pandal-1")

        // 1000m / (5000/60) = 1000 / 83.33 = 12.0 -> ceil = 12
        assertThat(results[0].estimatedWalkingMinutes).isEqualTo(12)
    }

    @Test
    fun `getRoutesFromPandal returns empty when no connectors exist`() = runTest {
        coEvery { connectorDao.getByPandalId("pandal-1") } returns emptyList()
        coEvery { exitNodeDao.getAll() } returns flowOf(allNodes)

        val results = repository.getRoutesFromPandal("pandal-1")

        assertThat(results).isEmpty()
    }

    // --- getAlternateRoutes tests ---

    @Test
    fun `getAlternateRoutes returns all connectors for pandal-exit pair`() = runTest {
        val connectors = listOf(
            createConnector("c1", "pandal-1", "metro-1", 500.0, isWellLit = true, isAlternate = false),
            createConnector("c2", "pandal-1", "metro-1", 650.0, isWellLit = false, isAlternate = true)
        )
        coEvery { connectorDao.getByPandalAndExit("pandal-1", "metro-1") } returns connectors
        coEvery { exitNodeDao.getAll() } returns flowOf(allNodes)

        val results = repository.getAlternateRoutes("pandal-1", "metro-1")

        assertThat(results).hasSize(2)
    }

    @Test
    fun `getAlternateRoutes sorts by distance`() = runTest {
        val connectors = listOf(
            createConnector("c1", "pandal-1", "metro-1", 650.0, isWellLit = true, isAlternate = true),
            createConnector("c2", "pandal-1", "metro-1", 500.0, isWellLit = false, isAlternate = false)
        )
        coEvery { connectorDao.getByPandalAndExit("pandal-1", "metro-1") } returns connectors
        coEvery { exitNodeDao.getAll() } returns flowOf(allNodes)

        val results = repository.getAlternateRoutes("pandal-1", "metro-1")

        assertThat(results[0].distanceMeters).isEqualTo(500.0)
        assertThat(results[1].distanceMeters).isEqualTo(650.0)
    }

    @Test
    fun `getAlternateRoutes returns empty when exit node not found`() = runTest {
        coEvery { connectorDao.getByPandalAndExit("pandal-1", "nonexistent") } returns emptyList()
        coEvery { exitNodeDao.getAll() } returns flowOf(allNodes)

        val results = repository.getAlternateRoutes("pandal-1", "nonexistent")

        assertThat(results).isEmpty()
    }

    @Test
    fun `getAlternateRoutes preserves isAlternate flag`() = runTest {
        val connectors = listOf(
            createConnector("c1", "pandal-1", "metro-1", 500.0, isWellLit = true, isAlternate = false),
            createConnector("c2", "pandal-1", "metro-1", 650.0, isWellLit = false, isAlternate = true)
        )
        coEvery { connectorDao.getByPandalAndExit("pandal-1", "metro-1") } returns connectors
        coEvery { exitNodeDao.getAll() } returns flowOf(allNodes)

        val results = repository.getAlternateRoutes("pandal-1", "metro-1")

        val primary = results.first { !it.isAlternate }
        val alternate = results.first { it.isAlternate }
        assertThat(primary.distanceMeters).isEqualTo(500.0)
        assertThat(alternate.distanceMeters).isEqualTo(650.0)
    }

    // --- Walking time calculation edge cases ---

    @Test
    fun `walking time rounds up for fractional minutes`() = runTest {
        // 100m / (5000/60) = 100 / 83.33 = 1.2 -> ceil = 2
        val connectors = listOf(
            createConnector("c1", "pandal-1", "metro-1", 100.0, isWellLit = true, isAlternate = false)
        )
        coEvery { connectorDao.getByPandalId("pandal-1") } returns connectors
        coEvery { exitNodeDao.getAll() } returns flowOf(allNodes)

        val results = repository.getRoutesFromPandal("pandal-1")

        assertThat(results[0].estimatedWalkingMinutes).isEqualTo(2)
    }

    @Test
    fun `walking time is 1 minute for very short distance`() = runTest {
        // 50m / (5000/60) = 50 / 83.33 = 0.6 -> ceil = 1
        val connectors = listOf(
            createConnector("c1", "pandal-1", "metro-1", 50.0, isWellLit = true, isAlternate = false)
        )
        coEvery { connectorDao.getByPandalId("pandal-1") } returns connectors
        coEvery { exitNodeDao.getAll() } returns flowOf(allNodes)

        val results = repository.getRoutesFromPandal("pandal-1")

        assertThat(results[0].estimatedWalkingMinutes).isEqualTo(1)
    }

    // --- Helper ---

    private fun createConnector(
        id: String,
        pandalId: String,
        exitNodeId: String,
        distanceMeters: Double,
        isWellLit: Boolean,
        isAlternate: Boolean
    ): ConnectorEntity {
        return ConnectorEntity(
            id = id,
            pandalId = pandalId,
            exitNodeId = exitNodeId,
            polylineJson = "[[22.56,88.35],[22.55,88.35]]",
            distanceMeters = distanceMeters,
            isWellLit = isWellLit,
            isAlternate = isAlternate
        )
    }
}
