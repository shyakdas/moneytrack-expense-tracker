// Copyright (c) 2026 shyakdas

package com.moneytrack.reminder.domain.usecase

import com.moneytrack.reminder.domain.repository.ReminderPreferencesRepository
import javax.inject.Inject

class UpdateReminderNotificationSettingsUseCase @Inject constructor(
    private val reminderPreferencesRepository: ReminderPreferencesRepository,
) {
    suspend operator fun invoke(
        notificationsPerDay: Int,
        reminderMessage: String,
    ) {
        reminderPreferencesRepository.updateReminderSettings(
            notificationsPerDay = notificationsPerDay,
            reminderMessage = reminderMessage,
        )
    }
}
