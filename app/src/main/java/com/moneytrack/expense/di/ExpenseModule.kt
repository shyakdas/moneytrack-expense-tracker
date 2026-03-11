// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.di

import com.moneytrack.expense.data.repository.CategoryRepositoryImpl
import com.moneytrack.expense.domain.repository.CategoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ExpenseModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl,
    ): CategoryRepository
}

