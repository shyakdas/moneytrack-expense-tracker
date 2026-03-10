// Copyright (c) 2026 shyakdas

package com.moneytrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moneytrack.home.presentation.HomeRoute
import com.moneytrack.onboarding.presentation.OnboardingRoute
import com.moneytrack.pinauth.presentation.PinAuthRoute
import com.moneytrack.pinsetup.presentation.PinSetupRoute

@Composable
fun AppNavHost(
    startDestination: String,
    forcePinAuth: Boolean,
    onForcePinAuthHandled: () -> Unit,
    onPinAuthenticated: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStackEntry?.destination?.route

    LaunchedEffect(forcePinAuth, currentRoute) {
        if (forcePinAuth) {
            if (currentRoute != AppDestination.PinAuth.route) {
                navController.navigate(AppDestination.PinAuth.route) {
                    launchSingleTop = true
                }
            }
            onForcePinAuthHandled()
        }
    }

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

        composable(AppDestination.PinAuth.route) {
            PinAuthRoute(
                onAuthenticated = {
                    onPinAuthenticated()
                    navController.navigate(AppDestination.Home.route) {
                        popUpTo(AppDestination.PinAuth.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(AppDestination.Home.route) {
            HomeRoute()
        }
    }
}
