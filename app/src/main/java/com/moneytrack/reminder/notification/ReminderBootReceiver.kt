// Copyright (c) 2026 shyakdas

package com.moneytrack.reminder.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.moneytrack.expense.scheduler.RecurringExpenseRescheduler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReminderBootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var recurringExpenseRescheduler: RecurringExpenseRescheduler

    override fun onReceive(
        context: Context,
        intent: Intent?,
    ) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
                -> {
                    ExpenseReminderNotification.ensureChannel(context)
                    ExpenseReminderScheduler.scheduleAll(context)
                    recurringExpenseRescheduler.rescheduleAll(context)
                }
        }
    }
}
