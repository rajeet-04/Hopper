package com.example.hopper.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hopper.data.local.db.entity.EditionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for pandal edition (yearly theme/awards) operations.
 */
@Dao
interface EditionDao {

    @Query("SELECT * FROM editions WHERE pandal_id = :pandalId ORDER BY year DESC")
    fun getByPandalId(pandalId: String): Flow<List<EditionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(editions: List<EditionEntity>)
}
