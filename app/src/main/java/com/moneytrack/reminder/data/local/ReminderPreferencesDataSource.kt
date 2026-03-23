// Copyright (c) 2026 shyakdas

package com.moneytrack.reminder.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.moneytrack.data.local.appDataStore
import com.moneytrack.locale.AppCurrencyManager
import com.moneytrack.locale.CurrencyCatalog
import com.moneytrack.reminder.domain.model.ReminderNotificationSettings
import com.moneytrack.reminder.notification.ReminderSchedule
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class ReminderPreferencesDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val appCurrencyManager: AppCurrencyManager,
    private val currencyCatalog: CurrencyCatalog,
) {
    private companion object {
        val PERMISSION_PROMPT_HANDLED_KEY = booleanPreferencesKey("notification_permission_prompt_handled")
        val REMINDER_NOTIFICATIONS_PER_DAY_KEY = intPreferencesKey("reminder_notifications_per_day")
        val REMINDER_MESSAGE_KEY = stringPreferencesKey("reminder_message")
    }

    val permissionPromptHandledFlow: Flow<Boolean> =
        context.appDataStore.data.map { preferences ->
            preferences[PERMISSION_PROMPT_HANDLED_KEY] ?: false
        }

    val reminderSettingsFlow: Flow<ReminderNotificationSettings> =
        context.appDataStore.data.map { preferences ->
            val defaultReminderMessage = defaultReminderMessage(
                currencyCode = appCurrencyManager.currentCurrencyCode(),
                currencyCatalog = currencyCatalog,
            )
            ReminderNotificationSettings(
                notificationsPerDay = preferences[REMINDER_NOTIFICATIONS_PER_DAY_KEY]
                    ?: ReminderSchedule.DEFAULT_NOTIFICATIONS_PER_DAY,
                reminderMessage = preferences[REMINDER_MESSAGE_KEY] ?: defaultReminderMessage,
            )
        }

    suspend fun setPermissionPromptHandled(handled: Boolean) {
        context.appDataStore.edit { preferences ->
            preferences[PERMISSION_PROMPT_HANDLED_KEY] = handled
        }
    }

    suspend fun updateReminderSettings(
        notificationsPerDay: Int,
        reminderMessage: String,
    ) {
        context.appDataStore.edit { preferences ->
            preferences[REMINDER_NOTIFICATIONS_PER_DAY_KEY] =
                ReminderSchedule.normalize(notificationsPerDay)
            preferences[REMINDER_MESSAGE_KEY] = reminderMessage
        }
    }
}

private fun defaultReminderMessage(
    currencyCode: String,
    currencyCatalog: CurrencyCatalog,
): String {
    val currencySymbol = currencyCatalog.find(currencyCode)?.symbol ?: currencyCode
    return "Add your expenses in MoneyTrack to stay on top of your $currencySymbol budget."
}
