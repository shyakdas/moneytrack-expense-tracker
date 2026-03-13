// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.di

import com.moneytrack.settings.data.repository.CurrencyPreferenceRepositoryImpl
import com.moneytrack.settings.data.repository.ThemePreferenceRepositoryImpl
import com.moneytrack.settings.domain.repository.CurrencyPreferenceRepository
import com.moneytrack.settings.domain.repository.ThemePreferenceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Binds
    @Singleton
    abstract fun bindCurrencyPreferenceRepository(
        impl: CurrencyPreferenceRepositoryImpl,
    ): CurrencyPreferenceRepository

    @Binds
    @Singleton
    abstract fun bindThemePreferenceRepository(
        impl: ThemePreferenceRepositoryImpl,
    ): ThemePreferenceRepository
}
