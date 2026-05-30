package com.example.hopper.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hopper.data.local.db.entity.ItineraryEntity
import com.example.hopper.data.local.db.entity.ItineraryStopEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for itinerary operations.
 */
@Dao
interface ItineraryDao {

    @Query("SELECT * FROM itineraries ORDER BY created_at_epoch_ms DESC")
    fun getAll(): Flow<List<ItineraryEntity>>

    @Query("SELECT * FROM itineraries WHERE id = :id")
    suspend fun getById(id: String): ItineraryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(itinerary: ItineraryEntity)

    @Query("DELETE FROM itineraries WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM itinerary_stops WHERE itinerary_id = :itineraryId ORDER BY sequence_order ASC")
    suspend fun getStopsForItinerary(itineraryId: String): List<ItineraryStopEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStops(stops: List<ItineraryStopEntity>)

    @Query("DELETE FROM itinerary_stops WHERE itinerary_id = :itineraryId")
    suspend fun deleteStopsForItinerary(itineraryId: String)
}
