// Copyright (c) 2026 shyakdas

package com.moneytrack

import android.app.Application
import com.moneytrack.reminder.notification.ExpenseReminderNotification
import com.moneytrack.reminder.notification.ExpenseReminderScheduler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MoneyTrackApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ExpenseReminderNotification.ensureChannel(this)
        ExpenseReminderScheduler.scheduleAll(this)
    }
}
