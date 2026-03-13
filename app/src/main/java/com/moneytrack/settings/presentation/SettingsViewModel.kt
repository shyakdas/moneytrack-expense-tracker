// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.locale.CountryProvider
import com.moneytrack.reminder.domain.usecase.ObserveReminderNotificationSettingsUseCase
import com.moneytrack.security.domain.model.PinSetupStatus
import com.moneytrack.security.domain.usecase.GetPinSetupStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Currency
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
    countryProvider: CountryProvider,
) : ViewModel() {

    private val baseState = SettingsUiState(
        currencyCode = countryProvider.currencyCode(),
        language = Locale.getDefault().displayLanguage.replaceFirstCharIfNeeded(),
        themeMode = SettingsThemeMode.SYSTEM,
        notificationsPerDay = DEFAULT_NOTIFICATIONS_PER_DAY,
    )

    val uiState: StateFlow<SettingsUiState> = combine(
        observeReminderNotificationSettingsUseCase(),
        getPinSetupStatusUseCase(),
    ) { reminderSettings, pinStatus ->
        baseState.copy(
            securityType = pinStatus.toSettingsSecurityType(),
            notificationsPerDay = reminderSettings.notificationsPerDay,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(WHILE_SUBSCRIBED_TIMEOUT_MS),
        initialValue = baseState,
    )

    private companion object {
        private const val WHILE_SUBSCRIBED_TIMEOUT_MS = 5_000L
        private const val DEFAULT_NOTIFICATIONS_PER_DAY = 3
    }
}

data class SettingsUiState(
    val currencyCode: String,
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

private fun CountryProvider.currencyCode(): String {
    val locale = Locale.Builder()
        .setLanguage("en")
        .setRegion(getCountryCode())
        .build()
    return runCatching {
        Currency.getInstance(locale).currencyCode
    }.getOrDefault(getCountryCode())
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
