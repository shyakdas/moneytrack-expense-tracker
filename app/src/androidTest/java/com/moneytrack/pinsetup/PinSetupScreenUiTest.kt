// Copyright (c) 2026 shyakdas

package com.moneytrack.pinsetup

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moneytrack.R
import com.moneytrack.pinsetup.presentation.PinSetupAction
import com.moneytrack.pinsetup.presentation.PinSetupScreen
import com.moneytrack.pinsetup.presentation.PinSetupStage
import com.moneytrack.pinsetup.presentation.PinSetupUiState
import com.moneytrack.pinsetup.presentation.PinSetupViewModel
import com.moneytrack.security.domain.model.PinSetupStatus
import com.moneytrack.security.domain.repository.SecurityRepository
import com.moneytrack.security.domain.usecase.CompletePinSetupWithBiometricUseCase
import com.moneytrack.security.domain.usecase.CompletePinSetupWithPinUseCase
import com.moneytrack.security.domain.usecase.SkipPinSetupUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ui.theme.MoneyTrackTheme

@RunWith(AndroidJUnit4::class)
class PinSetupScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun successStage_showsSuccessMessage() {
        composeRule.setContent {
            MoneyTrackTheme(darkTheme = false) {
                PinSetupScreen(
                    uiState = PinSetupUiState(stage = PinSetupStage.SUCCESS),
                    onAction = {},
                )
            }
        }

        composeRule.onNodeWithText(string(R.string.pin_setup_success_title)).assertIsDisplayed()
    }

    @Test
    fun lockedOutState_clickForgotPin_dispatchesAction() {
        var lastAction: PinSetupAction? = null
        composeRule.setContent {
            MoneyTrackTheme(darkTheme = false) {
                PinSetupScreen(
                    uiState = PinSetupUiState(
                        stage = PinSetupStage.CONFIRM_PIN,
                        showPinMismatch = true,
                        failedAttempts = 3,
                        isLockedOut = true,
                    ),
                    onAction = { action -> lastAction = action },
                )
            }
        }

        composeRule.onNodeWithText(string(R.string.pin_forgot_pin)).assertIsDisplayed().performClick()
        assertTrue(lastAction is PinSetupAction.ForgotPin)
    }

    @Test
    fun introState_clickSetPin_dispatchesSelectPinAction() {
        var lastAction: PinSetupAction? = null
        composeRule.setContent {
            MoneyTrackTheme(darkTheme = false) {
                PinSetupScreen(
                    uiState = PinSetupUiState(stage = PinSetupStage.INTRO),
                    onAction = { action -> lastAction = action },
                )
            }
        }

        composeRule.onNodeWithText(string(R.string.pin_intro_set_pin)).performClick()
        assertEquals(PinSetupAction.SelectPin, lastAction)
    }

    @Test
    fun enteringFourDigits_autoMovesToConfirm_withoutSubmitTap() {
        val viewModel = createPinSetupViewModel()

        composeRule.setContent {
            val uiState by viewModel.uiState.collectAsState()
            MoneyTrackTheme(darkTheme = false) {
                PinSetupScreen(
                    uiState = uiState,
                    onAction = viewModel::onAction,
                )
            }
        }

        composeRule.onNodeWithText(string(R.string.pin_intro_set_pin)).performClick()
        composeRule.onNodeWithText("1").performClick()
        composeRule.onNodeWithText("2").performClick()
        composeRule.onNodeWithText("3").performClick()
        composeRule.onNodeWithText("4").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText(string(R.string.pin_confirm_title)).assertIsDisplayed()
    }

    private fun createPinSetupViewModel(): PinSetupViewModel {
        val repository = object : SecurityRepository {
            override fun observePinSetupStatus(): Flow<PinSetupStatus> = flowOf(PinSetupStatus.NOT_STARTED)
            override suspend fun setPinSetupStatus(status: PinSetupStatus) = Unit
            override suspend fun savePinHash(pinHash: String) = Unit
            override suspend fun getPinHash(): String? = null
        }
        return PinSetupViewModel(
            completePinSetupWithPinUseCase = CompletePinSetupWithPinUseCase(repository),
            completePinSetupWithBiometricUseCase = CompletePinSetupWithBiometricUseCase(repository),
            skipPinSetupUseCase = SkipPinSetupUseCase(repository),
        )
    }

    private fun string(resId: Int): String =
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext.getString(resId)
}
