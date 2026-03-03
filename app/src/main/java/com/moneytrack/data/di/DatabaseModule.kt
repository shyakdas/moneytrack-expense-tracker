package com.moneytrack.data.di

import android.content.Context
import androidx.room.Room
import com.moneytrack.data.local.db.MoneyTrackDatabase
import com.moneytrack.data.local.db.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATABASE_NAME = "moneytrack.db"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMoneyTrackDatabase(
        @ApplicationContext context: Context,
    ): MoneyTrackDatabase = Room.databaseBuilder(
        context,
        MoneyTrackDatabase::class.java,
        DATABASE_NAME,
    )
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()

    @Provides
    fun provideTransactionDao(
        database: MoneyTrackDatabase,
    ): TransactionDao = database.transactionDao()
}
