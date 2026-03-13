// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.moneytrack.data.local.appDataStore
import com.moneytrack.data.local.db.MoneyTrackDatabase
import com.moneytrack.expense.scheduler.RecurringExpenseScheduler
import com.moneytrack.reminder.notification.ExpenseReminderScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalAppDataResetter @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val database: MoneyTrackDatabase,
) {
    suspend fun clearAllLocalData() {
        val recurringExpenseIds = database.recurringExpenseDao().getAll().map { recurringExpense ->
            recurringExpense.id
        }

        RecurringExpenseScheduler.cancelAll(
            context = context,
            recurringExpenseIds = recurringExpenseIds,
        )
        ExpenseReminderScheduler.cancelAll(context = context)

        context.appDataStore.edit { preferences ->
            preferences.clear()
        }

        database.clearAllTables()
    }
}
