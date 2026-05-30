package com.example.hopper.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a reporter's reputation score and badge tier.
 * The primary key is the device hash (one reputation record per device).
 * Maps to the "reputations" table.
 */
@Entity(tableName = "reputations")
data class ReputationEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "total_reports")
    val totalReports: Int,
    @ColumnInfo(name = "accurate_reports")
    val accurateReports: Int,
    @ColumnInfo(name = "accuracy_score")
    val accuracyScore: Double,
    @ColumnInfo(name = "badge_tier")
    val badgeTier: String,
    @ColumnInfo(name = "weight_multiplier")
    val weightMultiplier: Double
)
