@file:Suppress("MagicNumber", "LongMethod", "TooManyFunctions", "UnusedPrivateMember")

package com.moneytrack.home.presentation

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneytrack.designsystem.R
import com.moneytrack.R as AppR
import ui.components.card.transaction.TransactionCard
import ui.components.card.transaction.TransactionType
import ui.components.navigation.bottomNav.BottomNavItem
import ui.components.navigation.bottomNav.PrimaryBottomNavigation
import ui.components.navigation.button.LargeButton
import ui.components.navigation.common.SeeAllPill
import ui.components.navigation.tabs.TimeRangeTab
import ui.components.navigation.topNav.TopNavigation
import ui.components.navigation.topNav.TopNavigationConfig
import ui.components.form.input.InputField
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

private const val ROUTE_HOME = "home"
private const val ROUTE_TRANSACTION = "transaction"
private const val ROUTE_BUDGET = "budget"
private const val ROUTE_PROFILE = "profile"
private const val MAX_BUDGET_INPUT_LENGTH = 8

@Composable
fun HomeRoute() {
    val viewModel: HomeViewModel = hiltViewModel()
    val budget = viewModel.budget.collectAsStateWithLifecycle().value
    var selectedBottomRoute by remember { mutableStateOf(ROUTE_HOME) }
    var selectedRange by remember { mutableStateOf("Today") }
    var showBudgetSheet by remember { mutableStateOf(false) }

    val uiState = HomeUiState(
        accountBalance = 0.0,
        budget = budget?.amount,
        expenses = 0.0,
        transactions = emptyList(),
        selectedBottomRoute = selectedBottomRoute,
        selectedRange = selectedRange,
    )

    HomeScreen(
        uiState = uiState,
        onBottomRouteSelected = { route -> selectedBottomRoute = route },
        onTimeRangeSelected = { range -> selectedRange = range },
        onSetBudgetClick = { showBudgetSheet = true },
    )

    if (showBudgetSheet) {
        BudgetSetupBottomSheet(
            onDismiss = { showBudgetSheet = false },
            onSaveBudget = { budgetValue, description ->
                viewModel.saveBudget(
                    amount = budgetValue,
                    description = description,
                )
                showBudgetSheet = false
            },
        )
    }
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onBottomRouteSelected: (String) -> Unit,
    onTimeRangeSelected: (String) -> Unit,
    onSetBudgetClick: () -> Unit,
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
                    selectedRoute = uiState.selectedBottomRoute,
                    onItemClick = { item -> onBottomRouteSelected(item.route) },
                    onFabClick = {},
                )
            }
        },
    ) { innerPadding ->
        HomeContent(
            innerPadding = innerPadding,
            uiState = uiState,
            onTimeRangeSelected = onTimeRangeSelected,
            onSetBudgetClick = onSetBudgetClick,
        )
    }
}

@Composable
private fun HomeContent(
    innerPadding: PaddingValues,
    uiState: HomeUiState,
    onTimeRangeSelected: (String) -> Unit,
    onSetBudgetClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.spacing16),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppTheme.colors.surface),
        ) {
            TopNavigation(
                config = TopNavigationConfig.ProfileWithSelector(
                    profileImage = ColorPainter(AppTheme.colors.surfaceVariant),
                    selectedMonth = "October",
                    onMonthClick = {},
                    onActionClick = {},
                ),
            )
        }

        BalanceSummaryCard(
            accountBalance = uiState.accountBalance,
            budget = uiState.budget,
            expenses = uiState.expenses,
            onSetBudgetClick = onSetBudgetClick,
        )
        if (uiState.budget == null) {
            Spacer(modifier = Modifier.height(Dimens.spacing16))
            BudgetInfoTip(
                onSetBudgetClick = onSetBudgetClick,
            )
        }
        Spacer(modifier = Modifier.height(Dimens.spacing24))

        Text(
            text = "Spend Frequency",
            style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = AppTheme.colors.onBackground,
        )
        Spacer(modifier = Modifier.height(Dimens.spacing16))
        SpendFrequencyChart()
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
                style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.onBackground,
            )
            Spacer(modifier = Modifier.weight(1f))
            SeeAllPill(onClick = {})
        }
        Spacer(modifier = Modifier.height(Dimens.spacing12))

        if (uiState.transactions.isEmpty()) {
            Text(
                text = stringResource(id = AppR.string.home_empty_transactions),
                style = AppTheme.typography.bodyMedium,
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
        }
    }
}

@Composable
private fun BalanceSummaryCard(
    accountBalance: Double,
    budget: Double?,
    expenses: Double,
    onSetBudgetClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radius24),
        color = AppTheme.colors.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacing20),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Account Balance",
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(Dimens.spacing8))
            Text(
                text = "$${accountBalance.toInt()}",
                style = AppTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.onBackground,
            )
            Spacer(modifier = Modifier.height(Dimens.spacing20))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing16),
            ) {
                if (budget == null) {
                    MissingBudgetCard(
                        modifier = Modifier.weight(1f),
                        onClick = onSetBudgetClick,
                    )
                } else {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = stringResource(id = AppR.string.home_budget_label),
                        value = "$${budget.toInt()}",
                        icon = R.drawable.line_chart_2,
                        background = AppTheme.colors.primary,
                    )
                }
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = stringResource(id = AppR.string.home_expenses_label),
                    value = "$${expenses.toInt()}",
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
) {
    Surface(
        modifier = modifier,
        color = background,
        shape = RoundedCornerShape(Dimens.radius24),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacing12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.iconContainerSize)
                    .background(
                        color = AppTheme.colors.onPrimary,
                        shape = RoundedCornerShape(Dimens.radius16),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = icon),
                    contentDescription = null,
                    tint = background,
                )
            }
            Spacer(modifier = Modifier.width(Dimens.spacing8))
            Column {
                Text(
                    text = label,
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.onPrimary,
                )
                Text(
                    text = value,
                    style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = AppTheme.colors.onPrimary,
                )
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
            .border(
                width = Dimens.borderThick,
                color = AppTheme.colors.primary.copy(alpha = 0.4f),
                shape = RoundedCornerShape(Dimens.radius24),
            )
            .clickable(onClick = onClick),
        color = AppTheme.colors.surfaceVariant,
        shape = RoundedCornerShape(Dimens.radius24),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacing12),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing8),
        ) {
            Text(
                text = stringResource(id = AppR.string.home_budget_label),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.onSurface,
            )
            Text(
                text = stringResource(id = AppR.string.home_budget_not_set),
                style = AppTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.primary,
            )
            Text(
                text = stringResource(id = AppR.string.home_set_budget_cta),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun BudgetInfoTip(
    onSetBudgetClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = AppTheme.colors.surfaceVariant,
        shape = RoundedCornerShape(Dimens.radius16),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacing16),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing8),
        ) {
            Text(
                text = stringResource(id = AppR.string.home_budget_tip_title),
                style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.onSurface,
            )
            Text(
                text = stringResource(id = AppR.string.home_budget_tip_desc),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.onSurfaceVariant,
            )
            Text(
                text = stringResource(id = AppR.string.home_budget_tip_disadvantage),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.error,
            )
            Text(
                text = stringResource(id = AppR.string.home_set_budget_cta),
                style = AppTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.primary,
                modifier = Modifier.clickable(onClick = onSetBudgetClick),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BudgetSetupBottomSheet(
    onDismiss: () -> Unit,
    onSaveBudget: (Double, String?) -> Unit,
) {
    var budgetInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }
    val parsedBudget = budgetInput.toDoubleOrNull()
    val canContinue = parsedBudget != null && parsedBudget > 0.0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AppTheme.colors.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacing24)
                .padding(bottom = Dimens.spacing24),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing16),
        ) {
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
                        style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = AppTheme.colors.onPrimary,
                    )
                    Spacer(modifier = Modifier.height(Dimens.spacing8))
                    Text(
                        text = "$${parsedBudget?.toInt() ?: 0}",
                        style = AppTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
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

            InputField(
                value = descriptionInput,
                onValueChange = { descriptionInput = it },
                placeholder = stringResource(id = AppR.string.home_budget_sheet_description_hint),
                leadingIcon = ImageVector.vectorResource(id = R.drawable.document),
            )

            LargeButton(
                text = stringResource(id = AppR.string.home_budget_sheet_continue),
                onClick = {
                    parsedBudget?.let { budgetValue ->
                        onSaveBudget(
                            budgetValue,
                            descriptionInput,
                        )
                    }
                },
                enabled = canContinue,
            )
        }
    }
}

@Composable
private fun SpendFrequencyChart() {
    val chartLineColor = AppTheme.colors.primary
    val chartFillColor = AppTheme.colors.primary.copy(alpha = 0.15f)
    val chartGridColor = AppTheme.colors.outline.copy(alpha = 0.15f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .background(AppTheme.colors.background),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val points = listOf(
                Offset(0f, size.height * 0.75f),
                Offset(size.width * 0.12f, size.height * 0.68f),
                Offset(size.width * 0.22f, size.height * 0.66f),
                Offset(size.width * 0.34f, size.height * 0.86f),
                Offset(size.width * 0.46f, size.height * 0.56f),
                Offset(size.width * 0.58f, size.height * 0.70f),
                Offset(size.width * 0.70f, size.height * 0.45f),
                Offset(size.width * 0.82f, size.height * 0.18f),
                Offset(size.width * 0.92f, size.height * 0.60f),
                Offset(size.width, size.height * 0.70f),
            )

            val linePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (index in 1 until points.size) {
                    val prev = points[index - 1]
                    val current = points[index]
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

data class HomeTransaction(
    val icon: Int,
    val title: String,
    val subtitle: String,
    val amount: String,
    val time: String,
    val type: TransactionType,
)

data class HomeUiState(
    val accountBalance: Double,
    val budget: Double?,
    val expenses: Double,
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
                accountBalance = 0.0,
                budget = null,
                expenses = 0.0,
                transactions = emptyList(),
                selectedBottomRoute = ROUTE_HOME,
                selectedRange = "Today",
            ),
            onBottomRouteSelected = {},
            onTimeRangeSelected = {},
            onSetBudgetClick = {},
        )
    }
}
