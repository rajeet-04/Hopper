package com.example.hopper.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hopper.data.local.db.entity.ExitNodeEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for exit node operations.
 */
@Dao
interface ExitNodeDao {

    @Query("SELECT * FROM exit_nodes WHERE category = :category")
    fun getByCategory(category: String): Flow<List<ExitNodeEntity>>

    @Query("SELECT * FROM exit_nodes")
    fun getAll(): Flow<List<ExitNodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(nodes: List<ExitNodeEntity>)

    @Query("DELETE FROM exit_nodes")
    suspend fun deleteAll()
}
