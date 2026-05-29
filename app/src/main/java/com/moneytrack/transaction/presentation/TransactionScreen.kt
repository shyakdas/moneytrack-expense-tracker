// Copyright (c) 2026 shyakdas

@file:Suppress("LongMethod", "UnusedPrivateMember")

package com.moneytrack.transaction.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneytrack.R
import com.moneytrack.designsystem.R as DsR
import ui.components.card.transaction.TransactionCard
import ui.components.navigation.bottomNav.BottomNavItem
import ui.components.navigation.bottomNav.PrimaryBottomNavigation
import ui.components.navigation.common.SelectorChip
import ui.components.surface.MoneyTrackScreenBackground
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme
import kotlin.math.roundToInt

private const val ROUTE_HOME = "home"
private const val ROUTE_TRANSACTION = "transaction"
private const val ROUTE_PROFILE = "profile"
private const val MONTH_POPUP_ENTER_DURATION_MS = 260
private const val MONTH_POPUP_EXIT_DURATION_MS = 220
@Composable
fun TransactionRoute(
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    onEditExpenseClick: (Long) -> Unit = {},
) {
    val viewModel: TransactionViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    TransactionScreen(
        uiState = uiState,
        onBottomRouteClick = { route ->
            when (route) {
                ROUTE_HOME -> onHomeClick()
                ROUTE_PROFILE -> onProfileClick()
                else -> Unit
            }
        },
        onAddExpenseClick = onAddExpenseClick,
        onTransactionCardClick = onEditExpenseClick,
        onDeleteTransaction = viewModel::deleteTransaction,
        onMonthSelected = viewModel::onMonthSelected,
        onYearSelected = viewModel::onYearSelected,
        onSortOptionSelected = viewModel::onSortOptionSelected,
        onCategorySelected = viewModel::onCategorySelected,
    )
}

@Composable
fun TransactionScreen(
    uiState: TransactionUiState,
    onBottomRouteClick: (String) -> Unit,
    onAddExpenseClick: () -> Unit,
    onTransactionCardClick: (Long) -> Unit,
    onDeleteTransaction: (Long) -> Unit,
    onMonthSelected: (TransactionMonthOption) -> Unit,
    onYearSelected: (Int) -> Unit,
    onSortOptionSelected: (TransactionSortOption) -> Unit,
    onCategorySelected: (String) -> Unit,
) {
    val bottomItems = remember {
        listOf(
            BottomNavItem(ROUTE_HOME, DsR.drawable.home, "Home"),
            BottomNavItem(ROUTE_TRANSACTION, DsR.drawable.transaction, "Transaction"),
            BottomNavItem(ROUTE_PROFILE, DsR.drawable.user, "Profile"),
        )
    }

    Scaffold(
        containerColor = AppTheme.colors.background,
        bottomBar = {
            Box(modifier = Modifier.navigationBarsPadding()) {
                PrimaryBottomNavigation(
                    items = bottomItems,
                    selectedRoute = ROUTE_TRANSACTION,
                    onItemClick = { item -> onBottomRouteClick(item.route) },
                    onFabClick = onAddExpenseClick,
                )
            }
        },
    ) { innerPadding ->
        TransactionContent(
            uiState = uiState,
            innerPadding = innerPadding,
            onTransactionCardClick = onTransactionCardClick,
            onDeleteTransaction = onDeleteTransaction,
            onMonthSelected = onMonthSelected,
            onYearSelected = onYearSelected,
            onSortOptionSelected = onSortOptionSelected,
            onCategorySelected = onCategorySelected,
        )
    }
}

@Composable
private fun TransactionContent(
    uiState: TransactionUiState,
    innerPadding: PaddingValues,
    onTransactionCardClick: (Long) -> Unit,
    onDeleteTransaction: (Long) -> Unit,
    onMonthSelected: (TransactionMonthOption) -> Unit,
    onYearSelected: (Int) -> Unit,
    onSortOptionSelected: (TransactionSortOption) -> Unit,
    onCategorySelected: (String) -> Unit,
) {
    MoneyTrackScreenBackground {
        var isMonthPickerVisible by remember { mutableStateOf(false) }
        var isYearPickerVisible by remember { mutableStateOf(false) }
        var pendingSortOption by remember { mutableStateOf(uiState.selectedSortOption) }
        var pendingCategory by remember { mutableStateOf(uiState.selectedCategory) }
        var monthAnchorX by remember { mutableIntStateOf(0) }
        var yearAnchorX by remember { mutableIntStateOf(0) }
        var sortAnchorX by remember { mutableIntStateOf(0) }
        var popupAnchorY by remember { mutableIntStateOf(0) }
        var isSortOverlayVisible by remember { mutableStateOf(false) }
        val monthPickerTransition = remember { MutableTransitionState(false) }
        monthPickerTransition.targetState = isMonthPickerVisible
        val yearPickerTransition = remember { MutableTransitionState(false) }
        yearPickerTransition.targetState = isYearPickerVisible
        val density = LocalDensity.current
        val monthPopupYOffset = if (popupAnchorY == 0) {
            with(density) { Dimens.buttonLLargeHeight.roundToPx() }
        } else {
            popupAnchorY
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Dimens.spacing16),
            contentPadding = PaddingValues(bottom = Dimens.spacing24),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing16),
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    TransactionMonthYearHeader(
                        selectedMonth = uiState.selectedMonth.shortLabel.ifBlank { uiState.selectedMonth.label },
                        selectedYear = uiState.selectedMonth.year.toString(),
                        onMonthAnchorChanged = { monthX, anchorY ->
                            monthAnchorX = monthX
                            popupAnchorY = anchorY
                        },
                        onYearAnchorChanged = { yearX, anchorY ->
                            yearAnchorX = yearX
                            popupAnchorY = anchorY
                        },
                        onSortAnchorChanged = { sortX, anchorY ->
                            sortAnchorX = sortX
                            popupAnchorY = anchorY
                        },
                        onMonthClick = {
                            isMonthPickerVisible = !isMonthPickerVisible
                            isYearPickerVisible = false
                            isSortOverlayVisible = false
                        },
                        onYearClick = {
                            isYearPickerVisible = !isYearPickerVisible
                            isMonthPickerVisible = false
                            isSortOverlayVisible = false
                        },
                        onSortClick = {
                            isSortOverlayVisible = !isSortOverlayVisible
                            isMonthPickerVisible = false
                            isYearPickerVisible = false
                            if (isSortOverlayVisible) {
                                pendingSortOption = uiState.selectedSortOption
                                pendingCategory = uiState.selectedCategory
                            }
                        },
                    )
                    if (monthPickerTransition.currentState || monthPickerTransition.targetState) {
                        Popup(
                            alignment = Alignment.TopStart,
                            offset = IntOffset(x = monthAnchorX, y = monthPopupYOffset),
                            onDismissRequest = { isMonthPickerVisible = false },
                            properties = PopupProperties(focusable = true),
                        ) {
                            AnimatedPickerVisibility(visibleState = monthPickerTransition) {
                                TransactionMonthSelectorPopup(
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
                            alignment = Alignment.TopStart,
                            offset = IntOffset(x = yearAnchorX, y = monthPopupYOffset),
                            onDismissRequest = { isYearPickerVisible = false },
                            properties = PopupProperties(focusable = true),
                        ) {
                            AnimatedPickerVisibility(visibleState = yearPickerTransition) {
                                TransactionYearSelectorPopup(
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
                    if (isSortOverlayVisible) {
                        Popup(
                            alignment = Alignment.TopStart,
                            offset = IntOffset(x = sortAnchorX - 220, y = monthPopupYOffset),
                            onDismissRequest = { isSortOverlayVisible = false },
                            properties = PopupProperties(focusable = true),
                        ) {
                            SortFilterOverlay(
                                selectedSortOption = pendingSortOption,
                                sortOptions = TransactionSortOption.entries,
                                selectedCategory = pendingCategory,
                                categoryOptions = uiState.categoryOptions,
                                onSortOptionSelected = {
                                    pendingSortOption = it
                                },
                                onCategorySelected = {
                                    pendingCategory = it
                                },
                                onApplyClick = {
                                    onSortOptionSelected(pendingSortOption)
                                    onCategorySelected(pendingCategory)
                                    isSortOverlayVisible = false
                                },
                            )
                        }
                    }
                }
            }

            if (uiState.sections.isEmpty()) {
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 }),
                        exit = fadeOut(),
                    ) {
                        EmptyTransactionState()
                    }
                }
            } else {
                items(uiState.sections, key = { section -> section.title }) { section ->
                    Column(
                        modifier = Modifier.animateItem(),
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacing12),
                    ) {
                        Text(
                            text = section.title,
                            style = AppTheme.typography.titleMedium,
                            color = AppTheme.colors.onBackground,
                        )
                        section.items.forEach { transaction ->
                            key(transaction.id) {
                                SwipeToDeleteTransactionCard(
                                    onDelete = { onDeleteTransaction(transaction.id) },
                                ) {
                                    TransactionCard(
                                        icon = ImageVector.vectorResource(id = transaction.iconRes),
                                        category = transaction.category,
                                        title = transaction.title,
                                        subtitle = transaction.subtitle,
                                        amount = transaction.amount,
                                        date = transaction.date,
                                        time = transaction.time,
                                        type = transaction.type,
                                        modifier = Modifier.clickable {
                                            val isExpense = transaction.type ==
                                                ui.components.card.transaction.TransactionType.EXPENSE
                                            if (isExpense) {
                                                onTransactionCardClick(transaction.id)
                                            }
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionMonthYearHeader(
    selectedMonth: String,
    selectedYear: String,
    onMonthAnchorChanged: (Int, Int) -> Unit,
    onYearAnchorChanged: (Int, Int) -> Unit,
    onSortAnchorChanged: (Int, Int) -> Unit,
    onMonthClick: () -> Unit,
    onYearClick: () -> Unit,
    onSortClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimens.spacing8),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8)) {
            SelectorChip(
                label = selectedMonth,
                selected = false,
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    val parentCoordinates = coordinates.parentLayoutCoordinates ?: return@onGloballyPositioned
                    val position = parentCoordinates.localPositionOf(coordinates, Offset.Zero)
                    onMonthAnchorChanged(
                        position.x.roundToInt(),
                        (position.y + coordinates.size.height).roundToInt(),
                    )
                },
                onClick = onMonthClick,
                leadingIcon = ImageVector.vectorResource(id = DsR.drawable.arrow_down_2),
                highlighted = true,
            )
            SelectorChip(
                label = selectedYear,
                selected = false,
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    val parentCoordinates = coordinates.parentLayoutCoordinates ?: return@onGloballyPositioned
                    val position = parentCoordinates.localPositionOf(coordinates, Offset.Zero)
                    onYearAnchorChanged(
                        position.x.roundToInt(),
                        (position.y + coordinates.size.height).roundToInt(),
                    )
                },
                onClick = onYearClick,
                leadingIcon = ImageVector.vectorResource(id = DsR.drawable.arrow_down_2),
                highlighted = true,
            )
        }
        Surface(
            modifier = Modifier
                .size(Dimens.iconButtonSize)
                .onGloballyPositioned { coordinates ->
                    val parentCoordinates = coordinates.parentLayoutCoordinates ?: return@onGloballyPositioned
                    val position = parentCoordinates.localPositionOf(coordinates, Offset.Zero)
                    onSortAnchorChanged(
                        position.x.roundToInt(),
                        (position.y + coordinates.size.height).roundToInt(),
                    )
                }
                .clickable(onClick = onSortClick),
            shape = RoundedCornerShape(Dimens.radius16),
            color = AppTheme.colors.surface,
            tonalElevation = Dimens.elevation2,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = DsR.drawable.sort),
                    contentDescription = null,
                    tint = AppTheme.colors.onSurface,
                    modifier = Modifier.size(Dimens.icon20),
                )
            }
        }
    }
}

@Composable
private fun SortFilterOverlay(
    selectedSortOption: TransactionSortOption,
    sortOptions: List<TransactionSortOption>,
    selectedCategory: String,
    categoryOptions: List<String>,
    onSortOptionSelected: (TransactionSortOption) -> Unit,
    onCategorySelected: (String) -> Unit,
    onApplyClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(Dimens.radius20),
        color = AppTheme.colors.surface,
        tonalElevation = Dimens.elevation4,
        modifier = Modifier
            .widthIn(min = 250.dp, max = 320.dp)
            .border(
                width = Dimens.spacing1,
                color = AppTheme.colors.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(Dimens.radius20),
            ),
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacing12),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing8),
        ) {
            Text(
                text = "Sort By",
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colors.onSurface,
            )
            sortOptions.forEach { option ->
                FilterOptionRow(
                    text = option.label,
                    selected = selectedSortOption == option,
                    onClick = { onSortOptionSelected(option) },
                )
            }
            HorizontalDivider(color = AppTheme.colors.outline.copy(alpha = 0.35f))
            Text(
                text = "Category",
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colors.onSurface,
            )
            Column(
                modifier = Modifier.heightIn(max = 220.dp),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacing6),
            ) {
                categoryOptions.forEach { category ->
                    FilterOptionRow(
                        text = category,
                        selected = selectedCategory == category,
                        onClick = { onCategorySelected(category) },
                    )
                }
            }
            Spacer(modifier = Modifier.height(Dimens.spacing4))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onApplyClick),
                shape = RoundedCornerShape(Dimens.radius16),
                color = AppTheme.colors.primary,
            ) {
                Box(
                    modifier = Modifier.padding(vertical = Dimens.spacing10),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Apply",
                        style = AppTheme.typography.titleSmall,
                        color = AppTheme.colors.onPrimary,
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterOptionRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (selected) AppTheme.colors.primaryContainer else Color.Transparent,
                shape = RoundedCornerShape(Dimens.radius16),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.spacing12, vertical = Dimens.spacing10),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = AppTheme.typography.bodyMedium,
            color = if (selected) AppTheme.colors.onPrimaryContainer else AppTheme.colors.onSurface,
        )
    }
}

@Composable
private fun FinancialReportBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = AppTheme.colors.primaryContainer,
        shape = RoundedCornerShape(Dimens.radius20),
        tonalElevation = Dimens.elevation2,
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing16),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = R.string.transaction_financial_report_cta),
                modifier = Modifier.weight(1f),
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.onPrimaryContainer,
            )
            Icon(
                imageVector = ImageVector.vectorResource(id = DsR.drawable.arrow_right_2),
                contentDescription = null,
                tint = AppTheme.colors.primary,
            )
        }
    }
}

@Composable
private fun EmptyTransactionState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimens.spacing32),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing12),
    ) {
        Text(
            text = stringResource(id = R.string.transaction_empty_title),
            style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = AppTheme.colors.onBackground,
        )
        Text(
            text = stringResource(id = R.string.transaction_empty_desc),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun AnimatedPickerVisibility(
    visibleState: MutableTransitionState<Boolean>,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = MONTH_POPUP_ENTER_DURATION_MS,
                easing = FastOutSlowInEasing,
            ),
        ) + expandVertically(
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
        ) + shrinkVertically(
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
                        AppTheme.colors.error.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(Dimens.radius16),
                    )
                    .padding(horizontal = Dimens.spacing16),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = DsR.drawable.trash),
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
private fun TransactionScreenPreview() {
    MoneyTrackTheme(darkTheme = false) {
        TransactionScreen(
            uiState = TransactionUiState(
                sections = listOf(
                    TransactionSectionUiState(
                        title = "Today",
                        items = listOf(
                            TransactionItemUiState(
                                id = 1L,
                                iconRes = DsR.drawable.shopping_bag,
                                category = "Shopping",
                                title = "Shopping",
                                subtitle = "Buy some grocery",
                                amount = "-\u20b9120",
                                date = "17 May 2025",
                                time = "10:00 AM",
                                type = ui.components.card.transaction.TransactionType.EXPENSE,
                            ),
                            TransactionItemUiState(
                                id = 2L,
                                iconRes = DsR.drawable.recurring_bill,
                                category = "Subscription",
                                title = "Subscription",
                                subtitle = "Disney+ Annual",
                                amount = "-\u20b980",
                                date = "17 May 2025",
                                time = "03:30 PM",
                                type = ui.components.card.transaction.TransactionType.EXPENSE,
                            ),
                        ),
                    ),
                    TransactionSectionUiState(
                        title = "Yesterday",
                        items = listOf(
                            TransactionItemUiState(
                                id = 3L,
                                iconRes = DsR.drawable.salary,
                                category = "Salary",
                                title = "Salary",
                                subtitle = "Salary for July",
                                amount = "\u20b95,000",
                                date = "16 May 2025",
                                time = "04:30 PM",
                                type = ui.components.card.transaction.TransactionType.INCOME,
                            ),
                        ),
                    ),
                ),
            ),
            onBottomRouteClick = {},
            onAddExpenseClick = {},
            onTransactionCardClick = {},
            onDeleteTransaction = {},
            onMonthSelected = {},
            onYearSelected = {},
            onSortOptionSelected = {},
            onCategorySelected = {},
        )
    }
}
