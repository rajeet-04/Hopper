package com.example.hopper.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a volunteer opportunity post.
 * Maps to the "volunteer_posts" table.
 */
@Entity(tableName = "volunteer_posts")
data class VolunteerPostEntity(
    @PrimaryKey
    val id: String,
    val role: String,
    @ColumnInfo(name = "role_bengali")
    val roleBengali: String?,
    val location: String,
    @ColumnInfo(name = "location_bengali")
    val locationBengali: String?,
    val latitude: Double,
    val longitude: Double,
    val date: Long,
    @ColumnInfo(name = "time_slot_start")
    val timeSlotStart: Long,
    @ColumnInfo(name = "time_slot_end")
    val timeSlotEnd: Long,
    @ColumnInfo(name = "volunteers_needed")
    val volunteersNeeded: Int,
    @ColumnInfo(name = "volunteers_signed_up")
    val volunteersSignedUp: Int,
    val festival: String,
    val year: Int,
    @ColumnInfo(name = "is_filled")
    val isFilled: Boolean
)
