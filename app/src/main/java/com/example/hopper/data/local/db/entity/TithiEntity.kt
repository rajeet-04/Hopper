package com.example.hopper.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a tithi (sacred day) in the festival calendar.
 * Maps to the "tithis" table.
 */
@Entity(tableName = "tithis")
data class TithiEntity(
    @PrimaryKey
    val id: String,
    val festival: String,
    val year: Int,
    val name: String,
    @ColumnInfo(name = "name_bengali")
    val nameBengali: String?,
    val date: Long,
    @ColumnInfo(name = "cultural_significance")
    val culturalSignificance: String?,
    @ColumnInfo(name = "cultural_significance_bengali")
    val culturalSignificanceBengali: String?,
    @ColumnInfo(name = "is_peak_crowd")
    val isPeakCrowd: Boolean
)
