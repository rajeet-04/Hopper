package com.example.hopper.data.repository

import com.example.hopper.data.local.db.dao.LostPersonDao
import com.example.hopper.data.local.db.entity.LostPersonPostEntity
import com.example.hopper.domain.model.LatLng
import com.example.hopper.domain.repository.LostPersonRepository
import com.example.hopper.util.HaversineCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LostPersonRepositoryImpl @Inject constructor(
    private val lostPersonDao: LostPersonDao,
    private val clock: Clock
) : LostPersonRepository {

    override fun getActivePosts(): Flow<List<LostPersonPostEntity>> =
        lostPersonDao.getActivePosts(clock.millis())

    override fun getPostsWithinRadius(center: LatLng, radiusMeters: Double): Flow<List<LostPersonPostEntity>> {
        return getActivePosts().map { posts ->
            posts.filter { post ->
                val postLocation = LatLng(post.latitude, post.longitude)
                HaversineCalculator.distanceMeters(center, postLocation) <= radiusMeters
            }
        }
    }

    override suspend fun submitPost(post: LostPersonPostEntity) =
        lostPersonDao.insert(post)

    override suspend fun resolvePost(id: String) =
        lostPersonDao.resolve(id)

    override suspend fun getUnsyncedPosts(): List<LostPersonPostEntity> =
        lostPersonDao.getUnsyncedPosts()

    override suspend fun markSynced(id: String) =
        lostPersonDao.markSynced(id)

    override suspend fun deleteExpired() =
        lostPersonDao.deleteExpired(clock.millis())
}
