// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ExpenseRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: ExpenseViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    ExpenseScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onContinueClick = {},
        onAmountChanged = viewModel::onAmountChanged,
        onDescriptionChanged = viewModel::onDescriptionChanged,
        onAttachmentSelected = viewModel::onAttachmentSelected,
        onAttachmentRemoved = viewModel::onAttachmentRemoved,
        onCategorySelected = viewModel::onCategorySelected,
    )
}
