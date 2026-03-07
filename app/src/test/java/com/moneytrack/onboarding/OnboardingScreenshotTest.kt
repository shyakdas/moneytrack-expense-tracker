// Copyright (c) 2026 shyakdas

package com.moneytrack.onboarding

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import androidx.compose.runtime.Composable
import org.junit.Rule
import org.junit.Test
import com.moneytrack.R
import com.moneytrack.onboarding.domain.model.OnboardingPage
import com.moneytrack.onboarding.presentation.OnboardingScreen
import ui.theme.MoneyTrackTheme

class OnboardingScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5)

    @Test
    fun onboarding_page1_light() {
        paparazzi.snapshot {
            OnboardingSnapshotContent(
                initialPage = 0,
                darkTheme = false,
            )
        }
    }

    @Test
    fun onboarding_page2_light() {
        paparazzi.snapshot {
            OnboardingSnapshotContent(
                initialPage = 1,
                darkTheme = false,
            )
        }
    }

    @Test
    fun onboarding_page3_light() {
        paparazzi.snapshot {
            OnboardingSnapshotContent(
                initialPage = 2,
                darkTheme = false,
            )
        }
    }

    @Test
    fun onboarding_page1_dark() {
        paparazzi.snapshot {
            OnboardingSnapshotContent(
                initialPage = 0,
                darkTheme = true,
            )
        }
    }

    @Test
    fun onboarding_page2_dark() {
        paparazzi.snapshot {
            OnboardingSnapshotContent(
                initialPage = 1,
                darkTheme = true,
            )
        }
    }

    @Test
    fun onboarding_page3_dark() {
        paparazzi.snapshot {
            OnboardingSnapshotContent(
                initialPage = 2,
                darkTheme = true,
            )
        }
    }
}

@Composable
private fun OnboardingSnapshotContent(
    initialPage: Int,
    darkTheme: Boolean,
) {
    MoneyTrackTheme(darkTheme = darkTheme) {
        OnboardingScreen(
            pages = listOf(
                OnboardingPage(
                    imageRes = com.moneytrack.designsystem.R.drawable.variant_gain_total_control_of_your_money,
                    titleRes = R.string.onboarding_title_control,
                    descriptionRes = R.string.onboarding_desc_control,
                ),
                OnboardingPage(
                    imageRes = com.moneytrack.designsystem.R.drawable.variant_know_where_your_money_goes,
                    titleRes = R.string.onboarding_title_track,
                    descriptionRes = R.string.onboarding_desc_track,
                ),
                OnboardingPage(
                    imageRes = com.moneytrack.designsystem.R.drawable.variant_planning_ahead,
                    titleRes = R.string.onboarding_title_plan,
                    descriptionRes = R.string.onboarding_desc_plan,
                ),
            ),
            onFinished = {},
            initialPage = initialPage,
        )
    }
}
