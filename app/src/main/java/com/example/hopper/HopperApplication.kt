package com.example.hopper

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Hopper (Festival Atlas).
 * Annotated with @HiltAndroidApp to trigger Hilt's code generation
 * and serve as the application-level dependency container.
 */
@HiltAndroidApp
class HopperApplication : Application()
