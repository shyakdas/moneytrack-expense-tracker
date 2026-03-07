// Copyright (c) 2026 shyakdas

package com.moneytrack.onboarding.di

import com.moneytrack.onboarding.data.repository.OnboardingRepositoryImpl
import com.moneytrack.onboarding.domain.repository.OnboardingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OnboardingModule {

    @Binds
    @Singleton
    abstract fun bindOnboardingRepository(
        impl: OnboardingRepositoryImpl,
    ): OnboardingRepository
}
