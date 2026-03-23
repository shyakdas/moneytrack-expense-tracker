// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import com.moneytrack.reminder.domain.model.ReminderNotificationSettings
import com.moneytrack.reminder.domain.repository.ReminderPreferencesRepository
import com.moneytrack.reminder.domain.usecase.ObserveReminderNotificationSettingsUseCase
import com.moneytrack.reminder.domain.usecase.UpdateReminderNotificationSettingsUseCase
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
class NotificationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    @Test
    fun uiState_reflectsSavedReminderCount() = runTest {
        val repository = FakeReminderPreferencesRepository(initialNotificationsPerDay = 5)
        val viewModel = createViewModel(repository)
        val collectJob = launch { viewModel.uiState.collect { } }

        advanceUntilIdle()

        assertEquals(5, viewModel.uiState.value.selectedNotificationsPerDay)
        collectJob.cancel()
    }

    @Test
    fun uiState_normalizesUnsupportedReminderCountToDefault() = runTest {
        val repository = FakeReminderPreferencesRepository(initialNotificationsPerDay = 2)
        val viewModel = createViewModel(repository)
        val collectJob = launch { viewModel.uiState.collect { } }

        advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.selectedNotificationsPerDay)
        collectJob.cancel()
    }

    @Test
    fun onNotificationCountSelected_updatesReminderCountAndEmitsCompleted() = runTest {
        val repository = FakeReminderPreferencesRepository(initialNotificationsPerDay = 3)
        val viewModel = createViewModel(repository)
        val events = mutableListOf<NotificationEvent>()
        val collectJob = launch { viewModel.events.collect(events::add) }

        viewModel.onNotificationCountSelected(1)
        advanceUntilIdle()

        assertEquals(1, repository.savedNotificationsPerDay)
        assertEquals(NotificationEvent.Completed, events.last())
        collectJob.cancel()
    }

    private fun createViewModel(
        repository: FakeReminderPreferencesRepository,
    ): NotificationViewModel {
        return NotificationViewModel(
            observeReminderNotificationSettingsUseCase = ObserveReminderNotificationSettingsUseCase(repository),
            updateReminderNotificationSettingsUseCase = UpdateReminderNotificationSettingsUseCase(repository),
        )
    }

    private class FakeReminderPreferencesRepository(
        initialNotificationsPerDay: Int,
    ) : ReminderPreferencesRepository {
        private val reminderSettings = MutableStateFlow(
            ReminderNotificationSettings(
                notificationsPerDay = initialNotificationsPerDay,
                reminderMessage = "Track your expenses.",
            ),
        )
        var savedNotificationsPerDay: Int? = null

        override fun observePermissionPromptHandled(): Flow<Boolean> = MutableStateFlow(false).asStateFlow()

        override suspend fun setPermissionPromptHandled(handled: Boolean) = Unit

        override fun observeReminderSettings(): Flow<ReminderNotificationSettings> =
            reminderSettings.asStateFlow()

        override suspend fun updateReminderSettings(
            notificationsPerDay: Int,
            reminderMessage: String,
        ) {
            savedNotificationsPerDay = notificationsPerDay
            reminderSettings.value = ReminderNotificationSettings(
                notificationsPerDay = notificationsPerDay,
                reminderMessage = reminderMessage,
            )
        }
    }
}
