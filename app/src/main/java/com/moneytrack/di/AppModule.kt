// Copyright (c) 2026 shyakdas

package com.moneytrack.di

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.moneytrack.feature.FeatureManager
import com.moneytrack.feature.FirebaseFeatureManager
import com.moneytrack.locale.CountryProvider
import com.moneytrack.locale.DeviceCountryProvider
import com.moneytrack.remote.RemoteConfigProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig = RemoteConfigProvider.create()

    @Provides
    @Singleton
    fun provideFeatureManager(
        firebaseFeatureManager: FirebaseFeatureManager,
    ): FeatureManager = firebaseFeatureManager

    @Provides
    @Singleton
    fun provideCountryProvider(
        deviceCountryProvider: DeviceCountryProvider,
    ): CountryProvider = deviceCountryProvider
}
