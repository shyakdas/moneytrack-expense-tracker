// Copyright (c) 2026 shyakdas

@file:Suppress("MagicNumber", "LongMethod", "TooManyFunctions", "UnusedPrivateMember", "LongParameterList")

package com.moneytrack.home.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.moneytrack.R as AppR
import com.moneytrack.common.ui.LottieAnimationView
import com.moneytrack.designsystem.R
import ui.components.card.transaction.TransactionCard
import ui.components.navigation.bottomNav.BottomNavItem
import ui.components.navigation.bottomNav.PrimaryBottomNavigation
import ui.components.navigation.common.SeeAllPill
import ui.components.navigation.tabs.TimeRangeTab
import ui.components.navigation.topNav.TopNavigation
import ui.components.navigation.topNav.TopNavigationConfig
import ui.components.surface.MoneyTrackScreenBackground
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    isBudgetLoaded: Boolean = true,
    onBottomRouteSelected: (String) -> Unit,
    onSeeAllTransactionsClick: () -> Unit,
    onExpensesClick: () -> Unit,
    onDeleteTransaction: (Long) -> Unit,
    onTimeRangeSelected: (String) -> Unit,
    onMonthSelected: (HomeMonthOption) -> Unit = {},
    onYearSelected: (Int) -> Unit = {},
    onSetBudgetClick: (Double?) -> Unit,
    onAddExpenseClick: () -> Unit = {},
) {
    val bottomItems = remember {
        listOf(
            BottomNavItem(ROUTE_HOME, R.drawable.home, "Home"),
            BottomNavItem(ROUTE_TRANSACTION, R.drawable.transaction, "Transaction"),
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
            onExpensesClick = onExpensesClick,
            onDeleteTransaction = onDeleteTransaction,
            onTimeRangeSelected = onTimeRangeSelected,
            onMonthSelected = onMonthSelected,
            onYearSelected = onYearSelected,
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
    onExpensesClick: () -> Unit,
    onDeleteTransaction: (Long) -> Unit,
    onTimeRangeSelected: (String) -> Unit,
    onMonthSelected: (HomeMonthOption) -> Unit,
    onYearSelected: (Int) -> Unit,
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
        var isMonthPickerVisible by remember { mutableStateOf(false) }
        var isYearPickerVisible by remember { mutableStateOf(false) }
        val monthPickerTransition = remember {
            MutableTransitionState(false)
        }
        monthPickerTransition.targetState = isMonthPickerVisible
        val yearPickerTransition = remember {
            MutableTransitionState(false)
        }
        yearPickerTransition.targetState = isYearPickerVisible
        val density = LocalDensity.current
        val monthPopupYOffset = with(density) {
            Dimens.buttonLLargeHeight.roundToPx()
        }
        val monthPopupXOffset = with(density) {
            -YEAR_MONTH_POPUP_X_DISTANCE.roundToPx()
        }
        val yearPopupXOffset = with(density) {
            YEAR_MONTH_POPUP_X_DISTANCE.roundToPx()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
        Box(modifier = Modifier.fillMaxWidth()) {
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
                    selectedMonth = uiState.selectedMonth.shortLabel.ifBlank { uiState.selectedMonth.label },
                    onMonthClick = {
                        isMonthPickerVisible = !isMonthPickerVisible
                        isYearPickerVisible = false
                    },
                    selectedYear = uiState.selectedMonth.year.toString(),
                    onYearClick = {
                        isYearPickerVisible = !isYearPickerVisible
                        isMonthPickerVisible = false
                    },
                    onActionClick = {},
                    actionIconTint = AppTheme.colors.primary,
                ),
                containerColor = Color.Transparent,
            )
            if (monthPickerTransition.currentState || monthPickerTransition.targetState) {
                Popup(
                    alignment = Alignment.TopCenter,
                    offset = IntOffset(x = monthPopupXOffset, y = monthPopupYOffset),
                    onDismissRequest = { isMonthPickerVisible = false },
                    properties = PopupProperties(focusable = true),
                ) {
                    AnimatedPickerVisibility(visibleState = monthPickerTransition) {
                        MonthSelectorPopup(
                            months = uiState.monthOptions,
                            selectedMonth = uiState.selectedMonth,
                            onMonthSelected = { month ->
                                onMonthSelected(month)
                                isMonthPickerVisible = false
                            },
                        )
                    }
                }
            }
            if (yearPickerTransition.currentState || yearPickerTransition.targetState) {
                Popup(
                    alignment = Alignment.TopCenter,
                    offset = IntOffset(x = yearPopupXOffset, y = monthPopupYOffset),
                    onDismissRequest = { isYearPickerVisible = false },
                    properties = PopupProperties(focusable = true),
                ) {
                    AnimatedPickerVisibility(visibleState = yearPickerTransition) {
                        YearSelectorPopup(
                            years = uiState.yearOptions,
                            selectedYear = uiState.selectedMonth.year,
                            onYearSelected = { year ->
                                onYearSelected(year)
                                isYearPickerVisible = false
                            },
                        )
                    }
                }
            }
        }
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
                expensesAmount = uiState.expensesAmount,
                expensesText = uiState.expensesText,
                onSetBudgetClick = onSetBudgetClick,
                onExpensesClick = onExpensesClick,
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
                    key(transaction.id) {
                        SwipeToDeleteTransactionCard(
                            onDelete = { onDeleteTransaction(transaction.id) },
                        ) {
                            TransactionCard(
                                icon = ImageVector.vectorResource(id = transaction.icon),
                                category = transaction.category,
                                title = transaction.title,
                                subtitle = transaction.subtitle,
                                amount = transaction.amount,
                                date = transaction.date,
                                time = transaction.time,
                                type = transaction.type,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(Dimens.spacing12))
                }
                Spacer(modifier = Modifier.height(Dimens.spacing12))
            }
            }
        }
    }
}

@Composable
private fun AnimatedPickerVisibility(
    visibleState: MutableTransitionState<Boolean>,
    content: @Composable () -> Unit,
) {
    androidx.compose.animation.AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = MONTH_POPUP_ENTER_DURATION_MS,
                easing = FastOutSlowInEasing,
            ),
        ) +
            expandVertically(
                animationSpec = tween(
                    durationMillis = MONTH_POPUP_ENTER_DURATION_MS,
                    easing = FastOutSlowInEasing,
                ),
                expandFrom = Alignment.Top,
            ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = MONTH_POPUP_EXIT_DURATION_MS,
                easing = FastOutSlowInEasing,
            ),
        ) +
            shrinkVertically(
                animationSpec = tween(
                    durationMillis = MONTH_POPUP_EXIT_DURATION_MS,
                    easing = FastOutSlowInEasing,
                ),
                shrinkTowards = Alignment.Top,
            ),
    ) {
        content()
    }
}

private const val MONTH_POPUP_ENTER_DURATION_MS = 260
private const val MONTH_POPUP_EXIT_DURATION_MS = 220
private val YEAR_MONTH_POPUP_X_DISTANCE = Dimens.spacing48

@Composable
private fun SwipeToDeleteTransactionCard(
    onDelete: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    var deleteHandled by remember { mutableStateOf(false) }
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            value == SwipeToDismissBoxValue.EndToStart
        },
    )
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart && !deleteHandled) {
            deleteHandled = true
            onDelete()
            dismissState.reset()
            deleteHandled = false
        }
    }
    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                AppTheme.colors.error.copy(alpha = 0.2f),
                                AppTheme.colors.error.copy(alpha = 0.45f),
                            ),
                        ),
                        shape = RoundedCornerShape(Dimens.radius16),
                    )
                    .padding(horizontal = Dimens.spacing16),
                contentAlignment = Alignment.CenterEnd,
            ) {
                androidx.compose.material3.Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.trash),
                    contentDescription = null,
                    tint = AppTheme.colors.onPrimary,
                )
            }
        },
        content = content,
    )
}

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
                expensesAmount = 0.0,
                expensesText = "$0",
                spendFrequencyPoints = emptyList(),
                hasSpendFrequencyData = false,
                transactions = emptyList(),
                selectedBottomRoute = ROUTE_HOME,
                selectedRange = "Today",
                selectedMonth = currentHomeMonthOption(),
                monthOptions = homeMonthOptions(),
                yearOptions = homeYearOptions(),
            ),
            onBottomRouteSelected = {},
            onSeeAllTransactionsClick = {},
            onExpensesClick = {},
            onDeleteTransaction = {},
            onTimeRangeSelected = {},
            onSetBudgetClick = { },
            onAddExpenseClick = {},
        )
    }
}
