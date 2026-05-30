package com.example.hopper.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing an oral history entry associated with a pandal.
 * Maps to the "oral_histories" table.
 */
@Entity(
    tableName = "oral_histories",
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
data class OralHistoryEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "pandal_id")
    val pandalId: String,
    val title: String,
    @ColumnInfo(name = "title_bengali")
    val titleBengali: String?,
    @ColumnInfo(name = "contributor_name")
    val contributorName: String?,
    val year: Int,
    @ColumnInfo(name = "text_content")
    val textContent: String?,
    @ColumnInfo(name = "text_content_bengali")
    val textContentBengali: String?,
    @ColumnInfo(name = "audio_url")
    val audioUrl: String?,
    @ColumnInfo(name = "is_audio_cached_locally")
    val isAudioCachedLocally: Boolean,
    @ColumnInfo(name = "local_audio_path")
    val localAudioPath: String?
)
