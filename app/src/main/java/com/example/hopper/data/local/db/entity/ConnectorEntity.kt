package com.example.hopper.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a precomputed walking connector between a pandal and an exit node.
 * Maps to the "connectors" table.
 */
@Entity(
    tableName = "connectors",
    foreignKeys = [
        ForeignKey(
            entity = PandalEntity::class,
            parentColumns = ["id"],
            childColumns = ["pandal_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExitNodeEntity::class,
            parentColumns = ["id"],
            childColumns = ["exit_node_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("pandal_id"),
        Index("exit_node_id")
    ]
)
data class ConnectorEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "pandal_id")
    val pandalId: String,
    @ColumnInfo(name = "exit_node_id")
    val exitNodeId: String,
    @ColumnInfo(name = "polyline_json")
    val polylineJson: String,
    @ColumnInfo(name = "distance_meters")
    val distanceMeters: Double,
    @ColumnInfo(name = "is_well_lit")
    val isWellLit: Boolean,
    @ColumnInfo(name = "is_alternate")
    val isAlternate: Boolean
)
