package com.example.hopper.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a bishorjon (immersion) procession route.
 * Maps to the "processions" table.
 */
@Entity(tableName = "processions")
data class ProcessionEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    @ColumnInfo(name = "route_polyline_json")
    val routePolylineJson: String?,
    @ColumnInfo(name = "start_time_epoch_ms")
    val startTimeEpochMs: Long,
    @ColumnInfo(name = "estimated_end_time_epoch_ms")
    val estimatedEndTimeEpochMs: Long?,
    val year: Int
)
