// Copyright (c) 2026 shyakdas

package com.moneytrack.home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.moneytrack.home.domain.model.Budget

@Composable
internal fun HomeNotificationPromptEffect(
    hasNotificationPermission: Boolean,
    isPromptHandled: Boolean,
    onShowPrompt: () -> Unit,
) {
    LaunchedEffect(isPromptHandled, hasNotificationPermission) {
        if (!isPromptHandled && !hasNotificationPermission) {
            onShowPrompt()
        }
    }
}

@Composable
internal fun HomeBudgetPromptEffect(
    isBudgetLoaded: Boolean,
    budget: Budget?,
    shouldShowNotificationPermissionSheet: Boolean,
    hasShownInitialBudgetPrompt: Boolean,
    onPromptShown: () -> Unit,
) {
    LaunchedEffect(isBudgetLoaded, budget, shouldShowNotificationPermissionSheet) {
        if (isBudgetLoaded && budget == null && !hasShownInitialBudgetPrompt) {
            if (shouldShowNotificationPermissionSheet) {
                return@LaunchedEffect
            }
            onPromptShown()
        }
    }
}
