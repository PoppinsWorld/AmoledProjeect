package com.aether.aodaod.features.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.LocalLifecycleOwner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.aether.aodaod.ui.theme.AMOLEDBlack
import com.aether.aodaod.ui.theme.AMOLOLightGray
import com.aether.aodaod.ui.theme.AetherPrimary
import com.aether.aodaod.ui.theme.NeonGreen
import com.aether.aodaod.ui.theme.NeonRed

/**
 * Permission item data class
 */
data class PermissionItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isGranted: Boolean,
    val requiresPostNotification: Boolean = false
)

/**
 * ViewModel for managing permission state
 */
@HiltViewModel
class PermissionsViewModel @Inject constructor() : ViewModel() {
    
    private val _permissionState = MutableStateFlow(PermissionState())
    val permissionState: StateFlow<PermissionState> = _permissionState.asStateFlow()
    
    fun refreshPermissions(context: Context) {
        val displayOverOtherApps = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
        
        val notificationAccess = NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)
        
        val batteryOptimizationExempt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true
        }
        
        val foregroundServiceGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // On Android 13+, foreground service is granted when app is installed
            true
        } else {
            true
        }
        
        _permissionState.value = PermissionState(
            displayOverOtherApps = displayOverOtherApps,
            notificationAccess = notificationAccess,
            batteryOptimizationExempt = batteryOptimizationExempt,
            foregroundServiceGranted = foregroundServiceGranted
        )
    }
    
    fun requestNotificationPermission(context: Context) {
        viewModelScope.launch {
            // Open notification listener settings
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
    
    fun requestDisplayOverOtherAppsPermission(context: Context) {
        viewModelScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
    }
    
    fun requestBatteryOptimizationExemption(context: Context) {
        viewModelScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                if (!powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
            }
        }
    }
}

/**
 * Permission state data class
 */
data class PermissionState(
    val displayOverOtherApps: Boolean = false,
    val notificationAccess: Boolean = false,
    val batteryOptimizationExempt: Boolean = false,
    val foregroundServiceGranted: Boolean = true
)

/**
 * Animated status indicator composable
 */
@Composable
fun AnimatedStatusIndicator(isGranted: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "status-pulse")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse-alpha"
    )
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse-scale"
    )
    
    Box(
        modifier = Modifier
            .size(48.dp)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow ring
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    if (isGranted) 
                        Brush.radialGradient(
                            colors = listOf(NeonGreen.copy(alpha = alpha * 0.5f), Color.Transparent)
                        )
                    else 
                        Brush.radialGradient(
                            colors = listOf(NeonRed.copy(alpha = alpha * 0.5f), Color.Transparent)
                        )
                )
        )
        
        // Inner circle
        Surface(
            modifier = Modifier.size(32.dp),
            shape = RoundedCornerShape(50),
            color = if (isGranted) NeonGreen else NeonRed,
            shadowElevation = 8.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Icon(
                    imageVector = if (isGranted) androidx.compose.material.icons.Icons.Default.Check else androidx.compose.material.icons.Icons.Default.Close,
                    contentDescription = if (isGranted) "Granted" else "Not Granted",
                    tint = AMOLEDBlack,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Premium permission card with animations
 */
@Composable
fun PermissionCard(
    permissionItem: PermissionItem,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isHovered by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isHovered) 16.dp else 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = if (permissionItem.isGranted) NeonGreen.copy(alpha = 0.3f) else Color.Transparent,
                spotColor = if (permissionItem.isGranted) NeonGreen.copy(alpha = 0.3f) else Color.Transparent
            )
            .clickable { onActionClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = AMOLOLightGray.copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon container
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = if (permissionItem.isGranted) 
                                    listOf(AetherPrimary.copy(alpha = 0.3f), AetherPrimary.copy(alpha = 0.1f))
                                else 
                                    listOf(Color.Gray.copy(alpha = 0.3f), Color.Gray.copy(alpha = 0.1f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = permissionItem.icon,
                        contentDescription = permissionItem.title,
                        tint = if (permissionItem.isGranted) AetherPrimary else Color.Gray,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Text content
                Column {
                    Text(
                        text = permissionItem.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = permissionItem.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
            
            // Status indicator and arrow
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedStatusIndicator(permissionItem.isGranted)
                
                Spacer(modifier = Modifier.width(8.dp))
                
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Open settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        // Bottom gradient line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = if (permissionItem.isGranted) 
                            listOf(NeonGreen, AetherPrimary, NeonGreen)
                        else 
                            listOf(Color.Gray, Color.DarkGray, Color.Gray)
                    )
                )
        )
    }
}

/**
 * Main Permissions Screen with premium AMOLED Material 3 UI
 */
@Composable
fun PermissionsScreen(
    viewModel: PermissionsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val permissionState by viewModel.permissionState.collectAsState()
    
    // Request notification permission launcher for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.refreshPermissions(context)
    }
    
    // Refresh permissions when screen is shown
    LaunchedEffect(Unit) {
        viewModel.refreshPermissions(context)
    }
    
    // Auto-refresh permission state when returning to the app
    // This uses LifecycleEventObserver to detect when the user returns from settings
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.refreshPermissions(context)
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AMOLEDBlack)
    ) {
        // Subtle background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(AetherPrimary.copy(alpha = 0.08f), Color.Transparent),
                        center = Alignment.TopCenter,
                        radius = 600.dp
                    )
                )
        )
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(
                top = 60.dp,
                bottom = 40.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header section
            item {
                Column {
                    // Back button and title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigateBack() }
                    ) {
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = AMOLOLightGray.copy(alpha = 0.5f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                androidx.compose.material3.Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = "Permissions",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Subtitle
                    Text(
                        text = "Grant necessary permissions for Aether AOD to function properly",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Overall status chip
                    val allGranted = permissionState.displayOverOtherApps && 
                            permissionState.notificationAccess && 
                            permissionState.batteryOptimizationExempt
                    
                    AnimatedVisibility(
                        visible = allGranted,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            modifier = Modifier.wrapContentSize(),
                            shape = RoundedCornerShape(20.dp),
                            color = NeonGreen.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material3.Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = NeonGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "All permissions granted",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = NeonGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
            
            // Display over other apps permission
            item {
                PermissionCard(
                    permissionItem = PermissionItem(
                        id = "display_over_other_apps",
                        title = "Display Over Other Apps",
                        description = "Required to show the always-on display overlay above other applications",
                        icon = androidx.compose.material.icons.Icons.Default.Visibility,
                        isGranted = permissionState.displayOverOtherApps
                    ),
                    onActionClick = {
                        viewModel.requestDisplayOverOtherAppsPermission(context)
                    }
                )
            }
            
            // Notification access permission
            item {
                PermissionCard(
                    permissionItem = PermissionItem(
                        id = "notification_access",
                        title = "Notification Access",
                        description = "Required to display notifications on the always-on display",
                        icon = androidx.compose.material.icons.Icons.Default.Notifications,
                        isGranted = permissionState.notificationAccess,
                        requiresPostNotification = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    ),
                    onActionClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && 
                            !NotificationManagerCompat.areNotificationsEnabled(context)) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.requestNotificationPermission(context)
                        }
                    }
                )
            }
            
            // Battery optimization exemption
            item {
                PermissionCard(
                    permissionItem = PermissionItem(
                        id = "battery_optimization",
                        title = "Battery Optimization Exemption",
                        description = "Prevents the system from killing the service in the background",
                        icon = androidx.compose.material.icons.Icons.Default.BatteryFull,
                        isGranted = permissionState.batteryOptimizationExempt
                    ),
                    onActionClick = {
                        viewModel.requestBatteryOptimizationExemption(context)
                    }
                )
            }
            
            // Foreground service permission (info only, auto-granted)
            item {
                PermissionCard(
                    permissionItem = PermissionItem(
                        id = "foreground_service",
                        title = "Foreground Service",
                        description = "Allows the app to run continuously for always-on display functionality",
                        icon = androidx.compose.material.icons.Icons.Default.PlayCircle,
                        isGranted = permissionState.foregroundServiceGranted
                    ),
                    onActionClick = {
                        // This is auto-granted, just show info
                    }
                )
            }
            
            // Additional info section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = AMOLOLightGray.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.material3.Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Info,
                                contentDescription = "Info",
                                tint = AetherPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Text(
                                text = "Why these permissions?",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "These permissions are essential for Aether AOD to provide a seamless always-on display experience. The app needs to draw over other apps, access notifications to display them, and run as a foreground service to remain active.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.5
                        )
                    }
                }
            }
            
            // Footer spacing
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
