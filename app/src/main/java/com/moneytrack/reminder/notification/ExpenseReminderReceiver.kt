// Copyright (c) 2026 shyakdas

package com.moneytrack.reminder.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.stringPreferencesKey
import com.moneytrack.data.local.appDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ExpenseReminderReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent?,
    ) {
        val hour = intent?.getIntExtra(
            ExpenseReminderScheduler.EXTRA_REMINDER_HOUR,
            DEFAULT_FALLBACK_HOUR,
        ) ?: DEFAULT_FALLBACK_HOUR
        val minute = intent?.getIntExtra(
            ExpenseReminderScheduler.EXTRA_REMINDER_MINUTE,
            DEFAULT_FALLBACK_MINUTE,
        ) ?: DEFAULT_FALLBACK_MINUTE

        // Always reschedule first so future reminders continue even if this one cannot be shown.
        ExpenseReminderScheduler.schedule(
            context = context,
            hourOfDay = hour,
            minute = minute,
        )

        val message = runBlocking {
            context.appDataStore.data.first()[REMINDER_MESSAGE_KEY]
        }
        ExpenseReminderNotification.show(
            context = context,
            notificationId = hour,
            message = message,
        )
    }

    private companion object {
        const val DEFAULT_FALLBACK_HOUR = 21
        const val DEFAULT_FALLBACK_MINUTE = 0
        val REMINDER_MESSAGE_KEY = stringPreferencesKey("reminder_message")
    }
}
