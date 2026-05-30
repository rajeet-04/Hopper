package com.example.hopper.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hopper.data.local.db.entity.RitualGuideEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for ritual guide operations.
 */
@Dao
interface RitualGuideDao {

    @Query("SELECT * FROM ritual_guides WHERE festival = :festival AND year = :year")
    fun getByFestivalAndYear(festival: String, year: Int): Flow<List<RitualGuideEntity>>

    @Query("SELECT * FROM ritual_guides WHERE tithi_id = :tithiId")
    suspend fun getByTithiId(tithiId: String): RitualGuideEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(guides: List<RitualGuideEntity>)
}
