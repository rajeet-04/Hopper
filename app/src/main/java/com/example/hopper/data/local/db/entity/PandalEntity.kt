package com.example.hopper.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a pandal (temporary decorated structure housing a deity idol).
 * Maps to the "pandals" table.
 */
@Entity(tableName = "pandals")
data class PandalEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    @ColumnInfo(name = "name_bengali")
    val nameBengali: String?,
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val neighborhood: String?,
    val festival: String,
    val year: Int,
    val theme: String?,
    @ColumnInfo(name = "committee_name")
    val committeeName: String?,
    @ColumnInfo(name = "established_year")
    val establishedYear: Int?,
    @ColumnInfo(name = "artisan_credits_json")
    val artisanCreditsJson: String?,
    val awards: String?,
    val photos: String?,
    @ColumnInfo(name = "significance_rank")
    val significanceRank: Int,
    @ColumnInfo(name = "source_type")
    val sourceType: String,
    @ColumnInfo(name = "confidence_level")
    val confidenceLevel: String
)
