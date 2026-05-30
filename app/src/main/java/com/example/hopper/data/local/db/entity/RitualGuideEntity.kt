package com.example.hopper.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a ritual guide associated with a tithi.
 * Maps to the "ritual_guides" table.
 */
@Entity(
    tableName = "ritual_guides",
    foreignKeys = [
        ForeignKey(
            entity = TithiEntity::class,
            parentColumns = ["id"],
            childColumns = ["tithi_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AudioAssetEntity::class,
            parentColumns = ["id"],
            childColumns = ["audio_asset_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("tithi_id"),
        Index("audio_asset_id")
    ]
)
data class RitualGuideEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "tithi_id")
    val tithiId: String,
    val title: String,
    @ColumnInfo(name = "title_bengali")
    val titleBengali: String?,
    val steps: String?,
    @ColumnInfo(name = "timing_notes")
    val timingNotes: String?,
    @ColumnInfo(name = "timing_notes_bengali")
    val timingNotesBengali: String?,
    @ColumnInfo(name = "audio_asset_id")
    val audioAssetId: String?,
    val festival: String,
    val year: Int
)
