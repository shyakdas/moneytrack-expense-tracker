// Copyright (c) 2026 shyakdas

package com.moneytrack.reminder.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.datastore.preferences.core.intPreferencesKey
import com.moneytrack.data.local.appDataStore
import java.util.Calendar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object ExpenseReminderScheduler {
    const val EXTRA_REMINDER_HOUR = "extra_reminder_hour"
    const val EXTRA_REMINDER_MINUTE = "extra_reminder_minute"

    private const val REQUEST_CODE_MULTIPLIER = 100

    fun scheduleAll(context: Context) {
        scheduleConfigured(
            context = context,
            notificationsPerDay = currentNotificationsPerDay(context),
        )
    }

    fun scheduleConfigured(
        context: Context,
        notificationsPerDay: Int,
    ) {
        cancelAll(context)
        ReminderSchedule.timesFor(notificationsPerDay).forEach { reminderTime ->
            schedule(
                context = context,
                hourOfDay = reminderTime.hour,
                minute = reminderTime.minute,
            )
        }
    }

    fun cancelAll(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        ReminderSchedule.allSupportedTimes().forEach { reminderTime ->
            alarmManager.cancel(
                alarmPendingIntent(
                    context = context,
                    hourOfDay = reminderTime.hour,
                    minute = reminderTime.minute,
                ),
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

    private fun currentNotificationsPerDay(context: Context): Int = runBlocking {
        val notificationsPerDay = context.appDataStore.data.first()[REMINDER_NOTIFICATIONS_PER_DAY_KEY]
        ReminderSchedule.normalize(notificationsPerDay ?: ReminderSchedule.DEFAULT_NOTIFICATIONS_PER_DAY)
    }

    private const val PENDING_INTENT_FLAGS =
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    private val REMINDER_NOTIFICATIONS_PER_DAY_KEY =
        intPreferencesKey("reminder_notifications_per_day")
}
