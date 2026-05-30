package com.example.hopper.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing an emergency exit point (Metro, Railway, Police, Medical).
 * Maps to the "exit_nodes" table.
 */
@Entity(tableName = "exit_nodes")
data class ExitNodeEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    @ColumnInfo(name = "name_bengali")
    val nameBengali: String?,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    @ColumnInfo(name = "contact_number")
    val contactNumber: String?,
    @ColumnInfo(name = "is_24hr")
    val is24Hr: Boolean,
    @ColumnInfo(name = "is_well_lit")
    val isWellLit: Boolean
)
