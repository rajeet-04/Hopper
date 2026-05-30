package com.example.hopper.data.local.assets

import android.content.res.AssetManager
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream

class GeoJsonAssetLoaderTest {

    private lateinit var assetManager: AssetManager
    private lateinit var loader: GeoJsonAssetLoader

    @Before
    fun setup() {
        assetManager = mockk()
        loader = GeoJsonAssetLoader(assetManager)
    }

    // --- loadPandals tests ---

    @Test
    fun `loadPandals parses valid GeoJSON FeatureCollection`() {
        val geojson = """
        {
          "type": "FeatureCollection",
          "features": [
            {
              "type": "Feature",
              "geometry": { "type": "Point", "coordinates": [88.3629, 22.5726] },
              "properties": {
                "id": "pandal_001",
                "name": "Bagbazar Sarbojanin",
                "name_bengali": "বাগবাজার সার্বজনীন",
                "city": "Kolkata",
                "neighborhood": "Bagbazar",
                "festival": "DURGA_PUJA",
                "year": 2026,
                "theme": "Environmental Awareness",
                "committee_name": "Bagbazar Sarbojanin Durgotsab",
                "established_year": 1919,
                "artisan_credits_json": "{\"idolMaker\":\"Sankar Paul\"}",
                "awards": ["Best Idol 2025", "Heritage Award"],
                "photos": ["url1.jpg", "url2.jpg"],
                "significance_rank": 1,
                "source_type": "COMMITTEE",
                "confidence_level": "HIGH"
              }
            }
          ]
        }
        """.trimIndent()

        mockAssetFile("pandals.geojson", geojson)

        val result = loader.loadPandals("pandals.geojson")

        assertEquals(1, result.size)
        val pandal = result[0]
        assertEquals("pandal_001", pandal.id)
        assertEquals("Bagbazar Sarbojanin", pandal.name)
        assertEquals("বাগবাজার সার্বজনীন", pandal.nameBengali)
        assertEquals(22.5726, pandal.latitude, 0.0001)
        assertEquals(88.3629, pandal.longitude, 0.0001)
        assertEquals("Kolkata", pandal.city)
        assertEquals("Bagbazar", pandal.neighborhood)
        assertEquals("DURGA_PUJA", pandal.festival)
        assertEquals(2026, pandal.year)
        assertEquals("Environmental Awareness", pandal.theme)
        assertEquals("Bagbazar Sarbojanin Durgotsab", pandal.committeeName)
        assertEquals(1919, pandal.establishedYear)
        assertEquals("{\"idolMaker\":\"Sankar Paul\"}", pandal.artisanCreditsJson)
        assertEquals("Best Idol 2025,Heritage Award", pandal.awards)
        assertEquals("url1.jpg,url2.jpg", pandal.photos)
        assertEquals(1, pandal.significanceRank)
        assertEquals("COMMITTEE", pandal.sourceType)
        assertEquals("HIGH", pandal.confidenceLevel)
    }

    @Test
    fun `loadPandals handles missing optional fields`() {
        val geojson = """
        {
          "type": "FeatureCollection",
          "features": [
            {
              "type": "Feature",
              "geometry": { "type": "Point", "coordinates": [88.0, 22.0] },
              "properties": {
                "id": "pandal_002",
                "name": "Test Pandal",
                "city": "Kolkata",
                "festival": "DURGA_PUJA",
                "year": 2026
              }
            }
          ]
        }
        """.trimIndent()

        mockAssetFile("pandals.geojson", geojson)

        val result = loader.loadPandals("pandals.geojson")

        assertEquals(1, result.size)
        val pandal = result[0]
        assertEquals("pandal_002", pandal.id)
        assertNull(pandal.nameBengali)
        assertNull(pandal.neighborhood)
        assertNull(pandal.theme)
        assertNull(pandal.committeeName)
        assertNull(pandal.establishedYear)
        assertNull(pandal.artisanCreditsJson)
        assertNull(pandal.awards)
        assertNull(pandal.photos)
        assertEquals(Int.MAX_VALUE, pandal.significanceRank)
        assertEquals("UNKNOWN", pandal.sourceType)
        assertEquals("LOW", pandal.confidenceLevel)
    }

    @Test
    fun `loadPandals skips malformed features and continues`() {
        val geojson = """
        {
          "type": "FeatureCollection",
          "features": [
            { "type": "Feature", "geometry": {}, "properties": {} },
            {
              "type": "Feature",
              "geometry": { "type": "Point", "coordinates": [88.0, 22.0] },
              "properties": {
                "id": "pandal_valid",
                "name": "Valid Pandal",
                "city": "Kolkata",
                "festival": "DURGA_PUJA",
                "year": 2026
              }
            }
          ]
        }
        """.trimIndent()

        mockAssetFile("pandals.geojson", geojson)

        val result = loader.loadPandals("pandals.geojson")

        assertEquals(1, result.size)
        assertEquals("pandal_valid", result[0].id)
    }

    @Test
    fun `loadPandals returns empty list for missing file`() {
        every { assetManager.open("missing.geojson") } throws java.io.FileNotFoundException("not found")

        val result = loader.loadPandals("missing.geojson")

        assertTrue(result.isEmpty())
    }

    // --- loadExitNodes tests ---

    @Test
    fun `loadExitNodes parses valid GeoJSON FeatureCollection`() {
        val geojson = """
        {
          "type": "FeatureCollection",
          "features": [
            {
              "type": "Feature",
              "geometry": { "type": "Point", "coordinates": [88.37, 22.60] },
              "properties": {
                "id": "exit_001",
                "name": "Shyambazar Metro",
                "name_bengali": "শ্যামবাজার মেট্রো",
                "category": "METRO",
                "contact_number": "+91-33-2555-1234",
                "is_24hr": true,
                "is_well_lit": true
              }
            }
          ]
        }
        """.trimIndent()

        mockAssetFile("exit_nodes.geojson", geojson)

        val result = loader.loadExitNodes("exit_nodes.geojson")

        assertEquals(1, result.size)
        val node = result[0]
        assertEquals("exit_001", node.id)
        assertEquals("Shyambazar Metro", node.name)
        assertEquals("শ্যামবাজার মেট্রো", node.nameBengali)
        assertEquals("METRO", node.category)
        assertEquals(22.60, node.latitude, 0.0001)
        assertEquals(88.37, node.longitude, 0.0001)
        assertEquals("+91-33-2555-1234", node.contactNumber)
        assertTrue(node.is24Hr)
        assertTrue(node.isWellLit)
    }

    @Test
    fun `loadExitNodes handles missing optional fields`() {
        val geojson = """
        {
          "type": "FeatureCollection",
          "features": [
            {
              "type": "Feature",
              "geometry": { "type": "Point", "coordinates": [88.0, 22.0] },
              "properties": {
                "id": "exit_002",
                "name": "Police Booth",
                "category": "POLICE"
              }
            }
          ]
        }
        """.trimIndent()

        mockAssetFile("exit_nodes.geojson", geojson)

        val result = loader.loadExitNodes("exit_nodes.geojson")

        assertEquals(1, result.size)
        val node = result[0]
        assertNull(node.nameBengali)
        assertNull(node.contactNumber)
        assertEquals(false, node.is24Hr)
        assertEquals(false, node.isWellLit)
    }

    // --- loadConnectors tests ---

    @Test
    fun `loadConnectors parses valid GeoJSON with LineString geometry`() {
        val geojson = """
        {
          "type": "FeatureCollection",
          "features": [
            {
              "type": "Feature",
              "geometry": {
                "type": "LineString",
                "coordinates": [[88.36, 22.57], [88.37, 22.58], [88.37, 22.60]]
              },
              "properties": {
                "id": "conn_001",
                "pandal_id": "pandal_001",
                "exit_node_id": "exit_001",
                "distance_meters": 850.5,
                "is_well_lit": true,
                "is_alternate": false
              }
            }
          ]
        }
        """.trimIndent()

        mockAssetFile("connectors.geojson", geojson)

        val result = loader.loadConnectors("connectors.geojson")

        assertEquals(1, result.size)
        val connector = result[0]
        assertEquals("conn_001", connector.id)
        assertEquals("pandal_001", connector.pandalId)
        assertEquals("exit_001", connector.exitNodeId)
        assertEquals(850.5, connector.distanceMeters, 0.01)
        assertTrue(connector.isWellLit)
        assertEquals(false, connector.isAlternate)
        // polylineJson should be the raw coordinates array as string
        assertTrue(connector.polylineJson.startsWith("["))
        assertTrue(connector.polylineJson.contains("88.3"))
        assertTrue(connector.polylineJson.contains("22.6"))
    }

    // --- loadCalendar tests ---

    @Test
    fun `loadCalendar parses valid JSON array`() {
        val json = """
        [
          {
            "id": "tithi_001",
            "festival": "DURGA_PUJA",
            "year": 2026,
            "name": "Shashti",
            "name_bengali": "ষষ্ঠী",
            "date": 1791936000000,
            "cultural_significance": "Beginning of festivities",
            "cultural_significance_bengali": "উৎসবের সূচনা",
            "is_peak_crowd": false
          },
          {
            "id": "tithi_002",
            "festival": "DURGA_PUJA",
            "year": 2026,
            "name": "Ashtami",
            "date": 1792108800000,
            "is_peak_crowd": true
          }
        ]
        """.trimIndent()

        mockAssetFile("calendar.json", json)

        val result = loader.loadCalendar("calendar.json")

        assertEquals(2, result.size)

        val first = result[0]
        assertEquals("tithi_001", first.id)
        assertEquals("DURGA_PUJA", first.festival)
        assertEquals(2026, first.year)
        assertEquals("Shashti", first.name)
        assertEquals("ষষ্ঠী", first.nameBengali)
        assertEquals(1791936000000L, first.date)
        assertEquals("Beginning of festivities", first.culturalSignificance)
        assertEquals("উৎসবের সূচনা", first.culturalSignificanceBengali)
        assertEquals(false, first.isPeakCrowd)

        val second = result[1]
        assertEquals("tithi_002", second.id)
        assertNull(second.nameBengali)
        assertNull(second.culturalSignificance)
        assertTrue(second.isPeakCrowd)
    }

    // --- loadHistoricalPatterns tests ---

    @Test
    fun `loadHistoricalPatterns parses valid JSON array`() {
        val json = """
        [
          {
            "id": "pattern_001",
            "pandal_id": "pandal_001",
            "day_of_festival": 3,
            "hour_of_day": 21,
            "predicted_bucket": "RED",
            "year": 2025
          }
        ]
        """.trimIndent()

        mockAssetFile("historical_crowd_patterns.json", json)

        val result = loader.loadHistoricalPatterns("historical_crowd_patterns.json")

        assertEquals(1, result.size)
        val pattern = result[0]
        assertEquals("pattern_001", pattern.id)
        assertEquals("pandal_001", pattern.pandalId)
        assertEquals(3, pattern.dayOfFestival)
        assertEquals(21, pattern.hourOfDay)
        assertEquals("RED", pattern.predictedBucket)
        assertEquals(2025, pattern.year)
    }

    // --- Edge case tests ---

    @Test
    fun `loadPandals returns empty list for invalid JSON`() {
        mockAssetFile("bad.geojson", "not valid json at all")

        val result = loader.loadPandals("bad.geojson")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `loadPandals returns empty list for empty features array`() {
        val geojson = """{"type": "FeatureCollection", "features": []}"""
        mockAssetFile("empty.geojson", geojson)

        val result = loader.loadPandals("empty.geojson")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `loadCalendar returns empty list for invalid JSON`() {
        mockAssetFile("bad.json", "{ not an array }")

        val result = loader.loadCalendar("bad.json")

        assertTrue(result.isEmpty())
    }

    // --- Helper ---

    private fun mockAssetFile(filename: String, content: String) {
        every { assetManager.open(filename) } returns ByteArrayInputStream(content.toByteArray())
    }
}
