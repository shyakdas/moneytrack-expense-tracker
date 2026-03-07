// Copyright (c) 2026 shyakdas

package com.moneytrack.feature

interface FeatureManager {
    fun isEnabled(feature: FeatureFlag): Boolean
}
