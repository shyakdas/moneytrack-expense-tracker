package com.moneytrack.onboarding.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import com.moneytrack.onboarding.data.local.OnboardingPreferencesDataSource

class OnboardingRepositoryImplTest {

    private val dataSource = mockk<OnboardingPreferencesDataSource>()
    private val repository = OnboardingRepositoryImpl(dataSource)

    @Test
    fun observeCompletion_delegatesToDataSourceFlow() = runTest {
        every { dataSource.onboardingCompletedFlow } returns flowOf(true)

        assertTrue(repository.observeCompletion().first())
    }

    @Test
    fun setCompleted_delegatesToDataSource() = runTest {
        coEvery { dataSource.setOnboardingCompleted(true) } returns Unit

        repository.setCompleted(true)

        coVerify(exactly = 1) { dataSource.setOnboardingCompleted(true) }
    }
}
