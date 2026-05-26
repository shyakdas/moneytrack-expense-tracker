// Copyright (c) 2026 shyakdas

package com.moneytrack.home.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneytrack.home.domain.model.Budget
import com.moneytrack.reminder.presentation.NotificationPermissionUiState
import com.moneytrack.reminder.presentation.NotificationPermissionViewModel
import ui.components.card.bottomsheet.SheetBlurHost

internal const val ROUTE_HOME = "home"
internal const val ROUTE_TRANSACTION = "transaction"
internal const val ROUTE_PROFILE = "profile"

@Composable
fun HomeRoute(
    onTransactionClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    onEditExpenseClick: (Long) -> Unit = {},
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val notificationPermissionViewModel: NotificationPermissionViewModel = hiltViewModel()
    val notificationPermissionUiState = notificationPermissionViewModel.uiState.collectAsStateWithLifecycle().value

    HomeRouteContent(
        viewModel = viewModel,
        notificationPermissionViewModel = notificationPermissionViewModel,
        state = HomeRouteState(
            uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
            notificationPermissionUiState = notificationPermissionUiState,
            budget = viewModel.budget.collectAsStateWithLifecycle().value,
            isBudgetLoaded = viewModel.isBudgetLoaded.collectAsStateWithLifecycle().value,
        ),
        actions = HomeRouteActions(
            onTransactionClick = onTransactionClick,
            onProfileClick = onProfileClick,
            onAddExpenseClick = onAddExpenseClick,
            onEditExpenseClick = onEditExpenseClick,
        ),
    )
}

@Composable
private fun HomeRouteContent(
    viewModel: HomeViewModel,
    notificationPermissionViewModel: NotificationPermissionViewModel,
    state: HomeRouteState,
    actions: HomeRouteActions,
) {
    val context = LocalContext.current
    var hasNotificationPermission by remember {
        mutableStateOf(context.hasNotificationPermission())
    }
    var showBudgetSheet by remember { mutableStateOf(false) }
    var budgetSheetInitialAmount by remember { mutableStateOf<Double?>(null) }
    var hasShownInitialBudgetPrompt by remember { mutableStateOf(false) }
    val shouldShowNotificationPermissionSheet = state.notificationPermissionUiState.isPermissionPromptVisible &&
        !hasNotificationPermission

    HomeRouteInitialSelectionEffect(viewModel = viewModel)

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasNotificationPermission = granted || context.hasNotificationPermission()
        notificationPermissionViewModel.markPermissionPromptHandled()
        notificationPermissionViewModel.hidePermissionPrompt()
    }

    HomeScreenWithSheets(
        showBlur = showBudgetSheet || shouldShowNotificationPermissionSheet,
        state = state,
        viewModel = viewModel,
        actions = actions,
        onSetBudgetClick = { budgetAmount ->
            budgetSheetInitialAmount = budgetAmount
            showBudgetSheet = true
        },
    )

    HomeRoutePromptEffects(
        state = HomePromptState(
            isBudgetLoaded = state.isBudgetLoaded,
            budget = state.budget,
            hasNotificationPermission = hasNotificationPermission,
            isNotificationPromptHandled = state.notificationPermissionUiState.isPromptHandled,
            shouldShowNotificationPermissionSheet = shouldShowNotificationPermissionSheet,
            hasShownInitialBudgetPrompt = hasShownInitialBudgetPrompt,
        ),
        onShowNotificationPrompt = notificationPermissionViewModel::showPermissionPrompt,
        onPromptShown = {
            showBudgetSheet = true
            budgetSheetInitialAmount = null
            hasShownInitialBudgetPrompt = true
        },
    )

    HomeNotificationSheetHost(
        visible = shouldShowNotificationPermissionSheet,
        notificationsPerDay = state.notificationPermissionUiState.reminderSettings.notificationsPerDay,
        notificationPermissionViewModel = notificationPermissionViewModel,
        onRequestPermission = { notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
    )

    HomeBudgetSheetHost(
        visible = showBudgetSheet,
        initialBudgetAmount = budgetSheetInitialAmount,
        selectedMonth = state.uiState.selectedMonth,
        viewModel = viewModel,
        onDismiss = { showBudgetSheet = false },
    )
}

@Composable
private fun HomeRouteInitialSelectionEffect(viewModel: HomeViewModel) {
    LaunchedEffect(viewModel) {
        viewModel.onBottomRouteSelected(ROUTE_HOME)
    }
}

@Composable
private fun HomeScreenWithSheets(
    showBlur: Boolean,
    state: HomeRouteState,
    viewModel: HomeViewModel,
    actions: HomeRouteActions,
    onSetBudgetClick: (Double?) -> Unit,
) {
    SheetBlurHost(isSheetVisible = showBlur) {
        HomeScreen(
            uiState = state.uiState,
            isBudgetLoaded = state.isBudgetLoaded,
            onBottomRouteSelected = { route ->
                viewModel.onBottomRouteSelected(route)
                when (route) {
                    ROUTE_TRANSACTION -> actions.onTransactionClick()
                    ROUTE_PROFILE -> actions.onProfileClick()
                    else -> Unit
                }
            },
            onSeeAllTransactionsClick = actions.onTransactionClick,
            onExpensesClick = actions.onTransactionClick,
            onTransactionCardClick = actions.onEditExpenseClick,
            onTimeRangeSelected = viewModel::onTimeRangeSelected,
            onMonthSelected = viewModel::onMonthSelected,
            onYearSelected = viewModel::onYearSelected,
            onDeleteTransaction = viewModel::deleteTransaction,
            onSetBudgetClick = onSetBudgetClick,
            onAddExpenseClick = actions.onAddExpenseClick,
        )
    }
}

@Composable
private fun HomeRoutePromptEffects(
    state: HomePromptState,
    onShowNotificationPrompt: () -> Unit,
    onPromptShown: () -> Unit,
) {
    HomeNotificationPromptEffect(
        hasNotificationPermission = state.hasNotificationPermission,
        isPromptHandled = state.isNotificationPromptHandled,
        onShowPrompt = onShowNotificationPrompt,
    )

    HomeBudgetPromptEffect(
        isBudgetLoaded = state.isBudgetLoaded,
        budget = state.budget,
        shouldShowNotificationPermissionSheet = state.shouldShowNotificationPermissionSheet,
        hasShownInitialBudgetPrompt = state.hasShownInitialBudgetPrompt,
        onPromptShown = onPromptShown,
    )
}

@Composable
private fun HomeNotificationSheetHost(
    visible: Boolean,
    notificationsPerDay: Int,
    notificationPermissionViewModel: NotificationPermissionViewModel,
    onRequestPermission: () -> Unit,
) {
    if (!visible) return

    NotificationPermissionBottomSheet(
        notificationsPerDay = notificationsPerDay,
        onAllowNotifications = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                onRequestPermission()
            } else {
                notificationPermissionViewModel.markPermissionPromptHandled()
                notificationPermissionViewModel.hidePermissionPrompt()
            }
        },
        onNotNow = {
            notificationPermissionViewModel.markPermissionPromptHandled()
            notificationPermissionViewModel.hidePermissionPrompt()
        },
    )
}

@Composable
private fun HomeBudgetSheetHost(
    visible: Boolean,
    initialBudgetAmount: Double?,
    selectedMonth: HomeMonthOption,
    viewModel: HomeViewModel,
    onDismiss: () -> Unit,
) {
    if (!visible) return

    BudgetSetupBottomSheet(
        onDismiss = onDismiss,
        initialBudgetAmount = initialBudgetAmount,
        selectedMonth = selectedMonth,
        formatAmount = viewModel::formatCurrency,
        onSaveBudget = { budgetValue, description ->
            viewModel.saveBudget(
                month = selectedMonth.monthIndex + 1,
                year = selectedMonth.year,
                amount = budgetValue,
                description = description,
            )
        },
    )
}

private fun Context.hasNotificationPermission(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.POST_NOTIFICATIONS,
    ) == PackageManager.PERMISSION_GRANTED
}

private data class HomeRouteState(
    val uiState: HomeUiState,
    val notificationPermissionUiState: NotificationPermissionUiState,
    val budget: Budget?,
    val isBudgetLoaded: Boolean,
)

private data class HomeRouteActions(
    val onTransactionClick: () -> Unit,
    val onProfileClick: () -> Unit,
    val onAddExpenseClick: () -> Unit,
    val onEditExpenseClick: (Long) -> Unit,
)

private data class HomePromptState(
    val isBudgetLoaded: Boolean,
    val budget: Budget?,
    val hasNotificationPermission: Boolean,
    val isNotificationPromptHandled: Boolean,
    val shouldShowNotificationPermissionSheet: Boolean,
    val hasShownInitialBudgetPrompt: Boolean,
)
