// Copyright (c) 2026 shyakdas

package com.moneytrack.onboarding.domain.usecase

import com.moneytrack.onboarding.domain.repository.OnboardingRepository
import javax.inject.Inject

class SetOnboardingCompletedUseCase @Inject constructor(
    private val repository: OnboardingRepository,
) {
    suspend operator fun invoke(completed: Boolean = true) {
        repository.setCompleted(completed = completed)
    }
}
