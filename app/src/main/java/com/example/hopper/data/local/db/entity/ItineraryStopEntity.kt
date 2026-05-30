package com.example.hopper.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a single stop in an itinerary.
 * Maps to the "itinerary_stops" table.
 */
@Entity(
    tableName = "itinerary_stops",
    foreignKeys = [
        ForeignKey(
            entity = ItineraryEntity::class,
            parentColumns = ["id"],
            childColumns = ["itinerary_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PandalEntity::class,
            parentColumns = ["id"],
            childColumns = ["pandal_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("itinerary_id"),
        Index("pandal_id")
    ]
)
data class ItineraryStopEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "itinerary_id")
    val itineraryId: String,
    @ColumnInfo(name = "pandal_id")
    val pandalId: String,
    @ColumnInfo(name = "sequence_order")
    val sequenceOrder: Int,
    @ColumnInfo(name = "distance_from_previous_meters")
    val distanceFromPreviousMeters: Double,
    @ColumnInfo(name = "estimated_arrival_epoch_ms")
    val estimatedArrivalEpochMs: Long
)
