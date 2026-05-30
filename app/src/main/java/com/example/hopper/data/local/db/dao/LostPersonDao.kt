package com.example.hopper.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hopper.data.local.db.entity.LostPersonPostEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for lost person post operations.
 */
@Dao
interface LostPersonDao {

    @Query("SELECT * FROM lost_person_posts WHERE expires_at_epoch_ms > :currentTimeMs AND is_resolved = 0")
    fun getActivePosts(currentTimeMs: Long): Flow<List<LostPersonPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: LostPersonPostEntity)

    @Query("UPDATE lost_person_posts SET is_resolved = 1 WHERE id = :id")
    suspend fun resolve(id: String)

    @Query("SELECT * FROM lost_person_posts WHERE is_synced = 0")
    suspend fun getUnsyncedPosts(): List<LostPersonPostEntity>

    @Query("UPDATE lost_person_posts SET is_synced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM lost_person_posts WHERE expires_at_epoch_ms <= :currentTimeMs")
    suspend fun deleteExpired(currentTimeMs: Long)
}
