package com.moneytrack.onboarding.presentation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import com.moneytrack.onboarding.domain.repository.OnboardingRepository
import com.moneytrack.onboarding.domain.usecase.SetOnboardingCompletedUseCase
import com.moneytrack.testutil.MainDispatcherRule

class OnboardingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun onFinishedClick_marksOnboardingCompleted() = runTest {
        val fakeRepository = FakeOnboardingRepository()
        val useCase = SetOnboardingCompletedUseCase(fakeRepository)
        val viewModel = OnboardingViewModel(useCase)

        viewModel.onAction(OnboardingAction.OnFinishedClick)
        advanceUntilIdle()

        assertEquals(true, fakeRepository.latestCompletedValue)
    }

    private class FakeOnboardingRepository : OnboardingRepository {
        var latestCompletedValue: Boolean? = null

        override fun observeCompletion(): Flow<Boolean> = flowOf(false)

        override suspend fun setCompleted(completed: Boolean) {
            latestCompletedValue = completed
        }
    }
}
