// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.moneytrack.expense.domain.model.RecurringExpenseSchedule

object RecurringExpenseScheduler {
    const val EXTRA_RECURRING_EXPENSE_ID = "extra_recurring_expense_id"
    private const val REQUEST_CODE_OFFSET = 10_000
    private const val PENDING_INTENT_FLAGS =
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

    fun schedule(
        context: Context,
        recurringExpenseId: Long,
        triggerAtMillis: Long,
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = pendingIntent(context = context, recurringExpenseId = recurringExpenseId)
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

    fun cancel(
        context: Context,
        recurringExpenseId: Long,
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(
            pendingIntent(context = context, recurringExpenseId = recurringExpenseId),
        )
    }

    fun scheduleAll(
        context: Context,
        schedules: List<RecurringExpenseSchedule>,
    ) {
        schedules.forEach { schedule ->
            schedule(
                context = context,
                recurringExpenseId = schedule.id,
                triggerAtMillis = schedule.nextRunAtEpochMillis,
            )
        }
    }

    private fun pendingIntent(
        context: Context,
        recurringExpenseId: Long,
    ): PendingIntent {
        val intent = Intent(context, RecurringExpenseReceiver::class.java).apply {
            putExtra(EXTRA_RECURRING_EXPENSE_ID, recurringExpenseId)
        }
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_OFFSET + recurringExpenseId.toInt(),
            intent,
            PENDING_INTENT_FLAGS,
        )
    }
}
