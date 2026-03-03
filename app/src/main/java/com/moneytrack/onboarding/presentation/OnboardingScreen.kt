@file:Suppress("MagicNumber")

package com.moneytrack.onboarding.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import com.moneytrack.R
import com.moneytrack.onboarding.domain.model.OnboardingPage
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

private const val PAGER_WEIGHT = 1f
private const val DOT_ACTIVE_ALPHA = 1f
private const val DOT_INACTIVE_ALPHA = 0.24f
private const val PRESSED_SCALE = 0.9f
private const val DEFAULT_SCALE = 1f
private const val BUTTON_ANIMATION_DURATION_MS = 120
private const val PAGER_ANIMATION_DURATION_MS = 260

@Composable
fun OnboardingScreen(
    pages: List<OnboardingPage>,
    onFinished: () -> Unit,
    initialPage: Int = 0,
) {
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { pages.size },
    )
    val coroutineScope = rememberCoroutineScope()
    val currentPage = pagerState.currentPage
    val isFirstPage = currentPage == 0
    val isLastPage = currentPage == pages.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .padding(horizontal = Dimens.spacing24),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(Dimens.spacing72))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(PAGER_WEIGHT)
                .fillMaxWidth(),
        ) { pageIndex ->
            OnboardingPageItem(page = pages[pageIndex])
        }

        PageIndicators(
            pagesCount = pages.size,
            currentPage = pagerState.currentPage,
        )

        OnboardingNavigationRow(
            isFirstPage = isFirstPage,
            isLastPage = isLastPage,
            onPreviousClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(
                        page = currentPage - 1,
                        animationSpec = tween(durationMillis = PAGER_ANIMATION_DURATION_MS),
                    )
                }
            },
            onNextClick = {
                if (isLastPage) {
                    onFinished()
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            page = currentPage + 1,
                            animationSpec = tween(durationMillis = PAGER_ANIMATION_DURATION_MS),
                        )
                    }
                }
            },
        )

        Spacer(modifier = Modifier.height(Dimens.spacing28))
    }
}

@Composable
private fun PageIndicators(
    pagesCount: Int,
    currentPage: Int,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = Dimens.spacing24),
    ) {
        repeat(pagesCount) { index ->
            val isActive = currentPage == index
            Box(
                modifier = Modifier
                    .size(Dimens.spacing8)
                    .background(
                        color = AppTheme.colors.primary.copy(
                            alpha = if (isActive) DOT_ACTIVE_ALPHA else DOT_INACTIVE_ALPHA,
                        ),
                        shape = CircleShape,
                    ),
            )
            if (index < pagesCount - 1) {
                Spacer(modifier = Modifier.width(Dimens.spacing8))
            }
        }
    }
}

@Composable
private fun OnboardingNavigationRow(
    isFirstPage: Boolean,
    isLastPage: Boolean,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isFirstPage) {
            Spacer(modifier = Modifier.size(Dimens.buttonLargeHeight))
        } else {
            OnboardingNavButton(
                iconRes = com.moneytrack.designsystem.R.drawable.arrow_left_2,
                contentDescription = stringResource(R.string.onboarding_prev),
                onClick = onPreviousClick,
            )
        }

        OnboardingNavButton(
            iconRes = com.moneytrack.designsystem.R.drawable.arrow_right_2,
            contentDescription = if (isLastPage) {
                stringResource(R.string.onboarding_finish)
            } else {
                stringResource(R.string.onboarding_next)
            },
            onClick = onNextClick,
        )
    }
}

@Composable
private fun OnboardingNavButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) PRESSED_SCALE else DEFAULT_SCALE,
        animationSpec = tween(durationMillis = BUTTON_ANIMATION_DURATION_MS),
        label = "onboardingNavButtonScale",
    )

    Surface(
        shape = CircleShape,
        color = AppTheme.colors.primary,
        modifier = Modifier
            .size(Dimens.buttonLargeHeight)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = contentDescription,
                tint = AppTheme.colors.onPrimary,
            )
        }
    }
}

@Composable
private fun OnboardingPageItem(page: OnboardingPage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.onboardingIllustrationHeight),
            contentScale = ContentScale.Fit,
        )

        Spacer(modifier = Modifier.height(Dimens.spacing24))

        Text(
            text = stringResource(id = page.titleRes),
            style = AppTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            color = AppTheme.colors.onBackground,
        )

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        Text(
            text = stringResource(id = page.descriptionRes),
            style = AppTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = AppTheme.colors.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun OnboardingScreenPreview() {
    MoneyTrackTheme(darkTheme = false) {
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
        )
    }
}
