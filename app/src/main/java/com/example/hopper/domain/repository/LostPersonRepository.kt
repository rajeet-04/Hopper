package com.example.hopper.domain.repository

import com.example.hopper.data.local.db.entity.LostPersonPostEntity
import com.example.hopper.domain.model.LatLng
import kotlinx.coroutines.flow.Flow

interface LostPersonRepository {
    fun getActivePosts(): Flow<List<LostPersonPostEntity>>
    fun getPostsWithinRadius(center: LatLng, radiusMeters: Double = 2000.0): Flow<List<LostPersonPostEntity>>
    suspend fun submitPost(post: LostPersonPostEntity)
    suspend fun resolvePost(id: String)
    suspend fun getUnsyncedPosts(): List<LostPersonPostEntity>
    suspend fun markSynced(id: String)
    suspend fun deleteExpired()
}
