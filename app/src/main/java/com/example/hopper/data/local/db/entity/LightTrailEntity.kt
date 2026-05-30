package com.example.hopper.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a stop on the Chandannagar light trail.
 * Maps to the "light_trails" table.
 */
@Entity(tableName = "light_trails")
data class LightTrailEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    @ColumnInfo(name = "name_bengali")
    val nameBengali: String?,
    @ColumnInfo(name = "artist_name")
    val artistName: String?,
    val dimensions: String?,
    @ColumnInfo(name = "theme_description")
    val themeDescription: String?,
    val latitude: Double,
    val longitude: Double,
    @ColumnInfo(name = "sequence_order")
    val sequenceOrder: Int,
    @ColumnInfo(name = "is_vantage_point")
    val isVantagePoint: Boolean,
    @ColumnInfo(name = "viewing_angle")
    val viewingAngle: Float?,
    val year: Int
)
