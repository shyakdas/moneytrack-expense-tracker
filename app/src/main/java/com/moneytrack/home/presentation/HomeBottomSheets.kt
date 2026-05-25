// Copyright (c) 2026 shyakdas
@file:Suppress("TooManyFunctions")

package com.moneytrack.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneytrack.common.ui.LottieAnimationView
import com.moneytrack.designsystem.R
import com.moneytrack.R as AppR
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import ui.components.form.control.PrimarySwitch
import ui.components.navigation.button.LargeButton
import ui.components.surface.MoneyTrackBottomSheet
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
internal fun NotificationPermissionBottomSheet(
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
internal fun BudgetSetupBottomSheet(
    onDismiss: () -> Unit,
    initialBudgetAmount: Double?,
    selectedMonth: HomeMonthOption,
    formatAmount: (Double) -> String,
    onSaveBudget: (Double, String?) -> Unit,
    onSavedCompleted: () -> Unit,
) {
    var budgetInput by remember(initialBudgetAmount) {
        mutableStateOf(initialBudgetAmount?.toLong()?.toString().orEmpty())
    }
    var repeatMonthly by remember { mutableStateOf(true) }
    val parsedBudget = budgetInput.toDoubleOrNull()
    val canSave = parsedBudget != null && parsedBudget > 0.0

    MoneyTrackBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        BudgetSetupBottomSheetContent(
            state = BudgetSetupContentState(
                budgetInput = budgetInput,
                formattedAmount = formatAmount(parsedBudget ?: 0.0),
                canSave = canSave,
                repeatMonthly = repeatMonthly,
                monthLabel = selectedMonth.toBudgetMonthLabel(),
            ),
            onBudgetInputChange = { input ->
                budgetInput = input.filter { char -> char.isDigit() }.take(MAX_BUDGET_INPUT_LENGTH)
            },
            onRepeatMonthlyChange = { repeatMonthly = it },
            onSaveClick = {
                parsedBudget?.let { budgetValue ->
                    onSaveBudget(budgetValue, null)
                    onSavedCompleted()
                }
            },
        )
    }
}

@Composable
private fun BudgetSetupBottomSheetContent(
    state: BudgetSetupContentState,
    onBudgetInputChange: (String) -> Unit,
    onRepeatMonthlyChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacing24)
            .padding(bottom = Dimens.spacing24),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing16),
    ) {
        Text(
            text = stringResource(id = AppR.string.home_budget_sheet_title),
            style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = AppTheme.colors.onSurface,
        )
        Text(
            text = stringResource(id = AppR.string.home_budget_sheet_subtitle),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.onSurfaceVariant,
        )
        BudgetMonthSelectorRow(monthLabel = state.monthLabel)
        BudgetHeroCard(formattedAmount = state.formattedAmount)
        Text(
            text = stringResource(id = AppR.string.home_budget_sheet_amount_hint),
            style = AppTheme.typography.titleSmall,
            color = AppTheme.colors.onSurface,
        )
        BudgetAmountInput(
            amountInput = state.budgetInput,
            onAmountInputChange = onBudgetInputChange,
        )
        BudgetRepeatMonthlyRow(
            checked = state.repeatMonthly,
            onCheckedChange = onRepeatMonthlyChange,
        )
        SaveBudgetButton(
            enabled = state.canSave,
            onClick = onSaveClick,
        )
    }
}

@Composable
private fun BudgetMonthSelectorRow(monthLabel: String) {
    Text(
        text = stringResource(id = AppR.string.home_budget_sheet_month_label),
        style = AppTheme.typography.titleSmall,
        color = AppTheme.colors.onSurface,
    )
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radius16),
        color = AppTheme.colors.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = Dimens.spacing1,
                    color = AppTheme.colors.outline.copy(alpha = 0.32f),
                    shape = RoundedCornerShape(Dimens.radius16),
                )
                .padding(horizontal = Dimens.spacing12, vertical = Dimens.spacing12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MonthSelectorLeadingIcon()
            Spacer(modifier = Modifier.size(Dimens.spacing12))
            Text(
                text = monthLabel,
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.onSurface,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MonthSelectorLeadingIcon() {
    Box(
        modifier = Modifier
            .size(Dimens.spacing48)
            .clip(CircleShape)
            .background(AppTheme.colors.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.wallet_3),
            contentDescription = null,
            tint = AppTheme.colors.primary,
        )
    }
}

@Composable
private fun BudgetHeroCard(formattedAmount: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radius24),
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            AppTheme.colors.primaryContainer,
                            AppTheme.colors.surface,
                            AppTheme.colors.primaryContainer.copy(alpha = 0.6f),
                        ),
                    ),
                )
                .padding(Dimens.spacing20),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BudgetHeroText(formattedAmount = formattedAmount, modifier = Modifier.weight(1f))
            BudgetHeroIcon()
        }
    }
}

@Composable
private fun BudgetHeroText(
    formattedAmount: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = AppR.string.home_budget_sheet_budget_label),
            style = AppTheme.typography.labelMedium,
            color = AppTheme.colors.primary,
        )
        Spacer(modifier = Modifier.height(Dimens.spacing8))
        Text(
            text = formattedAmount,
            style = AppTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = AppTheme.colors.onSurface,
        )
        Spacer(modifier = Modifier.height(Dimens.spacing8))
        Text(
            text = stringResource(id = AppR.string.home_budget_sheet_budget_subtitle),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun BudgetHeroIcon() {
    Box(contentAlignment = Alignment.TopEnd) {
        Surface(
            modifier = Modifier.size(92.dp),
            shape = CircleShape,
            color = AppTheme.colors.surface,
            tonalElevation = Dimens.elevation4,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.wallet_3),
                    contentDescription = null,
                    tint = AppTheme.colors.primary,
                    modifier = Modifier.size(Dimens.spacing32),
                )
            }
        }
        Box(
            modifier = Modifier
                .size(Dimens.spacing20)
                .clip(CircleShape)
                .background(AppTheme.colors.success),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "✓",
                style = AppTheme.typography.labelSmall,
                color = AppTheme.colors.onPrimary,
            )
        }
    }
}

@Composable
private fun BudgetAmountInput(
    amountInput: String,
    onAmountInputChange: (String) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = Dimens.spacing1,
                color = AppTheme.colors.primary,
                shape = RoundedCornerShape(Dimens.radius16),
            ),
        shape = RoundedCornerShape(Dimens.radius16),
        color = AppTheme.colors.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacing12, vertical = Dimens.spacing10),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = RoundedCornerShape(Dimens.radius12),
                color = AppTheme.colors.surfaceVariant,
            ) {
                Text(
                    text = "$",
                    modifier = Modifier.padding(horizontal = Dimens.spacing12, vertical = Dimens.spacing8),
                    style = AppTheme.typography.titleLarge,
                    color = AppTheme.colors.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.size(Dimens.spacing12))
            BasicTextField(
                value = amountInput,
                onValueChange = onAmountInputChange,
                textStyle = AppTheme.typography.titleLarge.copy(color = AppTheme.colors.onSurface),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
                cursorBrush = SolidColor(AppTheme.colors.primary),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun BudgetRepeatMonthlyRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radius16),
        color = AppTheme.colors.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = Dimens.spacing1,
                    color = AppTheme.colors.outline.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(Dimens.radius16),
                )
                .padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = AppR.string.home_budget_sheet_repeat_title),
                    style = AppTheme.typography.titleSmall,
                    color = AppTheme.colors.onSurface,
                )
                Text(
                    text = stringResource(id = AppR.string.home_budget_sheet_repeat_subtitle),
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colors.onSurfaceVariant,
                )
            }
            PrimarySwitch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
    }
}

@Composable
private fun SaveBudgetButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.radius16))
            .clickable(enabled = enabled, onClick = onClick),
        color = Color.Transparent,
        shape = RoundedCornerShape(Dimens.radius16),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = if (enabled) {
                            listOf(AppTheme.colors.primary, AppTheme.colors.primary.copy(alpha = 0.75f))
                        } else {
                            listOf(
                                AppTheme.colors.outline.copy(alpha = 0.4f),
                                AppTheme.colors.outline.copy(alpha = 0.4f),
                            )
                        },
                    ),
                )
                .padding(horizontal = Dimens.spacing20, vertical = Dimens.spacing16),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = AppR.string.home_budget_sheet_save),
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colors.onPrimary,
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

private data class BudgetSetupContentState(
    val monthLabel: String,
    val formattedAmount: String,
    val canSave: Boolean,
    val repeatMonthly: Boolean,
    val budgetInput: String,
)

private fun HomeMonthOption.toBudgetMonthLabel(): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, monthIndex)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    return SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)
}

private const val MAX_BUDGET_INPUT_LENGTH = 8

@Suppress("UnusedPrivateMember")
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun BudgetSetupBottomSheetPreview() {
    MoneyTrackTheme(darkTheme = false) {
        Surface(color = AppTheme.colors.surface) {
            BudgetSetupBottomSheetContent(
                state = BudgetSetupContentState(
                    monthLabel = "May 2026",
                    formattedAmount = "$5,200",
                    canSave = true,
                    repeatMonthly = true,
                    budgetInput = "5200",
                ),
                onBudgetInputChange = {},
                onRepeatMonthlyChange = {},
                onSaveClick = {},
            )
        }
    }
}
