// Copyright (c) 2026 shyakdas

package com.moneytrack.remote

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.moneytrack.BuildConfig

object RemoteConfigProvider {
    private const val DEV_FETCH_INTERVAL_SECONDS = 0L
    private const val PROD_FETCH_INTERVAL_SECONDS = 3600L

    fun create(): FirebaseRemoteConfig {

        val remoteConfig = FirebaseRemoteConfig.getInstance()

        val settings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(
                if (BuildConfig.ENVIRONMENT == "DEV") {
                    DEV_FETCH_INTERVAL_SECONDS
                } else {
                    PROD_FETCH_INTERVAL_SECONDS
                },
            ).build()

        remoteConfig.setConfigSettingsAsync(settings)

        // Default fallback values
        remoteConfig.setDefaultsAsync(
            mapOf(
                "NEW_HOME" to false,
                "EXPORT_TRANSACTIONS" to false,
                "EXPERIMENTAL_CHARTS" to false,
                "KILL_SWITCH" to true
            )
        )

        remoteConfig.fetchAndActivate()

        return remoteConfig
    }
}
