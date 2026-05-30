package com.example.hopper.data.repository

import com.example.hopper.data.local.db.dao.VolunteerDao
import com.example.hopper.data.local.db.entity.VolunteerPostEntity
import com.example.hopper.data.local.db.entity.VolunteerSignupEntity
import com.example.hopper.domain.repository.VolunteerRepository
import kotlinx.coroutines.flow.Flow
import java.time.Clock
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VolunteerRepositoryImpl @Inject constructor(
    private val volunteerDao: VolunteerDao,
    private val clock: Clock
) : VolunteerRepository {

    override fun getActivePosts(festival: String, year: Int): Flow<List<VolunteerPostEntity>> =
        volunteerDao.getActivePosts(festival, year, clock.millis())

    override suspend fun getById(id: String): VolunteerPostEntity? =
        volunteerDao.getById(id)

    override suspend fun signUp(postId: String, displayName: String, encryptedContact: String): Result<Unit> {
        val post = volunteerDao.getById(postId) ?: return Result.failure(Exception("Post not found"))
        if (post.volunteersSignedUp >= post.volunteersNeeded) {
            return Result.failure(Exception("Post is already filled"))
        }

        val signup = VolunteerSignupEntity(
            id = UUID.randomUUID().toString(),
            postId = postId,
            displayName = displayName,
            encryptedContact = encryptedContact,
            signedUpAtEpochMs = clock.millis()
        )
        volunteerDao.insertSignup(signup)
        volunteerDao.incrementSignupCount(postId)
        return Result.success(Unit)
    }

    override suspend fun isPostFilled(postId: String): Boolean {
        val post = volunteerDao.getById(postId) ?: return true
        return post.volunteersSignedUp >= post.volunteersNeeded
    }

    override suspend fun getSignupsForPost(postId: String): List<VolunteerSignupEntity> =
        volunteerDao.getSignupsForPost(postId)

    override suspend fun insertAll(posts: List<VolunteerPostEntity>) =
        volunteerDao.insertAll(posts)

    override suspend fun deleteExpired() =
        volunteerDao.deleteExpired(clock.millis())
}
