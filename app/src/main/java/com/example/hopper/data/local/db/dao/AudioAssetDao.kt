package com.example.hopper.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hopper.data.local.db.entity.AudioAssetEntity

/**
 * Data Access Object for audio asset cache operations.
 */
@Dao
interface AudioAssetDao {

    @Query("SELECT * FROM audio_assets WHERE id = :id")
    suspend fun getById(id: String): AudioAssetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(asset: AudioAssetEntity)

    @Query("SELECT * FROM audio_assets WHERE is_cached = 1")
    suspend fun getCachedAssets(): List<AudioAssetEntity>

    @Query("SELECT COALESCE(SUM(file_size_bytes), 0) FROM audio_assets WHERE is_cached = 1")
    suspend fun getTotalCacheSize(): Long

    @Query("UPDATE audio_assets SET is_cached = 0, local_path = NULL")
    suspend fun clearCache()
}
