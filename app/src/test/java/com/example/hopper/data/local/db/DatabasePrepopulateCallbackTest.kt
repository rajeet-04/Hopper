package com.example.hopper.data.local.db

import android.content.SharedPreferences
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.hopper.data.local.assets.GeoJsonAssetLoader
import com.example.hopper.data.local.db.dao.CalendarDao
import com.example.hopper.data.local.db.dao.ConnectorDao
import com.example.hopper.data.local.db.dao.ExitNodeDao
import com.example.hopper.data.local.db.dao.HistoricalCrowdPatternDao
import com.example.hopper.data.local.db.dao.PandalDao
import com.example.hopper.data.local.db.entity.ConnectorEntity
import com.example.hopper.data.local.db.entity.ExitNodeEntity
import com.example.hopper.data.local.db.entity.HistoricalCrowdPatternEntity
import com.example.hopper.data.local.db.entity.PandalEntity
import com.example.hopper.data.local.db.entity.TithiEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import javax.inject.Provider

@OptIn(ExperimentalCoroutinesApi::class)
class DatabasePrepopulateCallbackTest {

    private lateinit var databaseProvider: Provider<HopperDatabase>
    private lateinit var database: HopperDatabase
    private lateinit var assetLoader: GeoJsonAssetLoader
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var sqliteDb: SupportSQLiteDatabase

    private lateinit var pandalDao: PandalDao
    private lateinit var exitNodeDao: ExitNodeDao
    private lateinit var connectorDao: ConnectorDao
    private lateinit var calendarDao: CalendarDao
    private lateinit var historicalCrowdPatternDao: HistoricalCrowdPatternDao

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        database = mockk()
        databaseProvider = mockk()
        assetLoader = mockk()
        sharedPreferences = mockk()
        editor = mockk(relaxed = true)
        sqliteDb = mockk()

        pandalDao = mockk()
        exitNodeDao = mockk()
        connectorDao = mockk()
        calendarDao = mockk()
        historicalCrowdPatternDao = mockk()

        every { databaseProvider.get() } returns database
        every { database.pandalDao() } returns pandalDao
        every { database.exitNodeDao() } returns exitNodeDao
        every { database.connectorDao() } returns connectorDao
        every { database.calendarDao() } returns calendarDao
        every { database.historicalCrowdPatternDao() } returns historicalCrowdPatternDao
        every { sharedPreferences.edit() } returns editor
        every { editor.putBoolean(any(), any()) } returns editor
    }

    @Test
    fun `onOpen skips seeding when flag is already set`() = runTest {
        every { sharedPreferences.getBoolean(DatabasePrepopulateCallback.PREF_DB_PREPOPULATED, false) } returns true

        val callback = DatabasePrepopulateCallback(
            databaseProvider = databaseProvider,
            assetLoader = assetLoader,
            sharedPreferences = sharedPreferences,
            scope = testScope
        )

        callback.onOpen(sqliteDb)

        verify(exactly = 0) { databaseProvider.get() }
    }

    @Test
    fun `onOpen seeds database when flag is not set`() = runTest {
        every { sharedPreferences.getBoolean(DatabasePrepopulateCallback.PREF_DB_PREPOPULATED, false) } returns false

        coEvery { assetLoader.seedDatabase(any(), any(), any(), any(), any()) } coAnswers {
            val insertPandals = firstArg<suspend (List<PandalEntity>) -> Unit>()
            val insertExitNodes = secondArg<suspend (List<ExitNodeEntity>) -> Unit>()
            val insertConnectors = thirdArg<suspend (List<ConnectorEntity>) -> Unit>()
            val insertCalendar = arg<suspend (List<TithiEntity>) -> Unit>(3)
            val insertHistoricalPatterns = arg<suspend (List<HistoricalCrowdPatternEntity>) -> Unit>(4)
            insertPandals(emptyList())
            insertExitNodes(emptyList())
            insertConnectors(emptyList())
            insertCalendar(emptyList())
            insertHistoricalPatterns(emptyList())
        }

        coEvery { pandalDao.insertAll(any()) } returns Unit
        coEvery { exitNodeDao.insertAll(any()) } returns Unit
        coEvery { connectorDao.insertAll(any()) } returns Unit
        coEvery { calendarDao.insertAll(any()) } returns Unit
        coEvery { historicalCrowdPatternDao.insertAll(any()) } returns Unit

        val callback = DatabasePrepopulateCallback(
            databaseProvider = databaseProvider,
            assetLoader = assetLoader,
            sharedPreferences = sharedPreferences,
            scope = testScope
        )

        callback.onOpen(sqliteDb)

        coVerify { assetLoader.seedDatabase(any(), any(), any(), any(), any()) }
        verify { editor.putBoolean(DatabasePrepopulateCallback.PREF_DB_PREPOPULATED, true) }
        verify { editor.apply() }
    }

    @Test
    fun `onOpen does not set flag when seeding throws exception`() = runTest {
        every { sharedPreferences.getBoolean(DatabasePrepopulateCallback.PREF_DB_PREPOPULATED, false) } returns false

        coEvery { assetLoader.seedDatabase(any(), any(), any(), any(), any()) } throws RuntimeException("Asset load failed")

        val callback = DatabasePrepopulateCallback(
            databaseProvider = databaseProvider,
            assetLoader = assetLoader,
            sharedPreferences = sharedPreferences,
            scope = testScope
        )

        callback.onOpen(sqliteDb)

        verify(exactly = 0) { editor.putBoolean(DatabasePrepopulateCallback.PREF_DB_PREPOPULATED, true) }
    }
}
