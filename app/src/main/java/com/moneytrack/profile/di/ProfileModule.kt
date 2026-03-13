// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.di

import com.moneytrack.profile.data.repository.ProfileRepositoryImpl
import com.moneytrack.profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        impl: ProfileRepositoryImpl,
    ): ProfileRepository
}
