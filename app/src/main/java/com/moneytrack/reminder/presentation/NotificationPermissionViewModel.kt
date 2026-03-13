// Copyright (c) 2026 shyakdas

package com.moneytrack.reminder.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.locale.AppCurrencyManager
import com.moneytrack.locale.CurrencyCatalog
import com.moneytrack.reminder.domain.model.ReminderNotificationSettings
import com.moneytrack.reminder.domain.usecase.ObserveNotificationPermissionPromptHandledUseCase
import com.moneytrack.reminder.domain.usecase.ObserveReminderNotificationSettingsUseCase
import com.moneytrack.reminder.domain.usecase.SetNotificationPermissionPromptHandledUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationPermissionViewModel @Inject constructor(
    observeNotificationPermissionPromptHandledUseCase: ObserveNotificationPermissionPromptHandledUseCase,
    observeReminderNotificationSettingsUseCase: ObserveReminderNotificationSettingsUseCase,
    private val setNotificationPermissionPromptHandledUseCase: SetNotificationPermissionPromptHandledUseCase,
    appCurrencyManager: AppCurrencyManager,
    currencyCatalog: CurrencyCatalog,
) : ViewModel() {

    private val _isPermissionPromptVisible = MutableStateFlow(false)
    private val defaultReminderMessage = defaultReminderMessage(
        currencyCode = appCurrencyManager.currentCurrencyCode(),
        currencyCatalog = currencyCatalog,
    )
    val uiState: StateFlow<NotificationPermissionUiState> = combine(
        _isPermissionPromptVisible,
        observeNotificationPermissionPromptHandledUseCase(),
        observeReminderNotificationSettingsUseCase(),
    ) { isPermissionPromptVisible, isPromptHandled, reminderSettings ->
        NotificationPermissionUiState(
            isPermissionPromptVisible = isPermissionPromptVisible,
            isPromptHandled = isPromptHandled,
            reminderSettings = reminderSettings,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(WHILE_SUBSCRIBED_TIMEOUT_MS),
        initialValue = NotificationPermissionUiState(
            reminderSettings = ReminderNotificationSettings(
                notificationsPerDay = DEFAULT_NOTIFICATIONS_PER_DAY,
                reminderMessage = defaultReminderMessage,
            ),
        ),
    )

    fun showPermissionPrompt() {
        _isPermissionPromptVisible.update { true }
    }

    fun hidePermissionPrompt() {
        _isPermissionPromptVisible.update { false }
    }

    fun markPermissionPromptHandled() {
        viewModelScope.launch {
            setNotificationPermissionPromptHandledUseCase(handled = true)
        }
    }
}

data class NotificationPermissionUiState(
    val isPermissionPromptVisible: Boolean = false,
    val isPromptHandled: Boolean = false,
    val reminderSettings: ReminderNotificationSettings = ReminderNotificationSettings(
        notificationsPerDay = DEFAULT_NOTIFICATIONS_PER_DAY,
        reminderMessage = "Add your expenses in MoneyTrack to stay on top of your budget.",
    ),
)

private const val WHILE_SUBSCRIBED_TIMEOUT_MS = 5_000L
private const val DEFAULT_NOTIFICATIONS_PER_DAY = 3

private fun defaultReminderMessage(
    currencyCode: String,
    currencyCatalog: CurrencyCatalog,
): String {
    val currencySymbol = currencyCatalog.find(currencyCode)?.symbol ?: currencyCode
    return "Add your expenses in MoneyTrack to stay on top of your $currencySymbol budget."
}
