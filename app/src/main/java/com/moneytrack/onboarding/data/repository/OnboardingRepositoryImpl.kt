package com.moneytrack.onboarding.data.repository

import kotlinx.coroutines.flow.Flow
import com.moneytrack.onboarding.data.local.OnboardingPreferencesDataSource
import com.moneytrack.onboarding.domain.repository.OnboardingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingRepositoryImpl @Inject constructor(
    private val dataSource: OnboardingPreferencesDataSource,
) : OnboardingRepository {
    override fun observeCompletion(): Flow<Boolean> = dataSource.onboardingCompletedFlow

    override suspend fun setCompleted(completed: Boolean) {
        dataSource.setOnboardingCompleted(completed = completed)
    }
}
