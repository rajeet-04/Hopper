package com.example.hopper.data.local.db

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.hopper.domain.model.ConfidenceLevel
import com.example.hopper.domain.model.CrowdBucket
import com.example.hopper.domain.model.ExitNodeCategory
import com.example.hopper.domain.model.Festival
import com.example.hopper.domain.model.SourceType
import org.json.JSONArray
import java.time.Instant

/**
 * Room type converters for JSON arrays, enums, and timestamps.
 * Register this class with @TypeConverters on the RoomDatabase.
 */
@TypeConverters
class Converters {

    // --- List<String> ↔ JSON string ---

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        if (value == null) return null
        val jsonArray = JSONArray()
        value.forEach { jsonArray.put(it) }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if (value == null) return null
        val jsonArray = JSONArray(value)
        val list = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getString(i))
        }
        return list
    }

    // --- Instant ↔ Long (epoch millis) ---

    @TypeConverter
    fun fromInstant(value: Instant?): Long? {
        return value?.toEpochMilli()
    }

    @TypeConverter
    fun toInstant(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    // --- Festival enum ↔ String ---

    @TypeConverter
    fun fromFestival(value: Festival?): String? {
        return value?.name
    }

    @TypeConverter
    fun toFestival(value: String?): Festival? {
        return value?.let { Festival.valueOf(it) }
    }

    // --- CrowdBucket enum ↔ String ---

    @TypeConverter
    fun fromCrowdBucket(value: CrowdBucket?): String? {
        return value?.name
    }

    @TypeConverter
    fun toCrowdBucket(value: String?): CrowdBucket? {
        return value?.let { CrowdBucket.valueOf(it) }
    }

    // --- ExitNodeCategory enum ↔ String ---

    @TypeConverter
    fun fromExitNodeCategory(value: ExitNodeCategory?): String? {
        return value?.name
    }

    @TypeConverter
    fun toExitNodeCategory(value: String?): ExitNodeCategory? {
        return value?.let { ExitNodeCategory.valueOf(it) }
    }

    // --- SourceType enum ↔ String ---

    @TypeConverter
    fun fromSourceType(value: SourceType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toSourceType(value: String?): SourceType? {
        return value?.let { SourceType.valueOf(it) }
    }

    // --- ConfidenceLevel enum ↔ String ---

    @TypeConverter
    fun fromConfidenceLevel(value: ConfidenceLevel?): String? {
        return value?.name
    }

    @TypeConverter
    fun toConfidenceLevel(value: String?): ConfidenceLevel? {
        return value?.let { ConfidenceLevel.valueOf(it) }
    }
}
