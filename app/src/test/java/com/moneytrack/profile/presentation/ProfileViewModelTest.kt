// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.presentation

import com.moneytrack.profile.domain.repository.ProfileRepository
import com.moneytrack.profile.domain.usecase.ClearLocalDataUseCase
import com.moneytrack.profile.domain.usecase.ObserveProfileDisplayNameUseCase
import com.moneytrack.profile.domain.usecase.SaveProfileDisplayNameUseCase
import com.moneytrack.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    @Test
    fun initialState_usesFallbackName() {
        val viewModel = createViewModel()

        val state = viewModel.uiState.value

        assertEquals("Saver", state.name)
        assertFalse(state.isEditSheetVisible)
        assertFalse(state.isClearDataSheetVisible)
        assertFalse(state.clearDataCompleted)
    }

    @Test
    fun showEditSheet_populatesDraftFromCurrentName() = runTest {
        val repository = FakeProfileRepository(initialName = "Nova")
        val viewModel = createViewModel(repository = repository)
        val collectJob = launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        viewModel.showEditSheet()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isEditSheetVisible)
        assertEquals("Nova", state.editName)
        assertTrue(state.isSaveEnabled)
        collectJob.cancel()
    }

    @Test
    fun onNameChanged_allowsEmptyDraftWhileEditing() = runTest {
        val repository = FakeProfileRepository(initialName = "Saver")
        val viewModel = createViewModel(repository = repository)
        val collectJob = launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        viewModel.showEditSheet()
        viewModel.onNameChanged("")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isEditSheetVisible)
        assertEquals("", state.editName)
        assertFalse(state.isSaveEnabled)
        collectJob.cancel()
    }

    @Test
    fun saveName_trimsValueAndClosesSheet() = runTest {
        val repository = FakeProfileRepository(initialName = "Saver")
        val viewModel = createViewModel(repository = repository)
        val collectJob = launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        viewModel.showEditSheet()
        viewModel.onNameChanged("  Mint  ")
        viewModel.saveName()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Mint", repository.savedName)
        assertEquals("Mint", state.name)
        assertFalse(state.isEditSheetVisible)
        assertEquals("Mint", repository.currentName())
        collectJob.cancel()
    }

    @Test
    fun hideEditSheet_resetsDraftAndClosesSheet() = runTest {
        val viewModel = createViewModel(repository = FakeProfileRepository(initialName = "Saver"))
        val collectJob = launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        viewModel.showEditSheet()
        viewModel.onNameChanged("Orbit")
        viewModel.hideEditSheet()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isEditSheetVisible)
        assertEquals("Saver", state.name)
        collectJob.cancel()
    }

    @Test
    fun clearDataFlow_setsCompletionFlagAndCanBeAcknowledged() = runTest {
        val clearLocalDataUseCase = mockk<ClearLocalDataUseCase>()
        coEvery { clearLocalDataUseCase.invoke() } returns Unit
        val viewModel = createViewModel(clearLocalDataUseCase = clearLocalDataUseCase)
        val collectJob = launch { viewModel.uiState.collect { } }

        viewModel.showClearDataSheet()
        viewModel.clearLocalData()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.clearDataCompleted)
        assertFalse(viewModel.uiState.value.isClearDataSheetVisible)
        coVerify(exactly = 1) { clearLocalDataUseCase.invoke() }

        viewModel.onClearDataHandled()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.clearDataCompleted)
        collectJob.cancel()
    }

    private fun createViewModel(
        repository: FakeProfileRepository = FakeProfileRepository(),
        clearLocalDataUseCase: ClearLocalDataUseCase = mockk(relaxed = true),
    ): ProfileViewModel {
        return ProfileViewModel(
            observeProfileDisplayNameUseCase = ObserveProfileDisplayNameUseCase(repository),
            saveProfileDisplayNameUseCase = SaveProfileDisplayNameUseCase(repository),
            clearLocalDataUseCase = clearLocalDataUseCase,
        )
    }

    private class FakeProfileRepository(
        initialName: String = "",
    ) : ProfileRepository {
        private val displayNameFlow = MutableStateFlow(initialName)
        var savedName: String? = null
            private set

        override fun observeDisplayName(): Flow<String> = displayNameFlow.asStateFlow()

        override suspend fun saveDisplayName(displayName: String) {
            savedName = displayName
            displayNameFlow.value = displayName
        }

        fun currentName(): String = displayNameFlow.value
    }
}
