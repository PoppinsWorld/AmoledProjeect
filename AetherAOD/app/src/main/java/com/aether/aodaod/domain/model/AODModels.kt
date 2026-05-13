package com.aether.aodaod.domain.model

import java.io.Serializable

/**
 * Represents different clock styles available in the app
 */
enum class ClockStyle(val id: String, val displayName: String) : Serializable {
    MINIMAL_DIGITAL("minimal_digital", "Minimal Digital"),
    FUTURISTIC_NEON("futuristic_neon", "Futuristic Neon"),
    NOTHING_OS("nothing_os", "Nothing OS"),
    PIXEL("pixel", "Pixel Inspired"),
    ANALOG_LUXURY("analog_luxury", "Analog Luxury"),
    MATRIX("matrix", "Matrix Cyber"),
    RGB_GAMER("rgb_gamer", "RGB Gamer"),
    ELEGANT_TYPOGRAPHY("elegant_typography", "Elegant Typography"),
    CINEMATIC("cinematic", "Large Cinematic"),
    MONOCHROME("monochrome", "Minimal Monochrome");
    
    companion object {
        fun fromId(id: String): ClockStyle {
            return values().find { it.id == id } ?: MINIMAL_DIGITAL
        }
    }
}

/**
 * Data class representing a clock configuration
 */
data class ClockConfig(
    val style: ClockStyle = ClockStyle.MINIMAL_DIGITAL,
    val primaryColor: Int = -1, // White by default
    val secondaryColor: Int? = null,
    val fontFamily: String = "default",
    val is24HourFormat: Boolean = true,
    val showSeconds: Boolean = false,
    val showDate: Boolean = true,
    val showBattery: Boolean = true,
    val scale: Float = 1.0f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val rotation: Float = 0f,
    val opacity: Float = 1.0f,
    val enableAnimation: Boolean = true,
    val animationSpeed: Float = 1.0f
) : Serializable

/**
 * Theme configuration for the AOD
 */
data class AODTheme(
    val id: String,
    val name: String,
    val backgroundColor: Int = -16777216, // Black
    val clockColors: List<Int>,
    val accentColor: Int,
    val gradientEnabled: Boolean = false,
    val gradientStart: Int? = null,
    val gradientEnd: Int? = null,
    val backgroundImage: String? = null,
    val blurRadius: Int = 0,
    val isDark: Boolean = true
) : Serializable

/**
 * Schedule configuration for AOD activation
 */
data class AODSchedule(
    val isEnabled: Boolean = true,
    val mode: ScheduleMode = ScheduleMode.ALL_DAY,
    val startTime: Int = 0, // Minutes from midnight
    val endTime: Int = 0, // Minutes from midnight
    val sunriseOffset: Int = 0,
    val sunsetOffset: Int = 0
) : Serializable

enum class ScheduleMode {
    ALL_DAY,
    CUSTOM_TIME,
    SUNRISE_SUNSET,
    SMART_ADAPTIVE
}

/**
 * Edge lighting configuration
 */
data class EdgeLightingConfig(
    val isEnabled: Boolean = true,
    val color: Int = -1048576, // Purple
    val style: EdgeLightStyle = EdgeLightStyle.BASIC,
    val duration: Int = 3000, // ms
    val brightness: Float = 0.8f,
    val triggerOnNotification: Boolean = true,
    val triggerOnCharging: Boolean = true,
    val triggerOnTap: Boolean = false
) : Serializable

enum class EdgeLightStyle {
    BASIC,
    GRADIENT,
    PULSE,
    WAVE,
    RAINBOW
}

/**
 * Burn-in protection settings
 */
data class BurnInProtection(
    val isEnabled: Boolean = true,
    val shiftInterval: Int = 300000, // 5 minutes in ms
    val maxShiftPixels: Int = 5,
    val dimAfterMinutes: Int = 10,
    val dimBrightness: Float = 0.5f
) : Serializable

/**
 * Battery and performance settings
 */
data class BatterySettings(
    val efficiencyMode: Boolean = true,
    val disableOnLowBattery: Boolean = true,
    val lowBatteryThreshold: Int = 15,
    val disableOnScreenOn: Boolean = true,
    val refreshRate: RefreshRate = RefreshRate.AUTO
) : Serializable

enum class RefreshRate {
    AUTO,
    ONE_HZ,
    THIRTY_HZ,
    SIXTY_HZ
}

/**
 * Notification preview settings
 */
data class NotificationSettings(
    val isEnabled: Boolean = true,
    val maxNotifications: Int = 5,
    val showAppIcon: Boolean = true,
    val showAppName: Boolean = true,
    val showTimestamp: Boolean = true,
    val dismissOnTap: Boolean = true,
    val priorityAppsOnly: Boolean = false,
    val blockedApps: Set<String> = emptySet()
) : Serializable

/**
 * Complete AOD settings state
 */
data class AODSettings(
    val clockConfig: ClockConfig = ClockConfig(),
    val theme: AODTheme? = null,
    val schedule: AODSchedule = AODSchedule(),
    val edgeLighting: EdgeLightingConfig = EdgeLightingConfig(),
    val burnInProtection: BurnInProtection = BurnInProtection(),
    val batterySettings: BatterySettings = BatterySettings(),
    val notificationSettings: NotificationSettings = NotificationSettings(),
    val brightness: Float = 0.7f,
    val isAODEnabled: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
) : Serializable
