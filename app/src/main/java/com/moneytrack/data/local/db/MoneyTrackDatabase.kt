package com.moneytrack.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.moneytrack.data.local.db.dao.TransactionDao
import com.moneytrack.data.local.db.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class MoneyTrackDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}
