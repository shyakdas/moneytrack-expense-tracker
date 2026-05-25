// Copyright (c) 2026 shyakdas

@file:Suppress("LongParameterList")

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
import androidx.compose.ui.unit.dp
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
            BalanceSummaryHeader(accountBalanceText = accountBalanceText)
            BalanceStatsRow(
                hasBudget = hasBudget,
                budgetAmount = budgetAmount,
                budgetText = budgetText,
                expensesText = expensesText,
                onSetBudgetClick = onSetBudgetClick,
            )
        }
    }
}

@Composable
private fun BalanceSummaryHeader(accountBalanceText: String) {
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
}

@Composable
private fun BalanceStatsRow(
    hasBudget: Boolean,
    budgetAmount: Double?,
    budgetText: String?,
    expensesText: String,
    onSetBudgetClick: (Double?) -> Unit,
) {
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

@Composable
internal fun StatCard(
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
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
        ) {
            StatCardContent(
                label = label,
                value = value,
                icon = icon,
                background = background,
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
    background: Color,
    compact: Boolean,
) {
    val labelStyle = if (compact) AppTheme.typography.bodySmall else AppTheme.typography.bodyMedium
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
        StatCardIcon(
            icon = icon,
            iconSize = if (compact) Dimens.spacing36 else Dimens.iconContainerSize,
            tint = background,
        )
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

@Composable
private fun StatCardIcon(
    icon: Int,
    iconSize: androidx.compose.ui.unit.Dp,
    tint: Color,
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
            tint = tint,
        )
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
