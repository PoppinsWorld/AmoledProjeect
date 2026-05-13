package com.aether.aodaod.domain.repository

import com.aether.aodaod.domain.model.AODSettings
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for AOD settings
 * Defines the contract for data operations
 */
interface AODSettingsRepository {
    
    /**
     * Get current AOD settings as a Flow
     */
    fun getSettings(): Flow<AODSettings>
    
    /**
     * Get current AOD settings once
     */
    suspend fun getSettingsOnce(): AODSettings
    
    /**
     * Save AOD settings
     */
    suspend fun saveSettings(settings: AODSettings)
    
    /**
     * Update clock configuration
     */
    suspend fun updateClockConfig(config: com.aether.aodaod.domain.model.ClockConfig)
    
    /**
     * Update theme
     */
    suspend fun updateTheme(theme: com.aether.aodaod.domain.model.AODTheme?)
    
    /**
     * Update schedule
     */
    suspend fun updateSchedule(schedule: com.aether.aodaod.domain.model.AODSchedule)
    
    /**
     * Update edge lighting config
     */
    suspend fun updateEdgeLighting(config: com.aether.aodaod.domain.model.EdgeLightingConfig)
    
    /**
     * Update burn-in protection settings
     */
    suspend fun updateBurnInProtection(protection: com.aether.aodaod.domain.model.BurnInProtection)
    
    /**
     * Update battery settings
     */
    suspend fun updateBatterySettings(settings: com.aether.aodaod.domain.model.BatterySettings)
    
    /**
     * Update notification settings
     */
    suspend fun updateNotificationSettings(settings: com.aether.aodaod.domain.model.NotificationSettings)
    
    /**
     * Set AOD enabled state
     */
    suspend fun setAODEnabled(enabled: Boolean)
    
    /**
     * Set brightness level
     */
    suspend fun setBrightness(level: Float)
    
    /**
     * Reset to default settings
     */
    suspend fun resetToDefaults()
    
    /**
     * Check if onboarding has been completed
     */
    fun isOnboardingCompleted(): Flow<Boolean>
    
    /**
     * Mark onboarding as completed
     */
    suspend fun setOnboardingCompleted(completed: Boolean)
}
