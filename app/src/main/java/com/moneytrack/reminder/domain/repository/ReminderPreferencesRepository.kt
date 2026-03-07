// Copyright (c) 2026 shyakdas

package com.moneytrack.reminder.domain.repository

import com.moneytrack.reminder.domain.model.ReminderNotificationSettings
import kotlinx.coroutines.flow.Flow

interface ReminderPreferencesRepository {
    fun observePermissionPromptHandled(): Flow<Boolean>
    suspend fun setPermissionPromptHandled(handled: Boolean)
    fun observeReminderSettings(): Flow<ReminderNotificationSettings>
    suspend fun updateReminderSettings(
        notificationsPerDay: Int,
        reminderMessage: String,
    )
}
