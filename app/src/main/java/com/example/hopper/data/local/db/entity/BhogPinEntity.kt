package com.example.hopper.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a community-reported food distribution or street food pin.
 * Maps to the "bhog_pins" table.
 */
@Entity(
    tableName = "bhog_pins",
    indices = [Index("pandal_id")]
)
data class BhogPinEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String?,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    @ColumnInfo(name = "pandal_id")
    val pandalId: String?,
    @ColumnInfo(name = "reported_by_device_hash")
    val reportedByDeviceHash: String,
    @ColumnInfo(name = "reported_at_epoch_ms")
    val reportedAtEpochMs: Long,
    @ColumnInfo(name = "end_time_epoch_ms")
    val endTimeEpochMs: Long?,
    val rating: Float?,
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean
)
