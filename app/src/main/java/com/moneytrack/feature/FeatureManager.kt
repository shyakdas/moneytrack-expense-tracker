package com.moneytrack.feature

interface FeatureManager {
    fun isEnabled(feature: FeatureFlag): Boolean
}
