package com.example.hopper.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a lost person report post.
 * Maps to the "lost_person_posts" table.
 */
@Entity(tableName = "lost_person_posts")
data class LostPersonPostEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "display_name")
    val displayName: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    @ColumnInfo(name = "reported_by_device_hash")
    val reportedByDeviceHash: String,
    @ColumnInfo(name = "reported_at_epoch_ms")
    val reportedAtEpochMs: Long,
    @ColumnInfo(name = "expires_at_epoch_ms")
    val expiresAtEpochMs: Long,
    @ColumnInfo(name = "is_resolved")
    val isResolved: Boolean,
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean
)
