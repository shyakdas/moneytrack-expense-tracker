// Copyright (c) 2026 shyakdas

package com.moneytrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moneytrack.expense.presentation.ExpenseRoute
import com.moneytrack.home.presentation.HomeRoute
import com.moneytrack.onboarding.presentation.OnboardingRoute
import com.moneytrack.pinauth.presentation.PinAuthRoute
import com.moneytrack.pinsetup.presentation.PinSetupRoute
import com.moneytrack.profile.presentation.ProfileRoute
import com.moneytrack.settings.presentation.CurrencyRoute
import com.moneytrack.settings.presentation.SecurityRoute
import com.moneytrack.settings.presentation.SettingsRoute
import com.moneytrack.settings.presentation.ThemeRoute
import com.moneytrack.transaction.presentation.TransactionRoute

@Composable
@Suppress("LongMethod")
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
            HomeRoute(
                onTransactionClick = {
                    navController.navigateToTopLevel(AppDestination.Transaction.route)
                },
                onProfileClick = {
                    navController.navigateToTopLevel(AppDestination.Profile.route)
                },
                onAddExpenseClick = {
                    navController.navigate(AppDestination.Expense.route) {
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(AppDestination.Expense.route) {
            ExpenseRoute(
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }

        composable(AppDestination.Transaction.route) {
            TransactionRoute(
                onHomeClick = {
                    navController.navigateToTopLevel(AppDestination.Home.route)
                },
                onProfileClick = {
                    navController.navigateToTopLevel(AppDestination.Profile.route)
                },
                onAddExpenseClick = {
                    navController.navigate(AppDestination.Expense.route) {
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(AppDestination.Profile.route) {
            ProfileRoute(
                onHomeClick = {
                    navController.navigateToTopLevel(AppDestination.Home.route)
                },
                onTransactionClick = {
                    navController.navigateToTopLevel(AppDestination.Transaction.route)
                },
                onAddExpenseClick = {
                    navController.navigate(AppDestination.Expense.route) {
                        launchSingleTop = true
                    }
                },
                onSettingsClick = {
                    navController.navigate(AppDestination.Settings.route) {
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(AppDestination.Settings.route) {
            SettingsRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onCurrencyClick = {
                    navController.navigate(AppDestination.Currency.route) {
                        launchSingleTop = true
                    }
                },
                onThemeClick = {
                    navController.navigate(AppDestination.Theme.route) {
                        launchSingleTop = true
                    }
                },
                onSecurityClick = {
                    navController.navigate(AppDestination.Security.route) {
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(AppDestination.Currency.route) {
            CurrencyRoute(
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }

        composable(AppDestination.Theme.route) {
            ThemeRoute(
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }

        composable(AppDestination.Security.route) {
            SecurityRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onPinOptionClick = {
                    navController.navigate(AppDestination.SecurityPinSetup.route) {
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(AppDestination.SecurityPinSetup.route) {
            PinSetupRoute(
                onCompleted = {
                    navController.popBackStack()
                },
            )
        }
    }
}

private fun NavHostController.navigateToTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
