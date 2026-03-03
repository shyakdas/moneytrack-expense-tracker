package com.moneytrack.feature

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import javax.inject.Inject

class FirebaseFeatureManager @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig
) : FeatureManager {

    override fun isEnabled(feature: FeatureFlag): Boolean {
        return remoteConfig.getBoolean(feature.name)
    }
}
