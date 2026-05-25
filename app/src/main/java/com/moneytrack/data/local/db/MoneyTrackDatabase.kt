// Copyright (c) 2026 shyakdas

package com.moneytrack.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.moneytrack.data.local.db.dao.BudgetDao
import com.moneytrack.data.local.db.dao.CategoryDao
import com.moneytrack.data.local.db.dao.CurrencyPreferenceDao
import com.moneytrack.data.local.db.dao.RecurringExpenseDao
import com.moneytrack.data.local.db.dao.TransactionDao
import com.moneytrack.data.local.db.entity.BudgetEntity
import com.moneytrack.data.local.db.entity.CategoryEntity
import com.moneytrack.data.local.db.entity.CurrencyPreferenceEntity
import com.moneytrack.data.local.db.entity.RecurringExpenseEntity
import com.moneytrack.data.local.db.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        BudgetEntity::class,
        CategoryEntity::class,
        CurrencyPreferenceEntity::class,
        RecurringExpenseEntity::class,
    ],
    version = 6,
    exportSchema = true,
)
abstract class MoneyTrackDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun categoryDao(): CategoryDao
    abstract fun currencyPreferenceDao(): CurrencyPreferenceDao
    abstract fun recurringExpenseDao(): RecurringExpenseDao
}
