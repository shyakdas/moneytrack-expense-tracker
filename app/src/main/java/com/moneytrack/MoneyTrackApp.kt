// Copyright (c) 2026 shyakdas

package com.moneytrack

import android.app.Application
import com.moneytrack.expense.scheduler.RecurringExpenseRescheduler
import com.moneytrack.reminder.notification.ExpenseReminderNotification
import com.moneytrack.reminder.notification.ExpenseReminderScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MoneyTrackApp : Application() {

    @Inject
    lateinit var recurringExpenseRescheduler: RecurringExpenseRescheduler

    override fun onCreate() {
        super.onCreate()
        ExpenseReminderNotification.ensureChannel(this)
        ExpenseReminderScheduler.scheduleAll(this)
        recurringExpenseRescheduler.rescheduleAll(this)
    }
}
