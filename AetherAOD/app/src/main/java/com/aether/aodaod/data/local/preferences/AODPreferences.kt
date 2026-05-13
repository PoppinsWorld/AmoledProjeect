package com.aether.aodaod.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aether.aodaod.domain.model.AODSchedule
import com.aether.aodaod.domain.model.AODSettings
import com.aether.aodaod.domain.model.BatterySettings
import com.aether.aodaod.domain.model.BurnInProtection
import com.aether.aodaod.domain.model.ClockConfig
import com.aether.aodaod.domain.model.ClockStyle
import com.aether.aodaod.domain.model.EdgeLightingConfig
import com.aether.aodaod.domain.model.EdgeLightStyle
import com.aether.aodaod.domain.model.NotificationSettings
import com.aether.aodaod.domain.model.RefreshRate
import com.aether.aodaod.domain.model.ScheduleMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aod_settings")

/**
 * DataStore-based preferences manager for AOD settings
 */
@Singleton
class AODPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    // Preference keys
    companion object {
        val CLOCK_STYLE = stringPreferencesKey("clock_style")
        val CLOCK_PRIMARY_COLOR = intPreferencesKey("clock_primary_color")
        val CLOCK_SECONDARY_COLOR = intPreferencesKey("clock_secondary_color")
        val CLOCK_FONT_FAMILY = stringPreferencesKey("clock_font_family")
        val CLOCK_24_HOUR = booleanPreferencesKey("clock_24_hour")
        val CLOCK_SHOW_SECONDS = booleanPreferencesKey("clock_show_seconds")
        val CLOCK_SHOW_DATE = booleanPreferencesKey("clock_show_date")
        val CLOCK_SHOW_BATTERY = booleanPreferencesKey("clock_show_battery")
        val CLOCK_SCALE = floatPreferencesKey("clock_scale")
        val CLOCK_OFFSET_X = floatPreferencesKey("clock_offset_x")
        val CLOCK_OFFSET_Y = floatPreferencesKey("clock_offset_y")
        val CLOCK_ROTATION = floatPreferencesKey("clock_rotation")
        val CLOCK_OPACITY = floatPreferencesKey("clock_opacity")
        val CLOCK_ANIMATION_ENABLED = booleanPreferencesKey("clock_animation_enabled")
        val CLOCK_ANIMATION_SPEED = floatPreferencesKey("clock_animation_speed")
        
        val THEME_ID = stringPreferencesKey("theme_id")
        val THEME_NAME = stringPreferencesKey("theme_name")
        val THEME_BG_COLOR = intPreferencesKey("theme_bg_color")
        val THEME_ACCENT_COLOR = intPreferencesKey("theme_accent_color")
        val THEME_GRADIENT_ENABLED = booleanPreferencesKey("theme_gradient_enabled")
        val THEME_GRADIENT_START = intPreferencesKey("theme_gradient_start")
        val THEME_GRADIENT_END = intPreferencesKey("theme_gradient_end")
        val THEME_BLUR_RADIUS = intPreferencesKey("theme_blur_radius")
        val THEME_IS_DARK = booleanPreferencesKey("theme_is_dark")
        
        val SCHEDULE_ENABLED = booleanPreferencesKey("schedule_enabled")
        val SCHEDULE_MODE = stringPreferencesKey("schedule_mode")
        val SCHEDULE_START_TIME = intPreferencesKey("schedule_start_time")
        val SCHEDULE_END_TIME = intPreferencesKey("schedule_end_time")
        
        val EDGE_LIGHT_ENABLED = booleanPreferencesKey("edge_light_enabled")
        val EDGE_LIGHT_COLOR = intPreferencesKey("edge_light_color")
        val EDGE_LIGHT_STYLE = stringPreferencesKey("edge_light_style")
        val EDGE_LIGHT_DURATION = intPreferencesKey("edge_light_duration")
        val EDGE_LIGHT_BRIGHTNESS = floatPreferencesKey("edge_light_brightness")
        val EDGE_LIGHT_TRIGGER_NOTIFICATION = booleanPreferencesKey("edge_light_trigger_notification")
        val EDGE_LIGHT_TRIGGER_CHARGING = booleanPreferencesKey("edge_light_trigger_charging")
        val EDGE_LIGHT_TRIGGER_TAP = booleanPreferencesKey("edge_light_trigger_tap")
        
        val BURN_IN_ENABLED = booleanPreferencesKey("burn_in_enabled")
        val BURN_IN_SHIFT_INTERVAL = intPreferencesKey("burn_in_shift_interval")
        val BURN_IN_MAX_SHIFT = intPreferencesKey("burn_in_max_shift")
        val BURN_IN_DIM_AFTER = intPreferencesKey("burn_in_dim_after")
        val BURN_IN_DIM_BRIGHTNESS = floatPreferencesKey("burn_in_dim_brightness")
        
        val BATTERY_EFFICIENCY = booleanPreferencesKey("battery_efficiency")
        val BATTERY_DISABLE_LOW = booleanPreferencesKey("battery_disable_low")
        val BATTERY_LOW_THRESHOLD = intPreferencesKey("battery_low_threshold")
        val BATTERY_DISABLE_SCREEN_ON = booleanPreferencesKey("battery_disable_screen_on")
        val BATTERY_REFRESH_RATE = stringPreferencesKey("battery_refresh_rate")
        
        val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
        val NOTIFICATION_MAX = intPreferencesKey("notification_max")
        val NOTIFICATION_SHOW_ICON = booleanPreferencesKey("notification_show_icon")
        val NOTIFICATION_SHOW_APP_NAME = booleanPreferencesKey("notification_show_app_name")
        val NOTIFICATION_SHOW_TIMESTAMP = booleanPreferencesKey("notification_show_timestamp")
        val NOTIFICATION_DISMISS_ON_TAP = booleanPreferencesKey("notification_dismiss_on_tap")
        val NOTIFICATION_PRIORITY_ONLY = booleanPreferencesKey("notification_priority_only")
        
        val BRIGHTNESS = floatPreferencesKey("brightness")
        val AOD_ENABLED = booleanPreferencesKey("aod_enabled")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }
    
    /**
     * Get all settings as a Flow
     */
    fun getSettings(): Flow<AODSettings> = context.dataStore.data.map { preferences ->
        AODSettings(
            clockConfig = ClockConfig(
                style = ClockStyle.fromId(preferences[CLOCK_STYLE] ?: ClockStyle.MINIMAL_DIGITAL.id),
                primaryColor = preferences[CLOCK_PRIMARY_COLOR] ?: -1,
                secondaryColor = preferences[CLOCK_SECONDARY_COLOR],
                fontFamily = preferences[CLOCK_FONT_FAMILY] ?: "default",
                is24HourFormat = preferences[CLOCK_24_HOUR] ?: true,
                showSeconds = preferences[CLOCK_SHOW_SECONDS] ?: false,
                showDate = preferences[CLOCK_SHOW_DATE] ?: true,
                showBattery = preferences[CLOCK_SHOW_BATTERY] ?: true,
                scale = preferences[CLOCK_SCALE] ?: 1.0f,
                offsetX = preferences[CLOCK_OFFSET_X] ?: 0f,
                offsetY = preferences[CLOCK_OFFSET_Y] ?: 0f,
                rotation = preferences[CLOCK_ROTATION] ?: 0f,
                opacity = preferences[CLOCK_OPACITY] ?: 1.0f,
                enableAnimation = preferences[CLOCK_ANIMATION_ENABLED] ?: true,
                animationSpeed = preferences[CLOCK_ANIMATION_SPEED] ?: 1.0f
            ),
            theme = preferences[THEME_ID]?.let {
                com.aether.aodaod.domain.model.AODTheme(
                    id = it,
                    name = preferences[THEME_NAME] ?: "",
                    backgroundColor = preferences[THEME_BG_COLOR] ?: -16777216,
                    clockColors = emptyList(),
                    accentColor = preferences[THEME_ACCENT_COLOR] ?: -1048576,
                    gradientEnabled = preferences[THEME_GRADIENT_ENABLED] ?: false,
                    gradientStart = preferences[THEME_GRADIENT_START],
                    gradientEnd = preferences[THEME_GRADIENT_END],
                    blurRadius = preferences[THEME_BLUR_RADIUS] ?: 0,
                    isDark = preferences[THEME_IS_DARK] ?: true
                )
            },
            schedule = AODSchedule(
                isEnabled = preferences[SCHEDULE_ENABLED] ?: true,
                mode = ScheduleMode.valueOf(preferences[SCHEDULE_MODE] ?: ScheduleMode.ALL_DAY.name),
                startTime = preferences[SCHEDULE_START_TIME] ?: 0,
                endTime = preferences[SCHEDULE_END_TIME] ?: 0
            ),
            edgeLighting = EdgeLightingConfig(
                isEnabled = preferences[EDGE_LIGHT_ENABLED] ?: true,
                color = preferences[EDGE_LIGHT_COLOR] ?: -1048576,
                style = EdgeLightStyle.valueOf(preferences[EDGE_LIGHT_STYLE] ?: EdgeLightStyle.BASIC.name),
                duration = preferences[EDGE_LIGHT_DURATION] ?: 3000,
                brightness = preferences[EDGE_LIGHT_BRIGHTNESS] ?: 0.8f,
                triggerOnNotification = preferences[EDGE_LIGHT_TRIGGER_NOTIFICATION] ?: true,
                triggerOnCharging = preferences[EDGE_LIGHT_TRIGGER_CHARGING] ?: true,
                triggerOnTap = preferences[EDGE_LIGHT_TRIGGER_TAP] ?: false
            ),
            burnInProtection = BurnInProtection(
                isEnabled = preferences[BURN_IN_ENABLED] ?: true,
                shiftInterval = preferences[BURN_IN_SHIFT_INTERVAL] ?: 300000,
                maxShiftPixels = preferences[BURN_IN_MAX_SHIFT] ?: 5,
                dimAfterMinutes = preferences[BURN_IN_DIM_AFTER] ?: 10,
                dimBrightness = preferences[BURN_IN_DIM_BRIGHTNESS] ?: 0.5f
            ),
            batterySettings = BatterySettings(
                efficiencyMode = preferences[BATTERY_EFFICIENCY] ?: true,
                disableOnLowBattery = preferences[BATTERY_DISABLE_LOW] ?: true,
                lowBatteryThreshold = preferences[BATTERY_LOW_THRESHOLD] ?: 15,
                disableOnScreenOn = preferences[BATTERY_DISABLE_SCREEN_ON] ?: true,
                refreshRate = RefreshRate.valueOf(preferences[BATTERY_REFRESH_RATE] ?: RefreshRate.AUTO.name)
            ),
            notificationSettings = NotificationSettings(
                isEnabled = preferences[NOTIFICATION_ENABLED] ?: true,
                maxNotifications = preferences[NOTIFICATION_MAX] ?: 5,
                showAppIcon = preferences[NOTIFICATION_SHOW_ICON] ?: true,
                showAppName = preferences[NOTIFICATION_SHOW_APP_NAME] ?: true,
                showTimestamp = preferences[NOTIFICATION_SHOW_TIMESTAMP] ?: true,
                dismissOnTap = preferences[NOTIFICATION_DISMISS_ON_TAP] ?: true,
                priorityAppsOnly = preferences[NOTIFICATION_PRIORITY_ONLY] ?: false
            ),
            brightness = preferences[BRIGHTNESS] ?: 0.7f,
            isAODEnabled = preferences[AOD_ENABLED] ?: false
        )
    }
    
    /**
     * Get settings once (non-flow)
     */
    suspend fun getSettingsOnce(): AODSettings = getSettings().first()
    
    /**
     * Save complete settings
     */
    suspend fun saveSettings(settings: AODSettings) {
        context.dataStore.edit { prefs ->
            // Clock config
            prefs[CLOCK_STYLE] = settings.clockConfig.style.id
            prefs[CLOCK_PRIMARY_COLOR] = settings.clockConfig.primaryColor
            settings.clockConfig.secondaryColor?.let { prefs[CLOCK_SECONDARY_COLOR] = it }
            prefs[CLOCK_FONT_FAMILY] = settings.clockConfig.fontFamily
            prefs[CLOCK_24_HOUR] = settings.clockConfig.is24HourFormat
            prefs[CLOCK_SHOW_SECONDS] = settings.clockConfig.showSeconds
            prefs[CLOCK_SHOW_DATE] = settings.clockConfig.showDate
            prefs[CLOCK_SHOW_BATTERY] = settings.clockConfig.showBattery
            prefs[CLOCK_SCALE] = settings.clockConfig.scale
            prefs[CLOCK_OFFSET_X] = settings.clockConfig.offsetX
            prefs[CLOCK_OFFSET_Y] = settings.clockConfig.offsetY
            prefs[CLOCK_ROTATION] = settings.clockConfig.rotation
            prefs[CLOCK_OPACITY] = settings.clockConfig.opacity
            prefs[CLOCK_ANIMATION_ENABLED] = settings.clockConfig.enableAnimation
            prefs[CLOCK_ANIMATION_SPEED] = settings.clockConfig.animationSpeed
            
            // Theme
            settings.theme?.let { theme ->
                prefs[THEME_ID] = theme.id
                prefs[THEME_NAME] = theme.name
                prefs[THEME_BG_COLOR] = theme.backgroundColor
                prefs[THEME_ACCENT_COLOR] = theme.accentColor
                prefs[THEME_GRADIENT_ENABLED] = theme.gradientEnabled
                theme.gradientStart?.let { prefs[THEME_GRADIENT_START] = it }
                theme.gradientEnd?.let { prefs[THEME_GRADIENT_END] = it }
                prefs[THEME_BLUR_RADIUS] = theme.blurRadius
                prefs[THEME_IS_DARK] = theme.isDark
            }
            
            // Schedule
            prefs[SCHEDULE_ENABLED] = settings.schedule.isEnabled
            prefs[SCHEDULE_MODE] = settings.schedule.mode.name
            prefs[SCHEDULE_START_TIME] = settings.schedule.startTime
            prefs[SCHEDULE_END_TIME] = settings.schedule.endTime
            
            // Edge lighting
            prefs[EDGE_LIGHT_ENABLED] = settings.edgeLighting.isEnabled
            prefs[EDGE_LIGHT_COLOR] = settings.edgeLighting.color
            prefs[EDGE_LIGHT_STYLE] = settings.edgeLighting.style.name
            prefs[EDGE_LIGHT_DURATION] = settings.edgeLighting.duration
            prefs[EDGE_LIGHT_BRIGHTNESS] = settings.edgeLighting.brightness
            prefs[EDGE_LIGHT_TRIGGER_NOTIFICATION] = settings.edgeLighting.triggerOnNotification
            prefs[EDGE_LIGHT_TRIGGER_CHARGING] = settings.edgeLighting.triggerOnCharging
            prefs[EDGE_LIGHT_TRIGGER_TAP] = settings.edgeLighting.triggerOnTap
            
            // Burn-in protection
            prefs[BURN_IN_ENABLED] = settings.burnInProtection.isEnabled
            prefs[BURN_IN_SHIFT_INTERVAL] = settings.burnInProtection.shiftInterval
            prefs[BURN_IN_MAX_SHIFT] = settings.burnInProtection.maxShiftPixels
            prefs[BURN_IN_DIM_AFTER] = settings.burnInProtection.dimAfterMinutes
            prefs[BURN_IN_DIM_BRIGHTNESS] = settings.burnInProtection.dimBrightness
            
            // Battery settings
            prefs[BATTERY_EFFICIENCY] = settings.batterySettings.efficiencyMode
            prefs[BATTERY_DISABLE_LOW] = settings.batterySettings.disableOnLowBattery
            prefs[BATTERY_LOW_THRESHOLD] = settings.batterySettings.lowBatteryThreshold
            prefs[BATTERY_DISABLE_SCREEN_ON] = settings.batterySettings.disableOnScreenOn
            prefs[BATTERY_REFRESH_RATE] = settings.batterySettings.refreshRate.name
            
            // Notification settings
            prefs[NOTIFICATION_ENABLED] = settings.notificationSettings.isEnabled
            prefs[NOTIFICATION_MAX] = settings.notificationSettings.maxNotifications
            prefs[NOTIFICATION_SHOW_ICON] = settings.notificationSettings.showAppIcon
            prefs[NOTIFICATION_SHOW_APP_NAME] = settings.notificationSettings.showAppName
            prefs[NOTIFICATION_SHOW_TIMESTAMP] = settings.notificationSettings.showTimestamp
            prefs[NOTIFICATION_DISMISS_ON_TAP] = settings.notificationSettings.dismissOnTap
            prefs[NOTIFICATION_PRIORITY_ONLY] = settings.notificationSettings.priorityAppsOnly
            
            // General
            prefs[BRIGHTNESS] = settings.brightness
            prefs[AOD_ENABLED] = settings.isAODEnabled
        }
    }
    
    /**
     * Check if onboarding completed
     */
    fun isOnboardingCompleted(): Flow<Boolean> = 
        context.dataStore.data.map { it[ONBOARDING_COMPLETED] ?: false }
    
    /**
     * Set onboarding completed status
     */
    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[ONBOARDING_COMPLETED] = completed }
    }
}
