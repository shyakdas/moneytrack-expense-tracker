// Copyright (c) 2026 shyakdas

package com.moneytrack.reminder.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object ExpenseReminderScheduler {
    const val EXTRA_REMINDER_HOUR = "extra_reminder_hour"
    const val EXTRA_REMINDER_MINUTE = "extra_reminder_minute"
    private const val MORNING_REMINDER_HOUR = 9
    private const val EVENING_REMINDER_HOUR = 18
    private const val NIGHT_REMINDER_HOUR = 22
    private const val TEST_REMINDER_HOUR = 0
    private const val TEST_REMINDER_MINUTE = 15

    private const val REQUEST_CODE_MULTIPLIER = 100

    private val reminderTimes = listOf(
        ReminderTime(hour = TEST_REMINDER_HOUR, minute = TEST_REMINDER_MINUTE),
        ReminderTime(hour = MORNING_REMINDER_HOUR, minute = 0),
        ReminderTime(hour = EVENING_REMINDER_HOUR, minute = 0),
        ReminderTime(hour = NIGHT_REMINDER_HOUR, minute = 0),
    )

    fun scheduleAll(context: Context) {
        reminderTimes.forEach { reminderTime ->
            schedule(
                context = context,
                hourOfDay = reminderTime.hour,
                minute = reminderTime.minute,
            )
        }
    }

    fun schedule(
        context: Context,
        hourOfDay: Int,
        minute: Int,
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerAtMillis = nextTriggerAtMillis(hourOfDay = hourOfDay, minute = minute)
        val pendingIntent = alarmPendingIntent(
            context = context,
            hourOfDay = hourOfDay,
            minute = minute,
        )
        alarmManager.cancel(pendingIntent)
        val canUseExactAlarm = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            alarmManager.canScheduleExactAlarms()

        if (canUseExactAlarm) {
            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent,
                )
            } catch (_: SecurityException) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent,
                )
            }
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent,
            )
        }
    }

    private fun nextTriggerAtMillis(
        hourOfDay: Int,
        minute: Int,
    ): Long {
        val now = Calendar.getInstance()
        val trigger = Calendar.getInstance().apply {
            timeInMillis = now.timeInMillis
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (trigger.timeInMillis <= now.timeInMillis) {
            trigger.add(Calendar.DAY_OF_YEAR, 1)
        }
        return trigger.timeInMillis
    }

    private fun alarmPendingIntent(
        context: Context,
        hourOfDay: Int,
        minute: Int,
    ): PendingIntent {
        val intent = Intent(context, ExpenseReminderReceiver::class.java).apply {
            putExtra(EXTRA_REMINDER_HOUR, hourOfDay)
            putExtra(EXTRA_REMINDER_MINUTE, minute)
        }
        return PendingIntent.getBroadcast(
            context,
            (hourOfDay * REQUEST_CODE_MULTIPLIER) + minute,
            intent,
            PENDING_INTENT_FLAGS,
        )
    }

    private data class ReminderTime(
        val hour: Int,
        val minute: Int,
    )

    private const val PENDING_INTENT_FLAGS =
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
}
