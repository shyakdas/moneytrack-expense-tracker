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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneytrack.designsystem.R
import com.moneytrack.common.ui.LottieAnimationView
import com.moneytrack.R as AppR
import kotlinx.coroutines.delay
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
fun HomeRoute() {
    val viewModel: HomeViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val budget = viewModel.budget.collectAsStateWithLifecycle().value
    val isBudgetLoaded = viewModel.isBudgetLoaded.collectAsStateWithLifecycle().value
    var showBudgetSheet by remember { mutableStateOf(false) }
    var hasShownInitialBudgetPrompt by remember { mutableStateOf(false) }

    SheetBlurHost(isSheetVisible = showBudgetSheet) {
        HomeScreen(
            uiState = uiState,
            onBottomRouteSelected = viewModel::onBottomRouteSelected,
            onTimeRangeSelected = viewModel::onTimeRangeSelected,
            onSetBudgetClick = { showBudgetSheet = true },
        )
    }

    LaunchedEffect(isBudgetLoaded, budget) {
        if (isBudgetLoaded && budget == null && !hasShownInitialBudgetPrompt) {
            showBudgetSheet = true
            hasShownInitialBudgetPrompt = true
        }
    }

    if (showBudgetSheet) {
        BudgetSetupBottomSheet(
            onDismiss = { showBudgetSheet = false },
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
            .verticalScroll(rememberScrollState()),
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
        Spacer(modifier = Modifier.height(Dimens.spacing12))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacing16),
        ) {
            BalanceSummaryCard(
                accountBalanceText = uiState.accountBalanceText,
                hasBudget = uiState.hasBudget,
                budgetText = uiState.budgetText,
                expensesText = uiState.expensesText,
                onSetBudgetClick = onSetBudgetClick,
            )
            Spacer(modifier = Modifier.height(Dimens.spacing24))

            Text(
                text = "Spend Frequency",
                style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.onBackground,
            )
            Spacer(modifier = Modifier.height(Dimens.spacing16))
            if (uiState.hasExpenses) {
                SpendFrequencyChart()
                Spacer(modifier = Modifier.height(Dimens.spacing16))

                TimeRangeTab(
                    options = listOf("Today", "Week", "Month", "Year"),
                    selectedOption = uiState.selectedRange,
                    onOptionSelected = onTimeRangeSelected,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.radius16),
                    color = AppTheme.colors.surfaceVariant,
                ) {
                    Text(
                        text = stringResource(id = AppR.string.home_spend_frequency_empty),
                        modifier = Modifier.padding(Dimens.spacing16),
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.onSurfaceVariant,
                    )
                }
            }
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

@Composable
private fun BalanceSummaryCard(
    accountBalanceText: String,
    hasBudget: Boolean,
    budgetText: String?,
    expensesText: String,
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
                text = accountBalanceText,
                style = AppTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.onBackground,
            )
            Spacer(modifier = Modifier.height(Dimens.spacing20))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing16),
            ) {
                if (!hasBudget) {
                    MissingBudgetCard(
                        modifier = Modifier.weight(1f),
                        onClick = onSetBudgetClick,
                    )
                } else {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = stringResource(id = AppR.string.home_budget_label),
                        value = budgetText.orEmpty(),
                        icon = R.drawable.line_chart_2,
                        background = AppTheme.colors.primary,
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
) {
    Surface(
        modifier = modifier.heightIn(min = SUMMARY_CARD_MIN_HEIGHT),
        color = background,
        shape = RoundedCornerShape(Dimens.radius24),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BudgetSetupBottomSheet(
    onDismiss: () -> Unit,
    formatAmount: (Double) -> String,
    onSaveBudget: (Double, String?) -> Unit,
    onSavedCompleted: () -> Unit,
) {
    var budgetInput by remember { mutableStateOf("") }
    var sheetStep by remember { mutableStateOf(BudgetSheetStep.PRIVACY) }
    val parsedBudget = budgetInput.toDoubleOrNull()
    val canContinue = parsedBudget != null && parsedBudget > 0.0

    LaunchedEffect(sheetStep) {
        if (sheetStep == BudgetSheetStep.SUCCESS) {
            delay(BUDGET_SUCCESS_DISMISS_DELAY_MS)
            onSavedCompleted()
        }
    }

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
            when (sheetStep) {
                BudgetSheetStep.PRIVACY -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radius16),
                        color = AppTheme.colors.surfaceVariant,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimens.spacing16),
                            verticalArrangement = Arrangement.spacedBy(Dimens.spacing8),
                        ) {
                            Text(
                                text = stringResource(id = AppR.string.home_budget_privacy_title),
                                style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = AppTheme.colors.onSurface,
                            )
                            Text(
                                text = stringResource(id = AppR.string.home_budget_privacy_desc),
                                style = AppTheme.typography.bodySmall,
                                color = AppTheme.colors.onSurfaceVariant,
                            )
                            Text(
                                text = stringResource(id = AppR.string.home_budget_need_title),
                                style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
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
                                style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = AppTheme.colors.onPrimary,
                            )
                            Spacer(modifier = Modifier.height(Dimens.spacing8))
                            Text(
                                text = formatAmount(parsedBudget ?: 0.0),
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
                            style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = AppTheme.colors.onSurface,
                        )
                    }
                }
            }
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
    val accountBalanceText: String,
    val hasBudget: Boolean,
    val budgetText: String?,
    val hasExpenses: Boolean,
    val expensesText: String,
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
                budgetText = null,
                hasExpenses = false,
                expensesText = "$0",
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
