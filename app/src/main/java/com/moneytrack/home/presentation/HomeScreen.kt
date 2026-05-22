// Copyright (c) 2026 shyakdas

@file:Suppress("MagicNumber", "LongMethod", "TooManyFunctions", "UnusedPrivateMember", "LongParameterList")

package com.moneytrack.home.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneytrack.designsystem.R
import com.moneytrack.common.ui.LottieAnimationView
import com.moneytrack.reminder.presentation.NotificationPermissionViewModel
import com.moneytrack.R as AppR
import kotlinx.coroutines.delay
import androidx.core.content.ContextCompat
import ui.components.card.transaction.TransactionCard
import ui.components.card.transaction.TransactionType
import ui.components.card.bottomsheet.SheetBlurHost
import ui.components.navigation.bottomNav.BottomNavItem
import ui.components.navigation.bottomNav.PrimaryBottomNavigation
import ui.components.navigation.button.LargeButton
import ui.components.navigation.common.SeeAllPill
import ui.components.navigation.tabs.TimeRangeTab
import ui.components.navigation.topNav.TopNavigation
import ui.components.navigation.topNav.TopNavigationConfig
import ui.components.surface.MoneyTrackScreenBackground
import ui.components.surface.MoneyTrackBottomSheet
import ui.components.form.input.InputField
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

private const val ROUTE_HOME = "home"
private const val ROUTE_TRANSACTION = "transaction"
private const val ROUTE_BUDGET = "budget"
private const val ROUTE_PROFILE = "profile"
private const val MAX_BUDGET_INPUT_LENGTH = 8
private const val BUDGET_SUCCESS_DISMISS_DELAY_MS = 1200L
private val SUMMARY_CARD_MIN_HEIGHT = 118.dp
private enum class BudgetSheetStep {
    PRIVACY,
    INPUT,
    SUCCESS,
}

@Composable
fun HomeRoute(
    onTransactionClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val notificationPermissionViewModel: NotificationPermissionViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val notificationPermissionUiState =
        notificationPermissionViewModel.uiState.collectAsStateWithLifecycle().value
    val budget = viewModel.budget.collectAsStateWithLifecycle().value
    val isBudgetLoaded = viewModel.isBudgetLoaded.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    var hasNotificationPermission by remember {
        mutableStateOf(context.hasNotificationPermission())
    }
    var showBudgetSheet by remember { mutableStateOf(false) }
    var budgetSheetInitialAmount by remember { mutableStateOf<Double?>(null) }
    var hasShownInitialBudgetPrompt by remember { mutableStateOf(false) }
    val shouldShowNotificationPermissionSheet = notificationPermissionUiState.isPermissionPromptVisible &&
        !hasNotificationPermission

    LaunchedEffect(viewModel) {
        viewModel.onBottomRouteSelected(ROUTE_HOME)
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasNotificationPermission = granted || context.hasNotificationPermission()
        notificationPermissionViewModel.markPermissionPromptHandled()
        notificationPermissionViewModel.hidePermissionPrompt()
    }

    val onBottomRouteSelected: (String) -> Unit = { route ->
        viewModel.onBottomRouteSelected(route)
        when (route) {
            ROUTE_TRANSACTION -> onTransactionClick()
            ROUTE_PROFILE -> onProfileClick()
            else -> Unit
        }
    }

    SheetBlurHost(isSheetVisible = showBudgetSheet || shouldShowNotificationPermissionSheet) {
        HomeScreen(
            uiState = uiState,
            isBudgetLoaded = isBudgetLoaded,
            onBottomRouteSelected = onBottomRouteSelected,
            onSeeAllTransactionsClick = onTransactionClick,
            onTimeRangeSelected = viewModel::onTimeRangeSelected,
            onSetBudgetClick = { budgetAmount ->
                budgetSheetInitialAmount = budgetAmount
                showBudgetSheet = true
            },
            onAddExpenseClick = onAddExpenseClick,
        )
    }

    HomeNotificationPromptEffect(
        hasNotificationPermission = hasNotificationPermission,
        isPromptHandled = notificationPermissionUiState.isPromptHandled,
        onShowPrompt = notificationPermissionViewModel::showPermissionPrompt,
    )

    HomeBudgetPromptEffect(
        isBudgetLoaded = isBudgetLoaded,
        budget = budget,
        shouldShowNotificationPermissionSheet = shouldShowNotificationPermissionSheet,
        hasShownInitialBudgetPrompt = hasShownInitialBudgetPrompt,
        onPromptShown = {
            showBudgetSheet = true
            budgetSheetInitialAmount = null
            hasShownInitialBudgetPrompt = true
        },
    )

    if (shouldShowNotificationPermissionSheet) {
        NotificationPermissionBottomSheet(
            notificationsPerDay = notificationPermissionUiState.reminderSettings.notificationsPerDay,
            onAllowNotifications = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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

    if (showBudgetSheet) {
        BudgetSetupBottomSheet(
            onDismiss = { showBudgetSheet = false },
            initialBudgetAmount = budgetSheetInitialAmount,
            formatAmount = viewModel::formatCurrency,
            onSaveBudget = { budgetValue, description ->
                viewModel.saveBudget(
                    amount = budgetValue,
                    description = description,
                )
            },
            onSavedCompleted = {
                showBudgetSheet = false
            },
        )
    }
}

@Composable
private fun HomeNotificationPromptEffect(
    hasNotificationPermission: Boolean,
    isPromptHandled: Boolean,
    onShowPrompt: () -> Unit,
) {
    LaunchedEffect(isPromptHandled, hasNotificationPermission) {
        if (!isPromptHandled && !hasNotificationPermission) {
            onShowPrompt()
        }
    }
}

@Composable
private fun HomeBudgetPromptEffect(
    isBudgetLoaded: Boolean,
    budget: com.moneytrack.home.domain.model.Budget?,
    shouldShowNotificationPermissionSheet: Boolean,
    hasShownInitialBudgetPrompt: Boolean,
    onPromptShown: () -> Unit,
) {
    LaunchedEffect(isBudgetLoaded, budget, shouldShowNotificationPermissionSheet) {
        if (isBudgetLoaded && budget == null && !hasShownInitialBudgetPrompt) {
            if (shouldShowNotificationPermissionSheet) {
                return@LaunchedEffect
            }
            onPromptShown()
        }
    }
}

private fun Context.hasNotificationPermission(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.POST_NOTIFICATIONS,
    ) == PackageManager.PERMISSION_GRANTED
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    isBudgetLoaded: Boolean = true,
    onBottomRouteSelected: (String) -> Unit,
    onSeeAllTransactionsClick: () -> Unit,
    onTimeRangeSelected: (String) -> Unit,
    onSetBudgetClick: (Double?) -> Unit,
    onAddExpenseClick: () -> Unit = {},
) {
    val bottomItems = remember {
        listOf(
            BottomNavItem(ROUTE_HOME, R.drawable.home, "Home"),
            BottomNavItem(ROUTE_TRANSACTION, R.drawable.transaction, "Transaction"),
            BottomNavItem(ROUTE_BUDGET, R.drawable.line_chart_2, "Budget"),
            BottomNavItem(ROUTE_PROFILE, R.drawable.user, "Profile"),
        )
    }
    Scaffold(
        containerColor = AppTheme.colors.background,
        bottomBar = {
            Box(modifier = Modifier.navigationBarsPadding()) {
                PrimaryBottomNavigation(
                    items = bottomItems,
                    selectedRoute = ROUTE_HOME,
                    onItemClick = { item -> onBottomRouteSelected(item.route) },
                    onFabClick = onAddExpenseClick,
                )
            }
        },
    ) { innerPadding ->
        HomeContent(
            innerPadding = innerPadding,
            uiState = uiState,
            isBudgetLoaded = isBudgetLoaded,
            onSeeAllTransactionsClick = onSeeAllTransactionsClick,
            onTimeRangeSelected = onTimeRangeSelected,
            onSetBudgetClick = onSetBudgetClick,
        )
    }
}

@Composable
private fun HomeContent(
    innerPadding: PaddingValues,
    uiState: HomeUiState,
    isBudgetLoaded: Boolean,
    onSeeAllTransactionsClick: () -> Unit,
    onTimeRangeSelected: (String) -> Unit,
    onSetBudgetClick: (Double?) -> Unit,
) {
    if (!isBudgetLoaded) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppTheme.colors.background),
        )
        return
    }

    if (!uiState.hasBudget) {
        BudgetRequiredState(
            innerPadding = innerPadding,
            onSetBudgetClick = { onSetBudgetClick(null) },
        )
        return
    }

    MoneyTrackScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
        TopNavigation(
            config = TopNavigationConfig.ProfileWithSelector(
                profileImage = ColorPainter(AppTheme.colors.primaryContainer),
                profileAvatarContent = {
                    LottieAnimationView(
                        rawRes = AppR.raw.lottie_profile_people,
                        modifier = Modifier.fillMaxSize(),
                        speed = 1.2f,
                    )
                },
                selectedMonth = "October",
                onMonthClick = {},
                onActionClick = {},
                actionIconTint = AppTheme.colors.primary,
            ),
            containerColor = Color.Transparent,
        )
        Spacer(modifier = Modifier.height(Dimens.spacing12))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.spacing16),
            ) {
            BalanceSummaryCard(
                accountBalanceText = uiState.accountBalanceText,
                hasBudget = uiState.hasBudget,
                budgetAmount = uiState.budgetAmount,
                budgetText = uiState.budgetText,
                expensesText = uiState.expensesText,
                onSetBudgetClick = onSetBudgetClick,
            )
            Spacer(modifier = Modifier.height(Dimens.spacing24))

            Text(
                text = "Spend Frequency",
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.onBackground,
            )
            Spacer(modifier = Modifier.height(Dimens.spacing16))
            if (uiState.hasSpendFrequencyData) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.radius16),
                    color = AppTheme.colors.surface,
                    tonalElevation = Dimens.elevation2,
                ) {
                    Crossfade(
                        targetState = uiState.spendFrequencyPoints,
                        label = "SpendFrequencyData",
                    ) { points ->
                        SpendFrequencyChart(
                            points = points,
                            modifier = Modifier.padding(Dimens.spacing16),
                        )
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.radius16),
                    color = AppTheme.colors.surface,
                    tonalElevation = Dimens.elevation2,
                ) {
                    Text(
                        text = stringResource(id = AppR.string.home_spend_frequency_empty),
                        modifier = Modifier.padding(Dimens.spacing16),
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.onSurfaceVariant,
                    )
                }
            }
            Spacer(modifier = Modifier.height(Dimens.spacing16))

            TimeRangeTab(
                options = listOf("Today", "Week", "Month", "Year"),
                selectedOption = uiState.selectedRange,
                onOptionSelected = onTimeRangeSelected,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(Dimens.spacing20))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Recent Transaction",
                    style = AppTheme.typography.titleMedium,
                    color = AppTheme.colors.onBackground,
                )
                Spacer(modifier = Modifier.weight(1f))
                SeeAllPill(onClick = onSeeAllTransactionsClick)
            }
            Spacer(modifier = Modifier.height(Dimens.spacing12))

            if (uiState.transactions.isEmpty()) {
                Text(
                    text = stringResource(id = AppR.string.home_empty_transactions_desc),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant,
                )
            } else {
                uiState.transactions.forEach { transaction ->
                    TransactionCard(
                        icon = ImageVector.vectorResource(id = transaction.icon),
                        title = transaction.title,
                        subtitle = transaction.subtitle,
                        amount = transaction.amount,
                        time = transaction.time,
                        type = transaction.type,
                    )
                    Spacer(modifier = Modifier.height(Dimens.spacing12))
                }
                Spacer(modifier = Modifier.height(Dimens.spacing12))
            }
            }
        }
    }
}

@Composable
private fun BudgetRequiredState(
    innerPadding: PaddingValues,
    onSetBudgetClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = Dimens.spacing24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        LottieAnimationView(
            rawRes = AppR.raw.lottie_home_budget_prompt,
            modifier = Modifier.size(Dimens.lottieHeroSize),
        )
        Spacer(modifier = Modifier.height(Dimens.spacing24))
        LargeButton(
            text = stringResource(id = AppR.string.home_set_budget_cta),
            onClick = onSetBudgetClick,
        )
    }
}

@Composable
private fun BalanceSummaryCard(
    accountBalanceText: String,
    hasBudget: Boolean,
    budgetAmount: Double?,
    budgetText: String?,
    expensesText: String,
    onSetBudgetClick: (Double?) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radius20),
        color = Color.Transparent,
        tonalElevation = Dimens.elevation2,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AppTheme.colors.primary,
                            AppTheme.colors.primary.copy(alpha = 0.86f),
                        ),
                    ),
                )
                .padding(Dimens.spacing20),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Account Balance",
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colors.onPrimary.copy(alpha = 0.78f),
            )
            Spacer(modifier = Modifier.height(Dimens.spacing8))
            Text(
                text = accountBalanceText,
                style = AppTheme.typography.headlineLarge,
                color = AppTheme.colors.onPrimary,
            )
            Spacer(modifier = Modifier.height(Dimens.spacing20))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing16),
            ) {
                if (!hasBudget) {
                    MissingBudgetCard(
                        modifier = Modifier.weight(1f),
                        onClick = { onSetBudgetClick(null) },
                    )
                } else {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = stringResource(id = AppR.string.home_budget_label),
                        value = budgetText.orEmpty(),
                        icon = R.drawable.line_chart_2,
                        background = AppTheme.colors.success,
                        onClick = { onSetBudgetClick(budgetAmount) },
                    )
                }
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = stringResource(id = AppR.string.home_expenses_label),
                    value = expensesText,
                    icon = R.drawable.expense,
                    background = AppTheme.colors.error,
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: Int,
    background: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier
            .heightIn(min = SUMMARY_CARD_MIN_HEIGHT)
            .let { base ->
                if (onClick == null) {
                    base
                } else {
                    base.clickable(onClick = onClick)
                }
            },
        color = background,
        shape = RoundedCornerShape(Dimens.radius16),
    ) {
        androidx.compose.foundation.layout.BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
        ) {
            val compact = maxWidth < 170.dp
            val iconSize = if (compact) Dimens.spacing36 else Dimens.iconContainerSize
            val labelStyle = if (compact) {
                AppTheme.typography.bodySmall
            } else {
                AppTheme.typography.bodyMedium
            }
            val valueStyle = if (compact) {
                AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            } else {
                AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.spacing12),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .background(
                            color = AppTheme.colors.onPrimary,
                            shape = RoundedCornerShape(Dimens.radius12),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = icon),
                        contentDescription = null,
                        tint = background,
                    )
                }
                Spacer(modifier = Modifier.width(if (compact) Dimens.spacing6 else Dimens.spacing8))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        style = labelStyle,
                        color = AppTheme.colors.onPrimary,
                        maxLines = if (compact) 2 else 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = value,
                        style = valueStyle,
                        color = AppTheme.colors.onPrimary,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
private fun MissingBudgetCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .heightIn(min = SUMMARY_CARD_MIN_HEIGHT)
            .border(
                width = Dimens.borderThick,
                color = AppTheme.colors.primary.copy(alpha = 0.4f),
                shape = RoundedCornerShape(Dimens.radius16),
            ),
        color = AppTheme.colors.primaryContainer,
        shape = RoundedCornerShape(Dimens.radius16),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacing12),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing8),
        ) {
            LottieAnimationView(
                rawRes = AppR.raw.lottie_budget_wallet,
                modifier = Modifier.size(Dimens.spacing36),
                iterations = 1,
            )
            LargeButton(
                text = stringResource(id = AppR.string.home_set_budget_cta),
                onClick = onClick,
            )
        }
    }
}

@Composable
private fun NotificationPermissionBottomSheet(
    notificationsPerDay: Int,
    onAllowNotifications: () -> Unit,
    onNotNow: () -> Unit,
) {
    MoneyTrackBottomSheet(
        onDismissRequest = onNotNow,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacing24)
                .padding(bottom = Dimens.spacing24),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing16),
        ) {
            Text(
                text = stringResource(id = AppR.string.home_notification_sheet_title),
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.onSurface,
            )
            Text(
                text = pluralStringResource(
                    id = AppR.plurals.home_notification_sheet_desc,
                    count = notificationsPerDay,
                    notificationsPerDay,
                ),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant,
            )
            LargeButton(
                text = stringResource(id = AppR.string.home_notification_sheet_enable),
                onClick = onAllowNotifications,
            )
            LargeButton(
                text = stringResource(id = AppR.string.home_notification_sheet_not_now),
                onClick = onNotNow,
            )
        }
    }
}

@Composable
private fun BudgetSetupBottomSheet(
    onDismiss: () -> Unit,
    initialBudgetAmount: Double?,
    formatAmount: (Double) -> String,
    onSaveBudget: (Double, String?) -> Unit,
    onSavedCompleted: () -> Unit,
) {
    var budgetInput by remember(initialBudgetAmount) {
        mutableStateOf(initialBudgetAmount?.toLong()?.toString().orEmpty())
    }
    var sheetStep by remember(initialBudgetAmount) {
        mutableStateOf(if (initialBudgetAmount == null) BudgetSheetStep.PRIVACY else BudgetSheetStep.INPUT)
    }
    val parsedBudget = budgetInput.toDoubleOrNull()
    val canContinue = parsedBudget != null && parsedBudget > 0.0

    LaunchedEffect(sheetStep) {
        if (sheetStep == BudgetSheetStep.SUCCESS) {
            delay(BUDGET_SUCCESS_DISMISS_DELAY_MS)
            onSavedCompleted()
        }
    }

    MoneyTrackBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacing24)
                .padding(bottom = Dimens.spacing24),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing16),
        ) {
            when (sheetStep) {
                BudgetSheetStep.PRIVACY -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radius16),
                        color = AppTheme.colors.primaryContainer,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimens.spacing16),
                            verticalArrangement = Arrangement.spacedBy(Dimens.spacing8),
                        ) {
                            Text(
                                text = stringResource(id = AppR.string.home_budget_privacy_title),
                                style = AppTheme.typography.titleMedium,
                                color = AppTheme.colors.onSurface,
                            )
                            Text(
                                text = stringResource(id = AppR.string.home_budget_privacy_desc),
                                style = AppTheme.typography.bodySmall,
                                color = AppTheme.colors.onSurfaceVariant,
                            )
                            Text(
                                text = stringResource(id = AppR.string.home_budget_need_title),
                                style = AppTheme.typography.titleMedium,
                                color = AppTheme.colors.onSurface,
                            )
                            Text(
                                text = stringResource(id = AppR.string.home_budget_need_desc),
                                style = AppTheme.typography.bodySmall,
                                color = AppTheme.colors.onSurfaceVariant,
                            )
                        }
                    }

                    LargeButton(
                        text = stringResource(id = AppR.string.home_budget_sheet_next),
                        onClick = { sheetStep = BudgetSheetStep.INPUT },
                    )
                }

                BudgetSheetStep.INPUT -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = AppTheme.colors.primary,
                        shape = RoundedCornerShape(Dimens.radius24),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimens.spacing20),
                        ) {
                            Text(
                                text = stringResource(id = AppR.string.home_budget_sheet_title),
                                style = AppTheme.typography.titleMedium,
                                color = AppTheme.colors.onPrimary,
                            )
                            Spacer(modifier = Modifier.height(Dimens.spacing8))
                            Text(
                                text = formatAmount(parsedBudget ?: 0.0),
                                style = AppTheme.typography.headlineLarge,
                                color = AppTheme.colors.onPrimary,
                            )
                        }
                    }

                    InputField(
                        value = budgetInput,
                        onValueChange = { input ->
                            val filtered = input.filter { it.isDigit() }.take(MAX_BUDGET_INPUT_LENGTH)
                            budgetInput = filtered
                        },
                        placeholder = stringResource(id = AppR.string.home_budget_sheet_amount_hint),
                        leadingIcon = ImageVector.vectorResource(id = R.drawable.wallet_3),
                    )

                    LargeButton(
                        text = stringResource(id = AppR.string.home_budget_sheet_continue),
                        onClick = {
                            parsedBudget?.let { budgetValue ->
                                onSaveBudget(
                                    budgetValue,
                                    null,
                                )
                                sheetStep = BudgetSheetStep.SUCCESS
                            }
                        },
                        enabled = canContinue,
                    )
                }

                BudgetSheetStep.SUCCESS -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Dimens.spacing24),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacing12),
                    ) {
                        LottieAnimationView(
                            rawRes = AppR.raw.lottie_budget_wallet,
                            modifier = Modifier.size(Dimens.spacing72),
                            iterations = 1,
                        )
                        Text(
                            text = stringResource(id = AppR.string.home_budget_saved_title),
                            style = AppTheme.typography.titleMedium,
                            color = AppTheme.colors.onSurface,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpendFrequencyChart(
    points: List<Float>,
    modifier: Modifier = Modifier,
) {
    val chartLineColor = AppTheme.colors.primary
    val chartFillColor = AppTheme.colors.primary.copy(alpha = 0.15f)
    val chartGridColor = AppTheme.colors.outline.copy(alpha = 0.15f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(170.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (points.isEmpty()) {
                drawRect(
                    color = chartGridColor,
                    size = Size(width = size.width, height = 1.dp.toPx()),
                )
                return@Canvas
            }

            val normalizedPoints = points.normalizeForChart()
            val chartPoints = normalizedPoints.mapIndexed { index, value ->
                val x = if (normalizedPoints.size == 1) {
                    size.width / 2f
                } else {
                    size.width * index / (normalizedPoints.lastIndex.coerceAtLeast(1))
                }
                Offset(x, size.height * value)
            }.let { generated ->
                if (generated.size == 1) {
                    listOf(
                        Offset(0f, generated.first().y),
                        Offset(size.width, generated.first().y),
                    )
                } else {
                    generated
                }
            }

            val linePath = Path().apply {
                moveTo(chartPoints.first().x, chartPoints.first().y)
                for (index in 1 until chartPoints.size) {
                    val prev = chartPoints[index - 1]
                    val current = chartPoints[index]
                    val cx = (prev.x + current.x) / 2
                    cubicTo(cx, prev.y, cx, current.y, current.x, current.y)
                }
            }

            val fillPath = Path().apply {
                addPath(linePath)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawPath(path = fillPath, color = chartFillColor)

            drawPath(
                path = linePath,
                color = chartLineColor,
                style = Stroke(
                    width = 5.dp.toPx(),
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.cornerPathEffect(30f),
                ),
            )

            drawRect(
                color = chartGridColor,
                size = Size(width = size.width, height = 1.dp.toPx()),
            )
        }
    }
}

private fun List<Float>.normalizeForChart(): List<Float> {
    val maxValue = maxOrNull()?.takeIf { value -> value > 0f } ?: return map { CHART_BASELINE_RATIO }
    return map { value ->
        val normalized = 1f - (value / maxValue) * CHART_HEIGHT_RATIO
        normalized.coerceIn(CHART_TOP_PADDING_RATIO, CHART_BASELINE_RATIO)
    }
}

data class HomeTransaction(
    val icon: Int,
    val title: String,
    val subtitle: String?,
    val amount: String,
    val time: String,
    val type: TransactionType,
)

private const val CHART_TOP_PADDING_RATIO = 0.12f
private const val CHART_BASELINE_RATIO = 0.88f
private const val CHART_HEIGHT_RATIO = 0.76f

data class HomeUiState(
    val accountBalanceText: String,
    val hasBudget: Boolean,
    val budgetAmount: Double?,
    val budgetText: String?,
    val hasExpenses: Boolean,
    val expensesText: String,
    val spendFrequencyPoints: List<Float>,
    val hasSpendFrequencyData: Boolean,
    val transactions: List<HomeTransaction>,
    val selectedBottomRoute: String,
    val selectedRange: String,
)

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun HomeScreenPreview() {
    MoneyTrackTheme(darkTheme = false) {
        HomeScreen(
            uiState = HomeUiState(
                accountBalanceText = "$0",
                hasBudget = false,
                budgetAmount = null,
                budgetText = null,
                hasExpenses = false,
                expensesText = "$0",
                spendFrequencyPoints = emptyList(),
                hasSpendFrequencyData = false,
                transactions = emptyList(),
                selectedBottomRoute = ROUTE_HOME,
                selectedRange = "Today",
            ),
            onBottomRouteSelected = {},
            onSeeAllTransactionsClick = {},
            onTimeRangeSelected = {},
            onSetBudgetClick = { },
            onAddExpenseClick = {},
        )
    }
}
