// Copyright (c) 2026 shyakdas

package com.moneytrack.pinauth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moneytrack.R
import com.moneytrack.pinauth.presentation.PinAuthAction
import com.moneytrack.pinauth.presentation.PinAuthEvent
import com.moneytrack.pinauth.presentation.PinAuthMode
import com.moneytrack.pinauth.presentation.PinAuthScreen
import com.moneytrack.pinauth.presentation.PinAuthUiState
import com.moneytrack.pinauth.presentation.PinAuthViewModel
import com.moneytrack.security.domain.model.PinSetupStatus
import com.moneytrack.security.domain.repository.SecurityRepository
import com.moneytrack.security.domain.usecase.GetPinSetupStatusUseCase
import com.moneytrack.security.domain.usecase.VerifyPinUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ui.theme.MoneyTrackTheme

@RunWith(AndroidJUnit4::class)
class PinAuthScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun pinMode_showsTitleAndError() {
        composeRule.setContent {
            MoneyTrackTheme(darkTheme = false) {
                PinAuthScreen(
                    uiState = PinAuthUiState(
                        isLoading = false,
                        mode = PinAuthMode.PIN,
                        showPinError = true,
                    ),
                    onAction = {},
                )
            }
        }

        composeRule.onNodeWithText(string(R.string.pin_auth_title)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.pin_auth_error)).assertIsDisplayed()
    }

    @Test
    fun biometricMode_clickRetry_dispatchesRequestBiometricAction() {
        var lastAction: PinAuthAction? = null
        composeRule.setContent {
            MoneyTrackTheme(darkTheme = false) {
                PinAuthScreen(
                    uiState = PinAuthUiState(
                        isLoading = false,
                        mode = PinAuthMode.BIOMETRIC,
                    ),
                    onAction = { action -> lastAction = action },
                )
            }
        }

        composeRule.onNodeWithText(string(R.string.pin_auth_biometric_retry)).performClick()
        assertTrue(lastAction is PinAuthAction.RequestBiometricAuth)
    }

    @Test
    fun biometricMode_showsContent() {
        composeRule.setContent {
            MoneyTrackTheme(darkTheme = false) {
                PinAuthScreen(
                    uiState = PinAuthUiState(
                        isLoading = false,
                        mode = PinAuthMode.BIOMETRIC,
                    ),
                    onAction = {},
                )
            }
        }

        composeRule.onNodeWithText(string(R.string.pin_auth_biometric_title)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.pin_auth_biometric_subtitle)).assertIsDisplayed()
    }

    @Test
    fun enteringFourDigits_autoAuthenticates_withoutSubmitTap() {
        val viewModel = createPinAuthViewModel(savedPin = "1234")

        composeRule.setContent {
            val uiState by viewModel.uiState.collectAsState()
            MoneyTrackTheme(darkTheme = false) {
                PinAuthScreen(
                    uiState = uiState,
                    onAction = viewModel::onAction,
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("1").performClick()
        composeRule.onNodeWithText("2").performClick()
        composeRule.onNodeWithText("3").performClick()
        composeRule.onNodeWithText("4").performClick()
        composeRule.waitForIdle()

        val event = runBlocking {
            withTimeout(2000) { viewModel.events.first() }
        }
        assertEquals(PinAuthEvent.Authenticated, event)
    }

    private fun createPinAuthViewModel(savedPin: String): PinAuthViewModel {
        val repository = object : SecurityRepository {
            override fun observePinSetupStatus(): Flow<PinSetupStatus> = flowOf(PinSetupStatus.PIN_ENABLED)
            override suspend fun setPinSetupStatus(status: PinSetupStatus) = Unit
            override suspend fun savePinHash(pinHash: String) = Unit
            override suspend fun getPinHash(): String = sha256(savedPin)
            override suspend fun clearPinHash() = Unit
        }
        return PinAuthViewModel(
            getPinSetupStatusUseCase = GetPinSetupStatusUseCase(repository),
            verifyPinUseCase = VerifyPinUseCase(repository),
        )
    }

    private fun sha256(value: String): String {
        val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(value.toByteArray())
        return bytes.joinToString(separator = "") { byte ->
            "%02x".format(byte)
        }
    }

    private fun string(resId: Int): String =
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext.getString(resId)
}
