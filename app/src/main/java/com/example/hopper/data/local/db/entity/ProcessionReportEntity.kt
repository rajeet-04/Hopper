package com.example.hopper.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a crowd-reported sighting of a procession at a location.
 * Maps to the "procession_reports" table.
 */
@Entity(
    tableName = "procession_reports",
    foreignKeys = [
        ForeignKey(
            entity = ProcessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["procession_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("procession_id")]
)
data class ProcessionReportEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "procession_id")
    val processionId: String,
    val latitude: Double,
    val longitude: Double,
    @ColumnInfo(name = "device_hash")
    val deviceHash: String,
    @ColumnInfo(name = "reported_at_epoch_ms")
    val reportedAtEpochMs: Long,
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean
)
