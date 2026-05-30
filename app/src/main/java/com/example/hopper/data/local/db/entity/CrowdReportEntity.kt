package com.example.hopper.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a user-submitted crowd level report for a pandal.
 * Maps to the "crowd_reports" table.
 */
@Entity(
    tableName = "crowd_reports",
    indices = [Index("pandal_id")]
)
data class CrowdReportEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "pandal_id")
    val pandalId: String,
    val bucket: String,
    @ColumnInfo(name = "device_hash")
    val deviceHash: String,
    @ColumnInfo(name = "reported_at_epoch_ms")
    val reportedAtEpochMs: Long,
    @ColumnInfo(name = "expires_at_epoch_ms")
    val expiresAtEpochMs: Long,
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean
)
