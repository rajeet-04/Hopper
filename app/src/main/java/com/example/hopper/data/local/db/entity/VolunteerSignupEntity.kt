package com.example.hopper.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a volunteer signup for a specific post.
 * Maps to the "volunteer_signups" table.
 */
@Entity(
    tableName = "volunteer_signups",
    foreignKeys = [
        ForeignKey(
            entity = VolunteerPostEntity::class,
            parentColumns = ["id"],
            childColumns = ["post_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("post_id")]
)
data class VolunteerSignupEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "post_id")
    val postId: String,
    @ColumnInfo(name = "display_name")
    val displayName: String,
    @ColumnInfo(name = "encrypted_contact")
    val encryptedContact: String?,
    @ColumnInfo(name = "signed_up_at_epoch_ms")
    val signedUpAtEpochMs: Long
)
