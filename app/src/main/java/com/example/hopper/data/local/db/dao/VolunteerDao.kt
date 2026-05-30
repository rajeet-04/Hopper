package com.example.hopper.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hopper.data.local.db.entity.VolunteerPostEntity
import com.example.hopper.data.local.db.entity.VolunteerSignupEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for volunteer post and signup operations.
 */
@Dao
interface VolunteerDao {

    @Query("SELECT * FROM volunteer_posts WHERE festival = :festival AND year = :year AND time_slot_end > :currentTimeMs AND is_filled = 0")
    fun getActivePosts(festival: String, year: Int, currentTimeMs: Long): Flow<List<VolunteerPostEntity>>

    @Query("SELECT * FROM volunteer_posts WHERE id = :id")
    suspend fun getById(id: String): VolunteerPostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<VolunteerPostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignup(signup: VolunteerSignupEntity)

    @Query("SELECT * FROM volunteer_signups WHERE post_id = :postId")
    suspend fun getSignupsForPost(postId: String): List<VolunteerSignupEntity>

    @Query("UPDATE volunteer_posts SET volunteers_signed_up = volunteers_signed_up + 1 WHERE id = :postId")
    suspend fun incrementSignupCount(postId: String)

    @Query("DELETE FROM volunteer_posts WHERE time_slot_end <= :currentTimeMs")
    suspend fun deleteExpired(currentTimeMs: Long)
}
