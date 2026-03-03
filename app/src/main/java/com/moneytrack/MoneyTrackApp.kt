package com.moneytrack

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MoneyTrackApp : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}
