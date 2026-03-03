package com.moneytrack.onboarding.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import com.moneytrack.onboarding.domain.repository.OnboardingRepository

class SetOnboardingCompletedUseCaseTest {

    @Test
    fun invoke_updatesRepositoryWithTrueByDefault() = runTest {
        var latestValue = false
        val repository = object : OnboardingRepository {
            override fun observeCompletion(): Flow<Boolean> = flowOf(false)
            override suspend fun setCompleted(completed: Boolean) {
                latestValue = completed
            }
        }
        val useCase = SetOnboardingCompletedUseCase(repository)

        useCase()

        assertEquals(true, latestValue)
    }
}
