package com.aether.aodaod.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.aether.aodaod.ui.navigation.AODNavHost
import com.aether.aodaod.ui.theme.AetherAODTheme
import com.aether.aodaod.ui.theme.AMOLEDBlack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Main Activity for Aether AOD
 * Entry point for the application
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            AetherAODTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AMOLEDBlack,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    AODNavHost()
                }
            }
        }
    }
}
