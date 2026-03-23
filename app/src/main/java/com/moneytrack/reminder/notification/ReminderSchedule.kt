// Copyright (c) 2026 shyakdas

package com.moneytrack.reminder.notification

internal object ReminderSchedule {
    private const val ONE_NOTIFICATION_PER_DAY = 1
    private const val THREE_NOTIFICATIONS_PER_DAY = 3
    private const val FIVE_NOTIFICATIONS_PER_DAY = 5

    private val onePerDay = listOf(ReminderTime(hour = 21, minute = 0))
    private val threePerDay = listOf(
        ReminderTime(hour = 9, minute = 0),
        ReminderTime(hour = 15, minute = 0),
        ReminderTime(hour = 21, minute = 0),
    )
    private val fivePerDay = listOf(
        ReminderTime(hour = 9, minute = 0),
        ReminderTime(hour = 12, minute = 0),
        ReminderTime(hour = 15, minute = 0),
        ReminderTime(hour = 18, minute = 0),
        ReminderTime(hour = 21, minute = 0),
    )

    fun normalize(notificationsPerDay: Int): Int =
        when (notificationsPerDay) {
            ONE_NOTIFICATION_PER_DAY,
            THREE_NOTIFICATIONS_PER_DAY,
            FIVE_NOTIFICATIONS_PER_DAY,
                -> notificationsPerDay
            else -> DEFAULT_NOTIFICATIONS_PER_DAY
        }

    fun timesFor(notificationsPerDay: Int): List<ReminderTime> =
        when (normalize(notificationsPerDay)) {
            ONE_NOTIFICATION_PER_DAY -> onePerDay
            FIVE_NOTIFICATIONS_PER_DAY -> fivePerDay
            else -> threePerDay
        }

    fun allSupportedTimes(): List<ReminderTime> = fivePerDay

    const val DEFAULT_NOTIFICATIONS_PER_DAY = THREE_NOTIFICATIONS_PER_DAY
}

internal data class ReminderTime(
    val hour: Int,
    val minute: Int,
)
