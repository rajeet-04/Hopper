package com.example.hopper.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a cached audio asset (for oral histories and ritual guides).
 * Maps to the "audio_assets" table.
 */
@Entity(tableName = "audio_assets")
data class AudioAssetEntity(
    @PrimaryKey
    val id: String,
    val url: String,
    @ColumnInfo(name = "local_path")
    val localPath: String?,
    @ColumnInfo(name = "file_size_bytes")
    val fileSizeBytes: Long,
    @ColumnInfo(name = "is_cached")
    val isCached: Boolean,
    @ColumnInfo(name = "last_accessed_epoch_ms")
    val lastAccessedEpochMs: Long
)
