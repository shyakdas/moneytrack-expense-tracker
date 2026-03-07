// Copyright (c) 2026 shyakdas

package com.moneytrack.reminder.di

import com.moneytrack.reminder.data.repository.ReminderPreferencesRepositoryImpl
import com.moneytrack.reminder.domain.repository.ReminderPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReminderModule {

    @Binds
    @Singleton
    abstract fun bindReminderPreferencesRepository(
        impl: ReminderPreferencesRepositoryImpl,
    ): ReminderPreferencesRepository
}
