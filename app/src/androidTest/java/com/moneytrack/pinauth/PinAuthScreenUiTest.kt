package com.moneytrack.pinauth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moneytrack.R
import com.moneytrack.pinauth.presentation.PinAuthAction
import com.moneytrack.pinauth.presentation.PinAuthMode
import com.moneytrack.pinauth.presentation.PinAuthScreen
import com.moneytrack.pinauth.presentation.PinAuthUiState
import org.junit.Assert.assertTrue
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

    private fun string(resId: Int): String =
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext.getString(resId)
}
