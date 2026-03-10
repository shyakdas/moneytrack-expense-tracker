// Copyright (c) 2026 shyakdas

package com.moneytrack.reminder.domain.usecase

import com.moneytrack.reminder.domain.model.ReminderNotificationSettings
import com.moneytrack.reminder.domain.repository.ReminderPreferencesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveReminderNotificationSettingsUseCase @Inject constructor(
    private val reminderPreferencesRepository: ReminderPreferencesRepository,
) {
    operator fun invoke(): Flow<ReminderNotificationSettings> =
        reminderPreferencesRepository.observeReminderSettings()
}
