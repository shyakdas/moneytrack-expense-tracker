// Copyright (c) 2026 shyakdas

package com.moneytrack

import android.os.Bundle
import android.graphics.Color
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import com.moneytrack.applock.AppLockViewModel
import com.moneytrack.navigation.AppNavHost
import com.moneytrack.startup.AppEntryViewModel
import ui.theme.MoneyTrackTheme

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val appEntryViewModel: AppEntryViewModel by viewModels()
    private val appLockViewModel: AppLockViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            appEntryViewModel.uiState.value.isLoading
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT,
            ),
        )
        setContent {
            val uiState = appEntryViewModel.uiState.collectAsStateWithLifecycle().value
            val appLockUiState = appLockViewModel.uiState.collectAsStateWithLifecycle().value
            MoneyTrackTheme {
                if (!uiState.isLoading) {
                    AppNavHost(
                        startDestination = uiState.startDestination,
                        forcePinAuth = appLockUiState.forcePinAuth,
                        onForcePinAuthHandled = appLockViewModel::onForcePinAuthHandled,
                        onPinAuthenticated = appLockViewModel::onPinAuthenticated,
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        appLockViewModel.onAppForegrounded()
    }

    override fun onStop() {
        appLockViewModel.onAppBackgrounded(isChangingConfigurations = isChangingConfigurations)
        super.onStop()
    }
}
