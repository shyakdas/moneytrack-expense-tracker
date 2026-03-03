@file:Suppress("MagicNumber", "LongMethod", "TooManyFunctions", "UnusedPrivateMember")

package com.moneytrack.home.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
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
import com.moneytrack.designsystem.R
import ui.components.card.transaction.TransactionCard
import ui.components.card.transaction.TransactionType
import ui.components.navigation.bottomNav.BottomNavItem
import ui.components.navigation.bottomNav.PrimaryBottomNavigation
import ui.components.navigation.common.SeeAllPill
import ui.components.navigation.tabs.TimeRangeTab
import ui.components.navigation.topNav.TopNavigation
import ui.components.navigation.topNav.TopNavigationConfig
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

private const val ROUTE_HOME = "home"
private const val ROUTE_TRANSACTION = "transaction"
private const val ROUTE_BUDGET = "budget"
private const val ROUTE_PROFILE = "profile"

private val incomeGreen = Color(0xFF12A86B)

@Composable
fun HomeRoute() {
    var selectedBottomRoute by remember { mutableStateOf(ROUTE_HOME) }
    var selectedRange by remember { mutableStateOf("Today") }

    HomeScreen(
        selectedBottomRoute = selectedBottomRoute,
        selectedRange = selectedRange,
        onBottomRouteSelected = { route -> selectedBottomRoute = route },
        onTimeRangeSelected = { range -> selectedRange = range },
    )
}

@Composable
fun HomeScreen(
    selectedBottomRoute: String,
    selectedRange: String,
    onBottomRouteSelected: (String) -> Unit,
    onTimeRangeSelected: (String) -> Unit,
) {
    val bottomItems = remember {
        listOf(
            BottomNavItem(ROUTE_HOME, R.drawable.home, "Home"),
            BottomNavItem(ROUTE_TRANSACTION, R.drawable.transaction, "Transaction"),
            BottomNavItem(ROUTE_BUDGET, R.drawable.line_chart_2, "Budget"),
            BottomNavItem(ROUTE_PROFILE, R.drawable.user, "Profile"),
        )
    }
    val transactions = remember {
        listOf(
            HomeTransaction(
                icon = R.drawable.shopping_bag,
                title = "Shopping",
                subtitle = "Buy some grocery",
                amount = "- $120",
                time = "10:00 AM",
                type = TransactionType.EXPENSE,
            ),
            HomeTransaction(
                icon = R.drawable.document,
                title = "Subscription",
                subtitle = "Disney+ Annual..",
                amount = "- $80",
                time = "03:30 PM",
                type = TransactionType.EXPENSE,
            ),
            HomeTransaction(
                icon = R.drawable.restaurant,
                title = "Food",
                subtitle = "Buy a ramen",
                amount = "- $32",
                time = "07:30 PM",
                type = TransactionType.EXPENSE,
            ),
        )
    }

    Scaffold(
        containerColor = AppTheme.colors.background,
        bottomBar = {
            Box(modifier = Modifier.navigationBarsPadding()) {
                PrimaryBottomNavigation(
                    items = bottomItems,
                    selectedRoute = selectedBottomRoute,
                    onItemClick = { item -> onBottomRouteSelected(item.route) },
                    onFabClick = {},
                )
            }
        },
    ) { innerPadding ->
        HomeContent(
            innerPadding = innerPadding,
            selectedRange = selectedRange,
            onTimeRangeSelected = onTimeRangeSelected,
            transactions = transactions,
        )
    }
}

@Composable
private fun HomeContent(
    innerPadding: PaddingValues,
    selectedRange: String,
    onTimeRangeSelected: (String) -> Unit,
    transactions: List<HomeTransaction>,
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

        BalanceSummaryCard()
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
            selectedOption = selectedRange,
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

        transactions.forEach { transaction ->
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

@Composable
private fun BalanceSummaryCard() {
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
                text = "$9400",
                style = AppTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.onBackground,
            )
            Spacer(modifier = Modifier.height(Dimens.spacing20))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing16),
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Income",
                    value = "$5000",
                    icon = R.drawable.income,
                    background = incomeGreen,
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Expenses",
                    value = "$1200",
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

private data class HomeTransaction(
    val icon: Int,
    val title: String,
    val subtitle: String,
    val amount: String,
    val time: String,
    val type: TransactionType,
)

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun HomeScreenPreview() {
    MoneyTrackTheme(darkTheme = false) {
        HomeScreen(
            selectedBottomRoute = ROUTE_HOME,
            selectedRange = "Today",
            onBottomRouteSelected = {},
            onTimeRangeSelected = {},
        )
    }
}
