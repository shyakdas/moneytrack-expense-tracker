package com.moneytrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moneytrack.onboarding.presentation.OnboardingRoute
import com.moneytrack.pinsetup.presentation.PinSetupRoute

@Composable
fun AppNavHost(
    startDestination: String,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(AppDestination.Onboarding.route) {
            OnboardingRoute(
                onCompleted = {
                    navController.navigate(AppDestination.PinSetup.route) {
                        popUpTo(AppDestination.Onboarding.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(AppDestination.PinSetup.route) {
            PinSetupRoute(
                onCompleted = {
                    navController.navigate(AppDestination.Home.route) {
                        popUpTo(AppDestination.PinSetup.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(AppDestination.Home.route) {
            HomePlaceholderScreen()
        }
    }
}
