// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.reminder.domain.usecase.ObserveReminderNotificationSettingsUseCase
import com.moneytrack.reminder.domain.usecase.UpdateReminderNotificationSettingsUseCase
import com.moneytrack.reminder.notification.ReminderSchedule
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationViewModel @Inject constructor(
    observeReminderNotificationSettingsUseCase: ObserveReminderNotificationSettingsUseCase,
    private val updateReminderNotificationSettingsUseCase: UpdateReminderNotificationSettingsUseCase,
) : ViewModel() {

    private val _events = MutableSharedFlow<NotificationEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val events = _events.asSharedFlow()

    val uiState: StateFlow<NotificationUiState> = observeReminderNotificationSettingsUseCase()
        .map { reminderSettings ->
            NotificationUiState(
                selectedNotificationsPerDay = ReminderSchedule.normalize(
                    reminderSettings.notificationsPerDay,
                ),
                reminderMessage = reminderSettings.reminderMessage,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(WHILE_SUBSCRIBED_TIMEOUT_MS),
            initialValue = NotificationUiState(),
        )

    fun onNotificationCountSelected(notificationsPerDay: Int) {
        viewModelScope.launch {
            updateReminderNotificationSettingsUseCase(
                notificationsPerDay = notificationsPerDay,
                reminderMessage = uiState.value.reminderMessage,
            )
            _events.emit(NotificationEvent.Completed)
        }
    }

    private companion object {
        private const val WHILE_SUBSCRIBED_TIMEOUT_MS = 5_000L
    }
}

data class NotificationUiState(
    val selectedNotificationsPerDay: Int = ReminderSchedule.DEFAULT_NOTIFICATIONS_PER_DAY,
    val reminderMessage: String = "",
)

sealed interface NotificationEvent {
    data object Completed : NotificationEvent
}
