package com.moneytrack.feature

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FirebaseFeatureManagerTest {

    private val remoteConfig = mockk<FirebaseRemoteConfig>()
    private val manager = FirebaseFeatureManager(remoteConfig)

    @Test
    fun isEnabled_returnsTrue_whenRemoteConfigIsTrue() {
        every { remoteConfig.getBoolean(FeatureFlag.NEW_HOME.name) } returns true

        assertTrue(manager.isEnabled(FeatureFlag.NEW_HOME))
    }

    @Test
    fun isEnabled_returnsFalse_whenRemoteConfigIsFalse() {
        every { remoteConfig.getBoolean(FeatureFlag.KILL_SWITCH.name) } returns false

        assertFalse(manager.isEnabled(FeatureFlag.KILL_SWITCH))
    }
}
