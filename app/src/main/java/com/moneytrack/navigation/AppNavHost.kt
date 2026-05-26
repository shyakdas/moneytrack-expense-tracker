// Copyright (c) 2026 shyakdas

package com.moneytrack.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.moneytrack.expense.presentation.ExpenseRoute
import com.moneytrack.home.presentation.HomeRoute
import com.moneytrack.onboarding.presentation.OnboardingRoute
import com.moneytrack.pinauth.presentation.PinAuthRoute
import com.moneytrack.profile.presentation.ExportDataRoute
import com.moneytrack.pinsetup.presentation.PinSetupRoute
import com.moneytrack.profile.presentation.ProfileRoute
import com.moneytrack.settings.presentation.AboutRoute
import com.moneytrack.settings.presentation.CurrencyRoute
import com.moneytrack.settings.presentation.NotificationRoute
import com.moneytrack.settings.presentation.SecurityRoute
import com.moneytrack.settings.presentation.SettingsScreenActions
import com.moneytrack.settings.presentation.SettingsRoute
import com.moneytrack.settings.presentation.ThemeRoute
import com.moneytrack.transaction.presentation.TransactionRoute
import ui.theme.MotionTokens

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
        enterTransition = { appEnterTransition() },
        exitTransition = { appExitTransition() },
        popEnterTransition = { appPopEnterTransition() },
        popExitTransition = { appPopExitTransition() },
        sizeTransform = { SizeTransform(clip = false) },
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
                onEditExpenseClick = { expenseId ->
                    navController.navigateToExpenseEditor(expenseId)
                },
            )
        }

        composable(
            route = EXPENSE_ROUTE_WITH_OPTIONAL_ID,
            arguments = listOf(
                navArgument(EXPENSE_ID_ARG) {
                    type = NavType.LongType
                    defaultValue = NO_EXPENSE_ID
                },
            ),
        ) { entry ->
            ExpenseRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                expenseId = entry.editExpenseIdOrNull(),
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
                onEditExpenseClick = { expenseId ->
                    navController.navigateToExpenseEditor(expenseId)
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
                onExportClick = {
                    navController.navigate(AppDestination.ExportData.route) {
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(AppDestination.Settings.route) {
            SettingsRoute(
                actions = SettingsScreenActions(
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
                    onNotificationClick = {
                        navController.navigate(AppDestination.Notification.route) {
                            launchSingleTop = true
                        }
                    },
                    onAboutClick = {
                        navController.navigate(AppDestination.About.route) {
                            launchSingleTop = true
                        }
                    },
                ),
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

        composable(AppDestination.Notification.route) {
            NotificationRoute(
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }

        composable(AppDestination.About.route) {
            AboutRoute(
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }

        composable(AppDestination.ExportData.route) {
            ExportDataRoute(
                onBackClick = {
                    navController.popBackStack()
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

private fun NavBackStackEntry.editExpenseIdOrNull(): Long? {
    val id = arguments?.getLong(EXPENSE_ID_ARG, NO_EXPENSE_ID) ?: NO_EXPENSE_ID
    return id.takeIf { it != NO_EXPENSE_ID }
}

private fun NavHostController.navigateToExpenseEditor(expenseId: Long) {
    navigate("${AppDestination.Expense.route}?$EXPENSE_ID_ARG=$expenseId") {
        launchSingleTop = true
    }
}

private const val EXPENSE_ID_ARG = "expenseId"
private const val NO_EXPENSE_ID = -1L
private val EXPENSE_ROUTE_WITH_OPTIONAL_ID = "${AppDestination.Expense.route}?$EXPENSE_ID_ARG={$EXPENSE_ID_ARG}"

private val topLevelRoutes = setOf(
    AppDestination.Home.route,
    AppDestination.Transaction.route,
    AppDestination.Profile.route,
)

private fun AnimatedContentTransitionScope<NavBackStackEntry>.appEnterTransition(): EnterTransition {
    val targetRoute = targetState.destination.route
    val initialRoute = initialState.destination.route
    val isTopLevelSwitch = initialRoute in topLevelRoutes && targetRoute in topLevelRoutes

    return when {
        isTopLevelSwitch -> fadeIn(
            animationSpec = MotionTokens.standardTween(MotionTokens.DurationMedium),
        ) + scaleIn(
            initialScale = 0.98f,
            animationSpec = MotionTokens.standardTween(MotionTokens.DurationMedium),
        )

        targetRoute == AppDestination.Expense.route -> slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Up,
            animationSpec = tween(
                durationMillis = MotionTokens.DurationLong,
                easing = MotionTokens.EmphasizedEasing,
            ),
        ) + fadeIn(animationSpec = MotionTokens.standardTween())

        else -> slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(
                durationMillis = MotionTokens.DurationLong,
                easing = MotionTokens.EmphasizedEasing,
            ),
        ) + fadeIn(animationSpec = MotionTokens.standardTween())
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.appExitTransition(): ExitTransition {
    val targetRoute = targetState.destination.route
    val initialRoute = initialState.destination.route
    val isTopLevelSwitch = initialRoute in topLevelRoutes && targetRoute in topLevelRoutes

    return when {
        isTopLevelSwitch -> fadeOut(
            animationSpec = MotionTokens.standardTween(MotionTokens.DurationShort),
        ) + scaleOut(
            targetScale = 1.02f,
            animationSpec = MotionTokens.standardTween(MotionTokens.DurationShort),
        )

        initialRoute == AppDestination.Expense.route -> slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Down,
            animationSpec = tween(
                durationMillis = MotionTokens.DurationMedium,
                easing = MotionTokens.StandardEasing,
            ),
        ) + fadeOut(animationSpec = MotionTokens.standardTween(MotionTokens.DurationShort))

        else -> slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(
                durationMillis = MotionTokens.DurationMedium,
                easing = MotionTokens.StandardEasing,
            ),
        ) + fadeOut(animationSpec = MotionTokens.standardTween(MotionTokens.DurationShort))
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.appPopEnterTransition(): EnterTransition =
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(
            durationMillis = MotionTokens.DurationMedium,
            easing = MotionTokens.EmphasizedEasing,
        ),
    ) + fadeIn(animationSpec = MotionTokens.standardTween())

private fun AnimatedContentTransitionScope<NavBackStackEntry>.appPopExitTransition(): ExitTransition {
    val initialRoute = initialState.destination.route
    val direction = if (initialRoute == AppDestination.Expense.route) {
        AnimatedContentTransitionScope.SlideDirection.Down
    } else {
        AnimatedContentTransitionScope.SlideDirection.Right
    }

    return slideOutOfContainer(
        towards = direction,
        animationSpec = tween(
            durationMillis = MotionTokens.DurationMedium,
            easing = MotionTokens.StandardEasing,
        ),
    ) + fadeOut(animationSpec = MotionTokens.standardTween(MotionTokens.DurationShort))
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
