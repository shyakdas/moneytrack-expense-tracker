// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.moneytrack.data.local.appDataStore
import com.moneytrack.data.local.db.MoneyTrackDatabase
import com.moneytrack.expense.scheduler.RecurringExpenseScheduler
import com.moneytrack.reminder.notification.ExpenseReminderScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val EXPENSE_ATTACHMENT_CACHE_DIR = "expense_attachments"

@Singleton
class LocalAppDataResetter @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val database: MoneyTrackDatabase,
) {
    suspend fun clearAllLocalData() {
        withContext(Dispatchers.IO) {
            val recurringExpenseIds = database.recurringExpenseDao().getAll().map { recurringExpense ->
                recurringExpense.id
            }

            RecurringExpenseScheduler.cancelAll(
                context = context,
                recurringExpenseIds = recurringExpenseIds,
            )
            ExpenseReminderScheduler.cancelAll(context = context)

            database.clearAllTables()
            clearExpenseAttachmentCache()
            releasePersistedUriPermissions()

            // Clear DataStore last so app entry state flips only after the local wipe is complete.
            context.appDataStore.edit { preferences ->
                preferences.clear()
            }
        }
    }

    private fun clearExpenseAttachmentCache() {
        File(context.cacheDir, EXPENSE_ATTACHMENT_CACHE_DIR).deleteRecursively()
    }

    private fun releasePersistedUriPermissions() {
        context.contentResolver.persistedUriPermissions.forEach { permission ->
            runCatching {
                context.contentResolver.releasePersistableUriPermission(
                    permission.uri,
                    persistedPermissionFlags(permission),
                )
            }
        }
    }

    private fun persistedPermissionFlags(permission: android.content.UriPermission): Int {
        var flags = 0
        if (permission.isReadPermission) {
            flags = flags or android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        if (permission.isWritePermission) {
            flags = flags or android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        }
        return flags
    }
}
