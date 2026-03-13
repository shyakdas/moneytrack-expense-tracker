// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import com.moneytrack.settings.domain.model.AppThemeMode
import com.moneytrack.settings.domain.repository.ThemePreferenceRepository
import com.moneytrack.settings.domain.usecase.ObserveAppThemeModeUseCase
import com.moneytrack.settings.domain.usecase.SaveAppThemeModeUseCase
import com.moneytrack.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ThemeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    @Test
    fun uiState_reflectsSavedThemeMode() = runTest {
        val repository = FakeThemePreferenceRepository(initialThemeMode = AppThemeMode.DARK)
        val viewModel = createViewModel(repository)
        val collectJob = launch { viewModel.uiState.collect { } }

        advanceUntilIdle()

        assertEquals(AppThemeMode.DARK, viewModel.uiState.value.selectedThemeMode)
        collectJob.cancel()
    }

    @Test
    fun onThemeModeSelected_savesThemeMode() = runTest {
        val repository = FakeThemePreferenceRepository(initialThemeMode = AppThemeMode.SYSTEM)
        val viewModel = createViewModel(repository)
        val collectJob = launch { viewModel.uiState.collect { } }

        viewModel.onThemeModeSelected(AppThemeMode.LIGHT)
        advanceUntilIdle()

        assertEquals(AppThemeMode.LIGHT, repository.savedThemeMode)
        assertEquals(AppThemeMode.LIGHT, viewModel.uiState.value.selectedThemeMode)
        collectJob.cancel()
    }

    private fun createViewModel(
        repository: FakeThemePreferenceRepository,
    ): ThemeViewModel {
        return ThemeViewModel(
            observeAppThemeModeUseCase = ObserveAppThemeModeUseCase(repository),
            saveAppThemeModeUseCase = SaveAppThemeModeUseCase(repository),
        )
    }

    private class FakeThemePreferenceRepository(
        initialThemeMode: AppThemeMode,
    ) : ThemePreferenceRepository {
        private val themeMode = MutableStateFlow(initialThemeMode)
        var savedThemeMode: AppThemeMode? = null

        override fun observeAppThemeMode(): Flow<AppThemeMode> = themeMode.asStateFlow()

        override suspend fun saveAppThemeMode(appThemeMode: AppThemeMode) {
            savedThemeMode = appThemeMode
            themeMode.value = appThemeMode
        }
    }
}
