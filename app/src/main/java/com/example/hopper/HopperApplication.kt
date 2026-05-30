package com.example.hopper

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.hopper.data.local.CleanupScheduler
import com.example.hopper.data.remote.sync.WorkScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

/**
 * Application class for Hopper (Festival Atlas).
 * Annotated with @HiltAndroidApp to trigger Hilt's code generation
 * and serve as the application-level dependency container.
 *
 * Implements [Configuration.Provider] so that Hilt's [HiltWorkerFactory]
 * can supply dependencies to @HiltWorker workers. The default WorkManager
 * initializer is disabled in the manifest because we provide a custom config.
 */
@HiltAndroidApp
class HopperApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var workScheduler: WorkScheduler
    @Inject lateinit var cleanupScheduler: CleanupScheduler

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        workScheduler.scheduleAll()
        cleanupScheduler.runCleanup(appScope)
    }
}
