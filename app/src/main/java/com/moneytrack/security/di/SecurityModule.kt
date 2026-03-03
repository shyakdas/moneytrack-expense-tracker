package com.moneytrack.security.di

import com.moneytrack.security.data.repository.SecurityRepositoryImpl
import com.moneytrack.security.domain.repository.SecurityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {

    @Binds
    @Singleton
    abstract fun bindSecurityRepository(
        impl: SecurityRepositoryImpl,
    ): SecurityRepository
}
