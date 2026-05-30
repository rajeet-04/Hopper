package com.example.hopper.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a yearly edition of a pandal (theme, awards, photo for a given year).
 * Maps to the "editions" table.
 */
@Entity(
    tableName = "editions",
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
data class EditionEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "pandal_id")
    val pandalId: String,
    val year: Int,
    val theme: String?,
    val awards: String?,
    @ColumnInfo(name = "photo_url")
    val photoUrl: String?
)
