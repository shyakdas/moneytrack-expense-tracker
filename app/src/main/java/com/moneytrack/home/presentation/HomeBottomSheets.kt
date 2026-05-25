// Copyright (c) 2026 shyakdas

package com.moneytrack.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.moneytrack.common.ui.LottieAnimationView
import com.moneytrack.designsystem.R
import com.moneytrack.R as AppR
import kotlinx.coroutines.delay
import ui.components.form.input.InputField
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
        BudgetSetupBottomSheetContent(
            state = BudgetSetupContentState(
                sheetStep = sheetStep,
                budgetInput = budgetInput,
                formattedAmount = formatAmount(parsedBudget ?: 0.0),
                canContinue = canContinue,
            ),
            actions = BudgetSetupContentActions(
                onNextClick = { sheetStep = BudgetSheetStep.INPUT },
                onBudgetInputChange = { input ->
                    budgetInput = input.filter { char -> char.isDigit() }.take(MAX_BUDGET_INPUT_LENGTH)
                },
                onContinueClick = {
                    parsedBudget?.let { budgetValue ->
                        onSaveBudget(budgetValue, null)
                        sheetStep = BudgetSheetStep.SUCCESS
                    }
                },
            ),
        )
    }
}

@Composable
private fun BudgetSetupBottomSheetContent(
    state: BudgetSetupContentState,
    actions: BudgetSetupContentActions,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacing24)
            .padding(bottom = Dimens.spacing24),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing16),
    ) {
        when (state.sheetStep) {
            BudgetSheetStep.PRIVACY -> BudgetPrivacyStep(onNextClick = actions.onNextClick)
            BudgetSheetStep.INPUT -> BudgetInputStep(
                budgetInput = state.budgetInput,
                formattedAmount = state.formattedAmount,
                canContinue = state.canContinue,
                onBudgetInputChange = actions.onBudgetInputChange,
                onContinueClick = actions.onContinueClick,
            )
            BudgetSheetStep.SUCCESS -> BudgetSuccessStep()
        }
    }
}

@Composable
private fun BudgetPrivacyStep(
    onNextClick: () -> Unit,
) {
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
        onClick = onNextClick,
    )
}

@Composable
private fun BudgetInputStep(
    budgetInput: String,
    formattedAmount: String,
    canContinue: Boolean,
    onBudgetInputChange: (String) -> Unit,
    onContinueClick: () -> Unit,
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
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.onPrimary,
            )
            Spacer(modifier = Modifier.height(Dimens.spacing8))
            Text(
                text = formattedAmount,
                style = AppTheme.typography.headlineLarge,
                color = AppTheme.colors.onPrimary,
            )
        }
    }

    InputField(
        value = budgetInput,
        onValueChange = onBudgetInputChange,
        placeholder = stringResource(id = AppR.string.home_budget_sheet_amount_hint),
        leadingIcon = ImageVector.vectorResource(id = R.drawable.wallet_3),
    )

    LargeButton(
        text = stringResource(id = AppR.string.home_budget_sheet_continue),
        onClick = onContinueClick,
        enabled = canContinue,
    )
}

@Composable
private fun BudgetSuccessStep() {
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

private enum class BudgetSheetStep {
    PRIVACY,
    INPUT,
    SUCCESS,
}

private data class BudgetSetupContentState(
    val sheetStep: BudgetSheetStep,
    val budgetInput: String,
    val formattedAmount: String,
    val canContinue: Boolean,
)

private data class BudgetSetupContentActions(
    val onNextClick: () -> Unit,
    val onBudgetInputChange: (String) -> Unit,
    val onContinueClick: () -> Unit,
)

private const val MAX_BUDGET_INPUT_LENGTH = 8
private const val BUDGET_SUCCESS_DISMISS_DELAY_MS = 1200L

@Suppress("UnusedPrivateMember")
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun BudgetSetupBottomSheetPreview() {
    MoneyTrackTheme(darkTheme = false) {
        Surface(color = AppTheme.colors.surface) {
            BudgetSetupBottomSheetContent(
                state = BudgetSetupContentState(
                    sheetStep = BudgetSheetStep.INPUT,
                    budgetInput = "5200",
                    formattedAmount = "$5,200",
                    canContinue = true,
                ),
                actions = BudgetSetupContentActions(
                    onNextClick = {},
                    onBudgetInputChange = {},
                    onContinueClick = {},
                ),
            )
        }
    }
}
