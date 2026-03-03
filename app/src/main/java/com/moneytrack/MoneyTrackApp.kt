package com.moneytrack

import android.app.Application
import com.moneytrack.feature.FeatureManager
import com.moneytrack.feature.FirebaseFeatureManager
import com.moneytrack.remote.RemoteConfigProvider

class MoneyTrackApp : Application() {

    lateinit var featureManager: FeatureManager
        private set

    override fun onCreate() {
        super.onCreate()

        val remoteConfig = RemoteConfigProvider.create()
        featureManager = FirebaseFeatureManager(remoteConfig)
    }
}
