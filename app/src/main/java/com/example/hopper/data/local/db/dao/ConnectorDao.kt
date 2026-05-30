package com.example.hopper.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hopper.data.local.db.entity.ConnectorEntity

/**
 * Data Access Object for connector (pandal-to-exit-node walking route) operations.
 */
@Dao
interface ConnectorDao {

    @Query("SELECT * FROM connectors WHERE pandal_id = :pandalId")
    suspend fun getByPandalId(pandalId: String): List<ConnectorEntity>

    @Query("SELECT * FROM connectors WHERE pandal_id = :pandalId AND exit_node_id = :exitNodeId")
    suspend fun getByPandalAndExit(pandalId: String, exitNodeId: String): List<ConnectorEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(connectors: List<ConnectorEntity>)

    @Query("DELETE FROM connectors")
    suspend fun deleteAll()
}
