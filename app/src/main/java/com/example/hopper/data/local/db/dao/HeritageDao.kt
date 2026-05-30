package com.example.hopper.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hopper.data.local.db.entity.HeritagePointEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for heritage point operations.
 */
@Dao
interface HeritageDao {

    @Query("SELECT * FROM heritage_points WHERE festival = :festival")
    fun getByFestival(festival: String): Flow<List<HeritagePointEntity>>

    @Query("SELECT * FROM heritage_points WHERE id = :id")
    suspend fun getById(id: String): HeritagePointEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(points: List<HeritagePointEntity>)

    @Query("DELETE FROM heritage_points")
    suspend fun deleteAll()
}
