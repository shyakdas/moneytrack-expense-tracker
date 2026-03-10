// Copyright (c) 2026 shyakdas

package com.moneytrack.reminder.domain.model

data class ReminderNotificationSettings(
    val notificationsPerDay: Int,
    val reminderMessage: String,
)
