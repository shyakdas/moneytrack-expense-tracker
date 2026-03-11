// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.scheduler

import android.content.Context
import com.moneytrack.expense.domain.usecase.GetActiveRecurringSchedulesUseCase
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Singleton
class RecurringExpenseRescheduler @Inject constructor(
    private val getActiveRecurringSchedulesUseCase: GetActiveRecurringSchedulesUseCase,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun rescheduleAll(context: Context) {
        scope.launch {
            RecurringExpenseScheduler.scheduleAll(
                context = context,
                schedules = getActiveRecurringSchedulesUseCase(),
            )
        }
    }
}
