// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.scheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.moneytrack.expense.domain.usecase.ProcessRecurringExpenseUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecurringExpenseReceiver : BroadcastReceiver() {

    @Inject
    lateinit var processRecurringExpenseUseCase: ProcessRecurringExpenseUseCase

    override fun onReceive(
        context: Context,
        intent: Intent?,
    ) {
        val recurringExpenseId = intent?.getLongExtra(
            RecurringExpenseScheduler.EXTRA_RECURRING_EXPENSE_ID,
            INVALID_RECURRING_EXPENSE_ID,
        ) ?: INVALID_RECURRING_EXPENSE_ID
        if (recurringExpenseId == INVALID_RECURRING_EXPENSE_ID) return

        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            val nextSchedule = processRecurringExpenseUseCase(recurringExpenseId)
            if (nextSchedule == null) {
                RecurringExpenseScheduler.cancel(
                    context = context,
                    recurringExpenseId = recurringExpenseId,
                )
            } else {
                RecurringExpenseScheduler.schedule(
                    context = context,
                    recurringExpenseId = nextSchedule.id,
                    triggerAtMillis = nextSchedule.nextRunAtEpochMillis,
                )
            }
            pendingResult.finish()
        }
    }

    private companion object {
        private const val INVALID_RECURRING_EXPENSE_ID = -1L
    }
}
