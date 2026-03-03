package com.moneytrack.onboarding.domain.usecase

import kotlinx.coroutines.flow.Flow
import com.moneytrack.onboarding.domain.repository.OnboardingRepository
import javax.inject.Inject

class GetOnboardingCompletedUseCase @Inject constructor(
    private val repository: OnboardingRepository,
) {
    operator fun invoke(): Flow<Boolean> = repository.observeCompletion()
}
