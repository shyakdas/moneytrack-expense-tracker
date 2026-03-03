package com.moneytrack.onboarding.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import com.moneytrack.onboarding.domain.repository.OnboardingRepository

class GetOnboardingCompletedUseCaseTest {

    @Test
    fun invoke_returnsRepositoryFlow() = runTest {
        val repository = object : OnboardingRepository {
            override fun observeCompletion(): Flow<Boolean> = flowOf(true)
            override suspend fun setCompleted(completed: Boolean) = Unit
        }
        val useCase = GetOnboardingCompletedUseCase(repository)

        assertTrue(useCase().first())
    }
}
