package com.example.hopper.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a user-created pandal-hopping itinerary.
 * Maps to the "itineraries" table.
 */
@Entity(tableName = "itineraries")
data class ItineraryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    @ColumnInfo(name = "created_at_epoch_ms")
    val createdAtEpochMs: Long,
    @ColumnInfo(name = "total_distance_km")
    val totalDistanceKm: Double,
    @ColumnInfo(name = "total_time_minutes")
    val totalTimeMinutes: Int,
    val festival: String,
    val year: Int
)
