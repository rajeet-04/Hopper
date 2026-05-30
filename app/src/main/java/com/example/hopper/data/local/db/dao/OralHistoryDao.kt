package com.example.hopper.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hopper.data.local.db.entity.OralHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for oral history operations.
 */
@Dao
interface OralHistoryDao {

    @Query("SELECT * FROM oral_histories WHERE pandal_id = :pandalId")
    fun getByPandalId(pandalId: String): Flow<List<OralHistoryEntity>>

    @Query("SELECT * FROM oral_histories")
    fun getAll(): Flow<List<OralHistoryEntity>>

    @Query("SELECT * FROM oral_histories WHERE id = :id")
    suspend fun getById(id: String): OralHistoryEntity?

    @Query("UPDATE oral_histories SET local_audio_path = :path, is_audio_cached_locally = :isCached WHERE id = :id")
    suspend fun updateLocalAudioPath(id: String, path: String, isCached: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(histories: List<OralHistoryEntity>)
}
