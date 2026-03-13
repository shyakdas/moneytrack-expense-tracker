// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.locale.CurrencyCatalog
import com.moneytrack.reminder.domain.usecase.ObserveReminderNotificationSettingsUseCase
import com.moneytrack.security.domain.model.PinSetupStatus
import com.moneytrack.security.domain.usecase.GetPinSetupStatusUseCase
import com.moneytrack.settings.domain.usecase.ObserveAppCurrencyCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeReminderNotificationSettingsUseCase: ObserveReminderNotificationSettingsUseCase,
    getPinSetupStatusUseCase: GetPinSetupStatusUseCase,
    observeAppCurrencyCodeUseCase: ObserveAppCurrencyCodeUseCase,
    private val currencyCatalog: CurrencyCatalog,
) : ViewModel() {

    private val baseState = SettingsUiState(
        currencySymbol = DEFAULT_CURRENCY_SYMBOL,
        language = Locale.getDefault().displayLanguage.replaceFirstCharIfNeeded(),
        themeMode = SettingsThemeMode.SYSTEM,
        notificationsPerDay = DEFAULT_NOTIFICATIONS_PER_DAY,
    )

    val uiState: StateFlow<SettingsUiState> = combine(
        observeReminderNotificationSettingsUseCase(),
        getPinSetupStatusUseCase(),
        observeAppCurrencyCodeUseCase(),
    ) { reminderSettings, pinStatus, currencyCode ->
        baseState.copy(
            currencySymbol = currencyCatalog.find(currencyCode)?.symbol ?: currencyCode,
            securityType = pinStatus.toSettingsSecurityType(),
            notificationsPerDay = reminderSettings.notificationsPerDay,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(WHILE_SUBSCRIBED_TIMEOUT_MS),
        initialValue = baseState,
    )

    private companion object {
        private const val DEFAULT_CURRENCY_SYMBOL = "$"
        private const val WHILE_SUBSCRIBED_TIMEOUT_MS = 5_000L
        private const val DEFAULT_NOTIFICATIONS_PER_DAY = 3
    }
}

data class SettingsUiState(
    val currencySymbol: String,
    val language: String,
    val themeMode: SettingsThemeMode,
    val securityType: SettingsSecurityType = SettingsSecurityType.NOT_SET,
    val notificationsPerDay: Int,
)

enum class SettingsThemeMode {
    SYSTEM,
}

enum class SettingsSecurityType {
    PIN,
    BIOMETRIC,
    NOT_SET,
}

private fun PinSetupStatus.toSettingsSecurityType(): SettingsSecurityType =
    when (this) {
        PinSetupStatus.PIN_ENABLED -> SettingsSecurityType.PIN
        PinSetupStatus.BIOMETRIC_ENABLED -> SettingsSecurityType.BIOMETRIC
        PinSetupStatus.NOT_STARTED,
        PinSetupStatus.SKIPPED,
            -> SettingsSecurityType.NOT_SET
    }

private fun String.replaceFirstCharIfNeeded(): String {
    return replaceFirstChar { char ->
        if (char.isLowerCase()) {
            char.titlecase(Locale.getDefault())
        } else {
            char.toString()
        }
    }
}
