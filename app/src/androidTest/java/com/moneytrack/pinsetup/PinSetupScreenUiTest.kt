package com.moneytrack.pinsetup

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moneytrack.R
import com.moneytrack.pinsetup.presentation.PinSetupAction
import com.moneytrack.pinsetup.presentation.PinSetupScreen
import com.moneytrack.pinsetup.presentation.PinSetupStage
import com.moneytrack.pinsetup.presentation.PinSetupUiState
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

    private fun string(resId: Int): String =
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext.getString(resId)
}
