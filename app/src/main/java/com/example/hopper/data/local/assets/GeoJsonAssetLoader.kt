package com.example.hopper.data.local.assets

import android.content.res.AssetManager
import android.util.Log
import com.example.hopper.data.local.db.entity.ConnectorEntity
import com.example.hopper.data.local.db.entity.ExitNodeEntity
import com.example.hopper.data.local.db.entity.HistoricalCrowdPatternEntity
import com.example.hopper.data.local.db.entity.PandalEntity
import com.example.hopper.data.local.db.entity.TithiEntity
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Loads and parses bundled GeoJSON and JSON asset files from the app's assets/ folder.
 * Used for first-launch database seeding and offline-first data availability.
 *
 * Handles malformed features gracefully by skipping invalid entries and logging warnings.
 */
@Singleton
class GeoJsonAssetLoader @Inject constructor(
    private val assetManager: AssetManager
) {

    companion object {
        private const val TAG = "GeoJsonAssetLoader"
    }

    /**
     * Loads pandal data from a GeoJSON FeatureCollection file.
     * Each Feature must have a Point geometry and properties matching PandalEntity fields.
     *
     * @param filename The asset filename (e.g., "pandals.geojson")
     * @return List of parsed PandalEntity objects; malformed features are skipped.
     */
    fun loadPandals(filename: String): List<PandalEntity> {
        val json = readAssetFile(filename) ?: return emptyList()
        val featureCollection = tryParseJson(json) ?: return emptyList()
        val features = featureCollection.optJSONArray("features") ?: return emptyList()

        return buildList {
            for (i in 0 until features.length()) {
                try {
                    val feature = features.getJSONObject(i)
                    val geometry = feature.getJSONObject("geometry")
                    val coordinates = geometry.getJSONArray("coordinates")
                    val props = feature.getJSONObject("properties")

                    val longitude = coordinates.getDouble(0)
                    val latitude = coordinates.getDouble(1)

                    add(
                        PandalEntity(
                            id = props.getString("id"),
                            name = props.getString("name"),
                            nameBengali = props.optStringOrNull("name_bengali"),
                            latitude = latitude,
                            longitude = longitude,
                            city = props.getString("city"),
                            neighborhood = props.optStringOrNull("neighborhood"),
                            festival = props.getString("festival"),
                            year = props.getInt("year"),
                            theme = props.optStringOrNull("theme"),
                            committeeName = props.optStringOrNull("committee_name"),
                            establishedYear = props.optIntOrNull("established_year"),
                            artisanCreditsJson = props.optStringOrNull("artisan_credits_json"),
                            awards = props.optJSONArray("awards")?.toStringList()?.joinToString(","),
                            photos = props.optJSONArray("photos")?.toStringList()?.joinToString(","),
                            significanceRank = props.optInt("significance_rank", Int.MAX_VALUE),
                            sourceType = props.optString("source_type", "UNKNOWN"),
                            confidenceLevel = props.optString("confidence_level", "LOW")
                        )
                    )
                } catch (e: Exception) {
                    Log.w(TAG, "Skipping malformed pandal feature at index $i: ${e.message}")
                }
            }
        }
    }

    /**
     * Loads exit node data from a GeoJSON FeatureCollection file.
     * Each Feature must have a Point geometry and properties matching ExitNodeEntity fields.
     *
     * @param filename The asset filename (e.g., "exit_nodes.geojson")
     * @return List of parsed ExitNodeEntity objects; malformed features are skipped.
     */
    fun loadExitNodes(filename: String): List<ExitNodeEntity> {
        val json = readAssetFile(filename) ?: return emptyList()
        val featureCollection = tryParseJson(json) ?: return emptyList()
        val features = featureCollection.optJSONArray("features") ?: return emptyList()

        return buildList {
            for (i in 0 until features.length()) {
                try {
                    val feature = features.getJSONObject(i)
                    val geometry = feature.getJSONObject("geometry")
                    val coordinates = geometry.getJSONArray("coordinates")
                    val props = feature.getJSONObject("properties")

                    val longitude = coordinates.getDouble(0)
                    val latitude = coordinates.getDouble(1)

                    add(
                        ExitNodeEntity(
                            id = props.getString("id"),
                            name = props.getString("name"),
                            nameBengali = props.optStringOrNull("name_bengali"),
                            category = props.getString("category"),
                            latitude = latitude,
                            longitude = longitude,
                            contactNumber = props.optStringOrNull("contact_number"),
                            is24Hr = props.optBoolean("is_24hr", false),
                            isWellLit = props.optBoolean("is_well_lit", false)
                        )
                    )
                } catch (e: Exception) {
                    Log.w(TAG, "Skipping malformed exit node feature at index $i: ${e.message}")
                }
            }
        }
    }

    /**
     * Loads connector data from a GeoJSON FeatureCollection file.
     * Each Feature must have a LineString geometry and properties matching ConnectorEntity fields.
     * The LineString coordinates are stored as a JSON string in polylineJson.
     *
     * @param filename The asset filename (e.g., "connectors.geojson")
     * @return List of parsed ConnectorEntity objects; malformed features are skipped.
     */
    fun loadConnectors(filename: String): List<ConnectorEntity> {
        val json = readAssetFile(filename) ?: return emptyList()
        val featureCollection = tryParseJson(json) ?: return emptyList()
        val features = featureCollection.optJSONArray("features") ?: return emptyList()

        return buildList {
            for (i in 0 until features.length()) {
                try {
                    val feature = features.getJSONObject(i)
                    val geometry = feature.getJSONObject("geometry")
                    val coordinates = geometry.getJSONArray("coordinates")
                    val props = feature.getJSONObject("properties")

                    add(
                        ConnectorEntity(
                            id = props.getString("id"),
                            pandalId = props.getString("pandal_id"),
                            exitNodeId = props.getString("exit_node_id"),
                            polylineJson = coordinates.toString(),
                            distanceMeters = props.getDouble("distance_meters"),
                            isWellLit = props.optBoolean("is_well_lit", false),
                            isAlternate = props.optBoolean("is_alternate", false)
                        )
                    )
                } catch (e: Exception) {
                    Log.w(TAG, "Skipping malformed connector feature at index $i: ${e.message}")
                }
            }
        }
    }

    /**
     * Loads calendar/tithi data from a JSON array file (non-GeoJSON).
     * Expected format: a JSON array of tithi objects.
     *
     * @param filename The asset filename (e.g., "calendar.json")
     * @return List of parsed TithiEntity objects; malformed entries are skipped.
     */
    fun loadCalendar(filename: String): List<TithiEntity> {
        val json = readAssetFile(filename) ?: return emptyList()
        val array = tryParseJsonArray(json) ?: return emptyList()

        return buildList {
            for (i in 0 until array.length()) {
                try {
                    val obj = array.getJSONObject(i)
                    add(
                        TithiEntity(
                            id = obj.getString("id"),
                            festival = obj.getString("festival"),
                            year = obj.getInt("year"),
                            name = obj.getString("name"),
                            nameBengali = obj.optStringOrNull("name_bengali"),
                            date = obj.getLong("date"),
                            culturalSignificance = obj.optStringOrNull("cultural_significance"),
                            culturalSignificanceBengali = obj.optStringOrNull("cultural_significance_bengali"),
                            isPeakCrowd = obj.optBoolean("is_peak_crowd", false)
                        )
                    )
                } catch (e: Exception) {
                    Log.w(TAG, "Skipping malformed calendar entry at index $i: ${e.message}")
                }
            }
        }
    }

    /**
     * Loads historical crowd pattern data from a JSON array file (non-GeoJSON).
     * Expected format: a JSON array of pattern objects.
     *
     * @param filename The asset filename (e.g., "historical_crowd_patterns.json")
     * @return List of parsed HistoricalCrowdPatternEntity objects; malformed entries are skipped.
     */
    fun loadHistoricalPatterns(filename: String): List<HistoricalCrowdPatternEntity> {
        val json = readAssetFile(filename) ?: return emptyList()
        val array = tryParseJsonArray(json) ?: return emptyList()

        return buildList {
            for (i in 0 until array.length()) {
                try {
                    val obj = array.getJSONObject(i)
                    add(
                        HistoricalCrowdPatternEntity(
                            id = obj.getString("id"),
                            pandalId = obj.getString("pandal_id"),
                            dayOfFestival = obj.getInt("day_of_festival"),
                            hourOfDay = obj.getInt("hour_of_day"),
                            predictedBucket = obj.getString("predicted_bucket"),
                            year = obj.getInt("year")
                        )
                    )
                } catch (e: Exception) {
                    Log.w(TAG, "Skipping malformed historical pattern at index $i: ${e.message}")
                }
            }
        }
    }

    /**
     * Seeds the database with all bundled asset data on first launch.
     * Loads pandals from both Durga Puja and Jagaddhatri Puja GeoJSON files,
     * plus exit nodes, connectors, calendar, and historical crowd patterns.
     *
     * @param insertPandals Suspend function to insert pandals into the database
     * @param insertExitNodes Suspend function to insert exit nodes into the database
     * @param insertConnectors Suspend function to insert connectors into the database
     * @param insertCalendar Suspend function to insert tithis into the database
     * @param insertHistoricalPatterns Suspend function to insert historical patterns into the database
     */
    suspend fun seedDatabase(
        insertPandals: suspend (List<PandalEntity>) -> Unit,
        insertExitNodes: suspend (List<ExitNodeEntity>) -> Unit,
        insertConnectors: suspend (List<ConnectorEntity>) -> Unit,
        insertCalendar: suspend (List<TithiEntity>) -> Unit,
        insertHistoricalPatterns: suspend (List<HistoricalCrowdPatternEntity>) -> Unit
    ) {
        // Load pandals from both festival files
        val durgaPandals = loadPandals("pandals_durga_puja_2026.geojson")
        val jagaddhatriPandals = loadPandals("pandals_jagaddhatri_puja_2026.geojson")
        val allPandals = durgaPandals + jagaddhatriPandals
        if (allPandals.isNotEmpty()) {
            insertPandals(allPandals)
            Log.i(TAG, "Seeded ${allPandals.size} pandals (${durgaPandals.size} Durga Puja + ${jagaddhatriPandals.size} Jagaddhatri Puja)")
        }

        val exitNodes = loadExitNodes("exit_nodes.geojson")
        if (exitNodes.isNotEmpty()) {
            insertExitNodes(exitNodes)
            Log.i(TAG, "Seeded ${exitNodes.size} exit nodes")
        }

        val connectors = loadConnectors("connectors.geojson")
        if (connectors.isNotEmpty()) {
            insertConnectors(connectors)
            Log.i(TAG, "Seeded ${connectors.size} connectors")
        }

        val calendar = loadCalendar("calendar.json")
        if (calendar.isNotEmpty()) {
            insertCalendar(calendar)
            Log.i(TAG, "Seeded ${calendar.size} tithi entries")
        }

        val patterns = loadHistoricalPatterns("historical_crowd_patterns.json")
        if (patterns.isNotEmpty()) {
            insertHistoricalPatterns(patterns)
            Log.i(TAG, "Seeded ${patterns.size} historical crowd patterns")
        }
    }

    // --- Private helpers ---

    private fun readAssetFile(filename: String): String? {
        return try {
            assetManager.open(filename).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read asset file '$filename': ${e.message}")
            null
        }
    }

    private fun tryParseJson(json: String): JSONObject? {
        return try {
            JSONObject(json)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse JSON object: ${e.message}")
            null
        }
    }

    private fun tryParseJsonArray(json: String): JSONArray? {
        return try {
            JSONArray(json)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse JSON array: ${e.message}")
            null
        }
    }

    private fun JSONArray.toStringList(): List<String> {
        return buildList {
            for (i in 0 until this@toStringList.length()) {
                add(this@toStringList.getString(i))
            }
        }
    }

    private fun JSONObject.optStringOrNull(key: String): String? {
        return if (has(key) && !isNull(key)) getString(key) else null
    }

    private fun JSONObject.optIntOrNull(key: String): Int? {
        return if (has(key) && !isNull(key)) getInt(key) else null
    }
}
