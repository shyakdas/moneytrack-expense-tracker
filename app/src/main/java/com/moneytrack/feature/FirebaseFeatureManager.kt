package com.moneytrack.feature

import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class FirebaseFeatureManager(
    private val remoteConfig: FirebaseRemoteConfig
) : FeatureManager {

    override fun isEnabled(feature: FeatureFlag): Boolean {
        return remoteConfig.getBoolean(feature.name)
    }
}
