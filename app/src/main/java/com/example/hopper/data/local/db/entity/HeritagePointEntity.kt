package com.example.hopper.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a heritage point of interest.
 * Maps to the "heritage_points" table.
 */
@Entity(tableName = "heritage_points")
data class HeritagePointEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    @ColumnInfo(name = "name_bengali")
    val nameBengali: String?,
    val description: String?,
    @ColumnInfo(name = "description_bengali")
    val descriptionBengali: String?,
    val period: String?,
    val latitude: Double,
    val longitude: Double,
    @ColumnInfo(name = "photo_url")
    val photoUrl: String?,
    val festival: String
)
