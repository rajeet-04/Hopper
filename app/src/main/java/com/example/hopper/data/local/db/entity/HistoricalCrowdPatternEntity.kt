package com.example.hopper.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing historical crowd density patterns for predictive wait times.
 * Maps to the "historical_crowd_patterns" table.
 */
@Entity(
    tableName = "historical_crowd_patterns",
    foreignKeys = [
        ForeignKey(
            entity = PandalEntity::class,
            parentColumns = ["id"],
            childColumns = ["pandal_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("pandal_id")]
)
data class HistoricalCrowdPatternEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "pandal_id")
    val pandalId: String,
    @ColumnInfo(name = "day_of_festival")
    val dayOfFestival: Int,
    @ColumnInfo(name = "hour_of_day")
    val hourOfDay: Int,
    @ColumnInfo(name = "predicted_bucket")
    val predictedBucket: String,
    val year: Int
)
