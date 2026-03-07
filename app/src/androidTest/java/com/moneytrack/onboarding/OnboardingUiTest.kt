// Copyright (c) 2026 shyakdas

package com.moneytrack.onboarding

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.moneytrack.MainActivity
import com.moneytrack.R

@RunWith(AndroidJUnit4::class)
class OnboardingUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun firstPage_showsTitle_nextButton_andHidesPreviousButton() {
        composeRule.onNodeWithText(string(R.string.onboarding_title_control)).assertIsDisplayed()
        composeRule.onNodeWithContentDescription(string(R.string.onboarding_next)).assertIsDisplayed()
        composeRule
            .onAllNodesWithContentDescription(string(R.string.onboarding_prev))
            .assertCountEquals(0)
    }

    @Test
    fun clickingNext_movesToSecondPage_andClickingPreviousReturnsToFirst() {
        composeRule.onNodeWithContentDescription(string(R.string.onboarding_next)).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText(string(R.string.onboarding_title_track)).assertIsDisplayed()
        composeRule.onNodeWithContentDescription(string(R.string.onboarding_prev)).assertIsDisplayed()

        composeRule.onNodeWithContentDescription(string(R.string.onboarding_prev)).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText(string(R.string.onboarding_title_control)).assertIsDisplayed()
    }

    @Test
    fun clickingNextToLastPage_showsFinishButton() {
        composeRule.onNodeWithContentDescription(string(R.string.onboarding_next)).performClick()
        composeRule.onNodeWithContentDescription(string(R.string.onboarding_next)).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText(string(R.string.onboarding_title_plan)).assertIsDisplayed()
        composeRule.onNodeWithContentDescription(string(R.string.onboarding_finish)).assertIsDisplayed()
    }

    private fun string(@StringRes resId: Int): String = composeRule.activity.getString(resId)
}
