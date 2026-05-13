package com.aether.aodaod.data.repository

import com.aether.aodaod.data.local.preferences.AODPreferences
import com.aether.aodaod.domain.model.AODSettings
import com.aether.aodaod.domain.model.BatterySettings
import com.aether.aodaod.domain.model.BurnInProtection
import com.aether.aodaod.domain.model.ClockConfig
import com.aether.aodaod.domain.model.EdgeLightingConfig
import com.aether.aodaod.domain.model.NotificationSettings
import com.aether.aodaod.domain.repository.AODSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AODSettingsRepository using DataStore
 */
@Singleton
class AODSettingsRepositoryImpl @Inject constructor(
    private val preferences: AODPreferences
) : AODSettingsRepository {
    
    override fun getSettings(): Flow<AODSettings> = preferences.getSettings()
    
    override suspend fun getSettingsOnce(): AODSettings = preferences.getSettingsOnce()
    
    override suspend fun saveSettings(settings: AODSettings) {
        preferences.saveSettings(settings)
    }
    
    override suspend fun updateClockConfig(config: ClockConfig) {
        val current = getSettingsOnce()
        saveSettings(current.copy(clockConfig = config, lastUpdated = System.currentTimeMillis()))
    }
    
    override suspend fun updateTheme(theme: com.aether.aodaod.domain.model.AODTheme?) {
        val current = getSettingsOnce()
        saveSettings(current.copy(theme = theme, lastUpdated = System.currentTimeMillis()))
    }
    
    override suspend fun updateSchedule(schedule: com.aether.aodaod.domain.model.AODSchedule) {
        val current = getSettingsOnce()
        saveSettings(current.copy(schedule = schedule, lastUpdated = System.currentTimeMillis()))
    }
    
    override suspend fun updateEdgeLighting(config: EdgeLightingConfig) {
        val current = getSettingsOnce()
        saveSettings(current.copy(edgeLighting = config, lastUpdated = System.currentTimeMillis()))
    }
    
    override suspend fun updateBurnInProtection(protection: BurnInProtection) {
        val current = getSettingsOnce()
        saveSettings(current.copy(burnInProtection = protection, lastUpdated = System.currentTimeMillis()))
    }
    
    override suspend fun updateBatterySettings(settings: BatterySettings) {
        val current = getSettingsOnce()
        saveSettings(current.copy(batterySettings = settings, lastUpdated = System.currentTimeMillis()))
    }
    
    override suspend fun updateNotificationSettings(settings: NotificationSettings) {
        val current = getSettingsOnce()
        saveSettings(current.copy(notificationSettings = settings, lastUpdated = System.currentTimeMillis()))
    }
    
    override suspend fun setAODEnabled(enabled: Boolean) {
        val current = getSettingsOnce()
        saveSettings(current.copy(isAODEnabled = enabled, lastUpdated = System.currentTimeMillis()))
    }
    
    override suspend fun setBrightness(level: Float) {
        val current = getSettingsOnce()
        saveSettings(current.copy(brightness = level.coerceIn(0f, 1f), lastUpdated = System.currentTimeMillis()))
    }
    
    override suspend fun resetToDefaults() {
        saveSettings(AODSettings())
    }
    
    override fun isOnboardingCompleted(): Flow<Boolean> = preferences.isOnboardingCompleted()
    
    override suspend fun setOnboardingCompleted(completed: Boolean) {
        preferences.setOnboardingCompleted(completed)
    }
}
