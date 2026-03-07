// Copyright (c) 2026 shyakdas

package com.moneytrack.reminder.data.repository

import com.moneytrack.reminder.data.local.ReminderPreferencesDataSource
import com.moneytrack.reminder.domain.model.ReminderNotificationSettings
import com.moneytrack.reminder.domain.repository.ReminderPreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class ReminderPreferencesRepositoryImpl @Inject constructor(
    private val dataSource: ReminderPreferencesDataSource,
) : ReminderPreferencesRepository {
    override fun observePermissionPromptHandled(): Flow<Boolean> = dataSource.permissionPromptHandledFlow

    override suspend fun setPermissionPromptHandled(handled: Boolean) {
        dataSource.setPermissionPromptHandled(handled = handled)
    }

    override fun observeReminderSettings(): Flow<ReminderNotificationSettings> = dataSource.reminderSettingsFlow

    override suspend fun updateReminderSettings(
        notificationsPerDay: Int,
        reminderMessage: String,
    ) {
        dataSource.updateReminderSettings(
            notificationsPerDay = notificationsPerDay,
            reminderMessage = reminderMessage,
        )
    }
}
