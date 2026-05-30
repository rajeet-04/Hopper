package com.example.hopper.data.local.db

import android.content.SharedPreferences
import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.hopper.data.local.assets.GeoJsonAssetLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider

/**
 * Room database callback that prepopulates the database with bundled asset data on first launch.
 *
 * Uses a SharedPreferences flag as the primary guard to ensure seeding runs only once,
 * even across destructive migrations. The callback fires on every database open but
 * short-circuits immediately if the flag is already set.
 *
 * Uses a Provider<HopperDatabase> to lazily access DAOs after the database is fully initialized,
 * avoiding circular dependency issues with Room's callback mechanism.
 */
class DatabasePrepopulateCallback(
    private val databaseProvider: Provider<HopperDatabase>,
    private val assetLoader: GeoJsonAssetLoader,
    private val sharedPreferences: SharedPreferences,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : RoomDatabase.Callback() {

    companion object {
        private const val TAG = "DatabasePrepopulate"
        internal const val PREF_DB_PREPOPULATED = "db_prepopulated"
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)

        if (sharedPreferences.getBoolean(PREF_DB_PREPOPULATED, false)) {
            Log.d(TAG, "Database already prepopulated, skipping seeding")
            return
        }

        scope.launch {
            try {
                seedDatabase()
                sharedPreferences.edit().putBoolean(PREF_DB_PREPOPULATED, true).apply()
                Log.i(TAG, "Database prepopulation completed successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Database prepopulation failed: ${e.message}", e)
            }
        }
    }

    private suspend fun seedDatabase() {
        val database = databaseProvider.get()

        val pandalDao = database.pandalDao()
        val exitNodeDao = database.exitNodeDao()
        val connectorDao = database.connectorDao()
        val calendarDao = database.calendarDao()
        val historicalCrowdPatternDao = database.historicalCrowdPatternDao()

        assetLoader.seedDatabase(
            insertPandals = { pandals -> pandalDao.insertAll(pandals) },
            insertExitNodes = { nodes -> exitNodeDao.insertAll(nodes) },
            insertConnectors = { connectors -> connectorDao.insertAll(connectors) },
            insertCalendar = { tithis -> calendarDao.insertAll(tithis) },
            insertHistoricalPatterns = { patterns -> historicalCrowdPatternDao.insertAll(patterns) }
        )
    }
}
