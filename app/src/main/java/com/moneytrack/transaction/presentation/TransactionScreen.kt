// Copyright (c) 2026 shyakdas

@file:Suppress("LongMethod", "UnusedPrivateMember")

package com.moneytrack.transaction.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneytrack.R
import com.moneytrack.designsystem.R as DsR
import ui.components.card.transaction.TransactionCard
import ui.components.navigation.bottomNav.BottomNavItem
import ui.components.navigation.bottomNav.PrimaryBottomNavigation
import ui.components.navigation.topNav.TopNavigation
import ui.components.navigation.topNav.TopNavigationConfig
import ui.components.surface.MoneyTrackScreenBackground
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

private const val ROUTE_HOME = "home"
private const val ROUTE_TRANSACTION = "transaction"
private const val ROUTE_PROFILE = "profile"

@Composable
fun TransactionRoute(
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
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
        onDeleteTransaction = viewModel::deleteTransaction,
    )
}

@Composable
fun TransactionScreen(
    uiState: TransactionUiState,
    onBottomRouteClick: (String) -> Unit,
    onAddExpenseClick: () -> Unit,
    onDeleteTransaction: (Long) -> Unit,
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
            onDeleteTransaction = onDeleteTransaction,
        )
    }
}

@Composable
private fun TransactionContent(
    uiState: TransactionUiState,
    innerPadding: PaddingValues,
    onDeleteTransaction: (Long) -> Unit,
) {
    MoneyTrackScreenBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Dimens.spacing16),
            contentPadding = PaddingValues(bottom = Dimens.spacing24),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing16),
        ) {
            item {
                TopNavigation(
                    config = TopNavigationConfig.DropdownWithFilter(
                        label = uiState.monthLabel,
                        showBadge = false,
                        badgeCount = 0,
                        onDropdownClick = {},
                        onFilterClick = {},
                    ),
                    containerColor = Color.Transparent,
                )
            }

            item {
                FinancialReportBanner()
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
            onDeleteTransaction = {},
        )
    }
}
