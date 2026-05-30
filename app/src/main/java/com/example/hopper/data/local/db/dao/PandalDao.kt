package com.example.hopper.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hopper.data.local.db.entity.PandalEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for pandal operations.
 */
@Dao
interface PandalDao {

    @Query("SELECT * FROM pandals WHERE festival = :festival AND year = :year")
    fun getByFestivalAndYear(festival: String, year: Int): Flow<List<PandalEntity>>

    @Query("SELECT * FROM pandals WHERE id = :id")
    suspend fun getById(id: String): PandalEntity?

    @Query("SELECT * FROM pandals")
    fun getAll(): Flow<List<PandalEntity>>

    @Query("SELECT * FROM pandals WHERE name LIKE '%' || :query || '%' OR name_bengali LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<PandalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pandals: List<PandalEntity>)

    @Query("DELETE FROM pandals")
    suspend fun deleteAll()
}
