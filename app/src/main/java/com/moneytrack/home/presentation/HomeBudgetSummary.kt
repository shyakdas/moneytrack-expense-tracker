// Copyright (c) 2026 shyakdas

@file:Suppress("LongParameterList", "TooManyFunctions", "LongMethod", "MagicNumber")

package com.moneytrack.home.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moneytrack.common.ui.LottieAnimationView
import com.moneytrack.designsystem.R
import com.moneytrack.R as AppR
import ui.components.navigation.button.LargeButton
import ui.theme.AppTheme
import ui.theme.Dimens

@Composable
internal fun BudgetRequiredState(
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
internal fun BalanceSummaryCard(
    accountBalanceText: String,
    hasBudget: Boolean,
    budgetAmount: Double?,
    budgetText: String?,
    expensesAmount: Double,
    expensesText: String,
    onSetBudgetClick: (Double?) -> Unit,
    onExpensesClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radius24),
        color = AppTheme.colors.surface,
        tonalElevation = Dimens.elevation2,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(Dimens.spacing20),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing16),
        ) {
            BalanceSummaryHeader(accountBalanceText = accountBalanceText)
            BalanceStatsRow(
                hasBudget = hasBudget,
                budgetAmount = budgetAmount,
                budgetText = budgetText,
                expensesAmount = expensesAmount,
                expensesText = expensesText,
                onSetBudgetClick = onSetBudgetClick,
                onExpensesClick = onExpensesClick,
            )
        }
    }
}

@Composable
private fun BalanceSummaryHeader(accountBalanceText: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radius24),
        color = Color.Transparent,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            AppTheme.colors.primaryContainer.copy(alpha = 0.95f),
                            AppTheme.colors.surface,
                            AppTheme.colors.primaryContainer.copy(alpha = 0.7f),
                        ),
                    ),
                )
                .padding(horizontal = Dimens.spacing20, vertical = Dimens.spacing20),
        ) {
            Column(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacing8),
            ) {
                Text(
                    text = "Account Balance",
                    style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = AppTheme.colors.onSurfaceVariant,
                )
                Text(
                    text = accountBalanceText,
                    style = AppTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = dynamicAmountFontSize(accountBalanceText),
                    ),
                    color = AppTheme.colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Surface(
                modifier = Modifier.align(Alignment.CenterEnd),
                shape = RoundedCornerShape(999.dp),
                color = AppTheme.colors.surface.copy(alpha = 0.9f),
                tonalElevation = Dimens.elevation4,
            ) {
                Box(
                    modifier = Modifier.size(84.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.wallet_3),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = AppTheme.colors.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun BalanceStatsRow(
    hasBudget: Boolean,
    budgetAmount: Double?,
    budgetText: String?,
    expensesAmount: Double,
    expensesText: String,
    onSetBudgetClick: (Double?) -> Unit,
    onExpensesClick: () -> Unit,
) {
    val expenseProgress = if ((budgetAmount ?: 0.0) <= 0.0) {
        0f
    } else {
        (expensesAmount / budgetAmount.orZero()).toFloat().coerceIn(0f, 1f)
    }
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
                background = AppTheme.colors.success.copy(alpha = 0.12f),
                contentColor = AppTheme.colors.success,
                progress = 1f,
                progressLabel = "100% of budget",
                onClick = { onSetBudgetClick(budgetAmount) },
            )
        }
        StatCard(
            modifier = Modifier.weight(1f),
            label = stringResource(id = AppR.string.home_expenses_label),
            value = expensesText,
            icon = R.drawable.expense,
            background = AppTheme.colors.error.copy(alpha = 0.1f),
            contentColor = AppTheme.colors.error,
            progress = expenseProgress,
            progressLabel = "${(expenseProgress * 100).toInt()}% of budget",
            onClick = onExpensesClick,
        )
    }
}

@Composable
internal fun StatCard(
    label: String,
    value: String,
    icon: Int,
    background: Color,
    contentColor: Color,
    progress: Float,
    progressLabel: String,
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
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
        ) {
            StatCardContent(
                label = label,
                value = value,
                icon = icon,
                contentColor = contentColor,
                progress = progress,
                progressLabel = progressLabel,
                compact = maxWidth < STAT_CARD_COMPACT_WIDTH,
            )
        }
    }
}

@Composable
private fun StatCardContent(
    label: String,
    value: String,
    icon: Int,
    contentColor: Color,
    progress: Float,
    progressLabel: String,
    compact: Boolean,
) {
    val labelStyle = if (compact) AppTheme.typography.bodySmall else AppTheme.typography.bodyMedium
    val valueStyle = if (compact) {
        AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
    } else {
        AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.spacing12),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing10),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatCardIcon(
                icon = icon,
                iconSize = if (compact) Dimens.spacing36 else Dimens.iconContainerSize,
                tint = contentColor,
                background = contentColor.copy(alpha = 0.14f),
            )
            Spacer(modifier = Modifier.width(if (compact) Dimens.spacing6 else Dimens.spacing8))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = labelStyle,
                    color = contentColor,
                    maxLines = if (compact) 2 else 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = value,
                    style = valueStyle.copy(fontSize = dynamicStatAmountFontSize(value)),
                    color = AppTheme.colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    color = contentColor.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(999.dp),
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .height(8.dp)
                    .background(
                        color = contentColor,
                        shape = RoundedCornerShape(999.dp),
                    ),
            )
        }
        Text(
            text = progressLabel,
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun StatCardIcon(
    icon: Int,
    iconSize: androidx.compose.ui.unit.Dp,
    tint: Color,
    background: Color,
) {
    Box(
        modifier = Modifier
            .size(iconSize)
            .background(
                color = background,
                shape = RoundedCornerShape(Dimens.radius12),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription = null,
            tint = tint,
        )
    }
}

private fun Double?.orZero(): Double = this ?: 0.0

private fun dynamicAmountFontSize(value: String): TextUnit {
    val length = value.length
    return when {
        length >= 18 -> 15.sp
        length >= 16 -> 16.sp
        length >= 14 -> 17.sp
        length >= 12 -> 18.sp
        else -> 19.sp
    }
}

private fun dynamicStatAmountFontSize(value: String): TextUnit {
    val length = value.length
    return when {
        length >= 16 -> 9.sp
        length >= 14 -> 10.sp
        length >= 12 -> 11.sp
        length >= 10 -> 12.sp
        else -> 13.sp
    }
}

@Composable
internal fun MissingBudgetCard(
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

private val SUMMARY_CARD_MIN_HEIGHT = 118.dp
private val STAT_CARD_COMPACT_WIDTH = 170.dp
