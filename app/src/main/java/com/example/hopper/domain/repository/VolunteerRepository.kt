package com.example.hopper.domain.repository

import com.example.hopper.data.local.db.entity.VolunteerPostEntity
import com.example.hopper.data.local.db.entity.VolunteerSignupEntity
import kotlinx.coroutines.flow.Flow

interface VolunteerRepository {
    fun getActivePosts(festival: String, year: Int): Flow<List<VolunteerPostEntity>>
    suspend fun getById(id: String): VolunteerPostEntity?
    suspend fun signUp(postId: String, displayName: String, encryptedContact: String): Result<Unit>
    suspend fun isPostFilled(postId: String): Boolean
    suspend fun getSignupsForPost(postId: String): List<VolunteerSignupEntity>
    suspend fun insertAll(posts: List<VolunteerPostEntity>)
    suspend fun deleteExpired()
}
