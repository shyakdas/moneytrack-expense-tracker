package com.moneytrack.remote

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.moneytrack.BuildConfig

object RemoteConfigProvider {

    fun create(): FirebaseRemoteConfig {

        val remoteConfig = FirebaseRemoteConfig.getInstance()

        val settings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(
                if (BuildConfig.ENVIRONMENT == "DEV") {
                    0      // Always fetch fresh in dev
                } else {
                    3600   // 1 hour cache in prod
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
