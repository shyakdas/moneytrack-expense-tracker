// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import com.moneytrack.BuildConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AboutViewModelTest {

    @Test
    fun uiState_exposesBuildVersionName() {
        val viewModel = AboutViewModel()

        assertEquals(BuildConfig.VERSION_NAME, viewModel.uiState.value.versionName)
    }

    @Test
    fun uiState_versionName_isNotBlank() {
        val viewModel = AboutViewModel()

        assertTrue(viewModel.uiState.value.versionName.isNotBlank())
    }
}
