// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneytrack.expense.scheduler.RecurringExpenseScheduler
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ExpenseRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: ExpenseViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    LaunchedEffect(viewModel, context) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ExpenseEvent.Saved -> {
                    val recurringExpenseId = event.result.recurringExpenseId
                    val nextRunAtEpochMillis = event.result.nextRunAtEpochMillis
                    if (recurringExpenseId != null && nextRunAtEpochMillis != null) {
                        RecurringExpenseScheduler.schedule(
                            context = context,
                            recurringExpenseId = recurringExpenseId,
                            triggerAtMillis = nextRunAtEpochMillis,
                        )
                    }
                    onBackClick()
                }
            }
        }
    }

    ExpenseScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onContinueClick = viewModel::submitExpense,
        onAmountChanged = viewModel::onAmountChanged,
        onDescriptionChanged = viewModel::onDescriptionChanged,
        onAttachmentSelected = viewModel::onAttachmentSelected,
        onAttachmentRemoved = viewModel::onAttachmentRemoved,
        onRepeatConfigured = viewModel::onRepeatConfigured,
        onRepeatRemoved = viewModel::onRepeatRemoved,
        onCategorySelected = viewModel::onCategorySelected,
    )
}
