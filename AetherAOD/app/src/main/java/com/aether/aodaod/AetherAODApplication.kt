package com.aether.aodaod

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main Application class for Aether AOD
 * Initializes Hilt dependency injection
 */
@HiltAndroidApp
class AetherAODApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    
    companion object {
        lateinit var instance: AetherAODApplication
            private set
    }
}
