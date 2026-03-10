// Copyright (c) 2026 shyakdas

package com.moneytrack.onboarding.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moneytrack.R
import com.moneytrack.onboarding.domain.model.OnboardingPage

@Composable
fun OnboardingRoute(
    onCompleted: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                OnboardingEvent.Completed -> onCompleted()
            }
        }
    }

    OnboardingScreen(
        pages = onboardingPages(),
        onFinished = { viewModel.onAction(OnboardingAction.OnFinishedClick) },
    )
}

private fun onboardingPages() = listOf(
    OnboardingPage(
        imageRes = com.moneytrack.designsystem.R.drawable.variant_gain_total_control_of_your_money,
        titleRes = R.string.onboarding_title_control,
        descriptionRes = R.string.onboarding_desc_control,
    ),
    OnboardingPage(
        imageRes = com.moneytrack.designsystem.R.drawable.variant_know_where_your_money_goes,
        titleRes = R.string.onboarding_title_track,
        descriptionRes = R.string.onboarding_desc_track,
    ),
    OnboardingPage(
        imageRes = com.moneytrack.designsystem.R.drawable.variant_planning_ahead,
        titleRes = R.string.onboarding_title_plan,
        descriptionRes = R.string.onboarding_desc_plan,
    ),
)
