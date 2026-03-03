package com.moneytrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import com.moneytrack.navigation.AppNavHost
import com.moneytrack.startup.AppEntryViewModel
import ui.theme.MoneyTrackTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val appEntryViewModel: AppEntryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            appEntryViewModel.uiState.value.isLoading
        }
        enableEdgeToEdge()
        setContent {
            val uiState = appEntryViewModel.uiState.collectAsStateWithLifecycle().value
            MoneyTrackTheme {
                if (!uiState.isLoading) {
                    AppNavHost(startDestination = uiState.startDestination)
                }
            }
        }
    }
}
