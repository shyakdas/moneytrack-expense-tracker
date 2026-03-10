// Copyright (c) 2026 shyakdas

package com.moneytrack.home.di

import com.moneytrack.home.data.repository.BudgetRepositoryImpl
import com.moneytrack.home.domain.repository.BudgetRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {

    @Binds
    @Singleton
    abstract fun bindBudgetRepository(
        impl: BudgetRepositoryImpl,
    ): BudgetRepository
}
