@file:Suppress("MagicNumber", "LongMethod", "TooManyFunctions", "UnusedPrivateMember")

package com.moneytrack.pinsetup.presentation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieConstants
import com.moneytrack.R
import com.moneytrack.common.ui.LottieAnimationView
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

private const val PIN_LENGTH = 4
private const val MAX_CONFIRM_ATTEMPTS = 3
private const val ANIMATION_DURATION_MS = 1200
private const val IDLE_SCALE = 1f
private const val MAX_SCALE = 1.08f

@Composable
fun PinSetupScreen(
    uiState: PinSetupUiState,
    onAction: (PinSetupAction) -> Unit,
) {
    when (uiState.stage) {
        PinSetupStage.INTRO -> PinSetupIntroScreen(onAction = onAction)
        PinSetupStage.CREATE_PIN,
        PinSetupStage.CONFIRM_PIN,
            -> PinEntryScreen(
                uiState = uiState,
                onAction = onAction,
            )
        PinSetupStage.SUCCESS -> PinSetupSuccessScreen()
    }
}

@Composable
private fun PinSetupSuccessScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .padding(horizontal = Dimens.spacing24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        LottieAnimationView(
            rawRes = R.raw.lottie_success_check,
            modifier = Modifier.size(Dimens.lottieHeroSize),
            iterations = 1,
        )
        Spacer(modifier = Modifier.height(Dimens.spacing24))
        Text(
            text = stringResource(R.string.pin_setup_success_title),
            style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = AppTheme.colors.onBackground,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PinSetupIntroScreen(
    onAction: (PinSetupAction) -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "pin_intro_transition")
    val animatedScale by transition.animateFloat(
        initialValue = IDLE_SCALE,
        targetValue = MAX_SCALE,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = ANIMATION_DURATION_MS),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pin_intro_scale",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .padding(horizontal = Dimens.spacing24),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        LottieAnimationView(
            rawRes = R.raw.lottie_secure_shield,
            modifier = Modifier
                .size(Dimens.lottieHeroSize)
                .scale(animatedScale),
            iterations = LottieConstants.IterateForever,
        )

        Spacer(modifier = Modifier.height(Dimens.spacing24))

        Text(
            text = stringResource(R.string.pin_intro_title),
            style = AppTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = AppTheme.colors.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(Dimens.spacing12))

        Text(
            text = stringResource(R.string.pin_intro_subtitle),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(Dimens.spacing36))

        PinPrimaryActionButton(
            text = stringResource(R.string.pin_intro_set_pin),
            onClick = { onAction(PinSetupAction.SelectPin) },
        )

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        PinSecondaryActionButton(
            text = stringResource(R.string.pin_intro_use_biometric),
            onClick = { onAction(PinSetupAction.SelectBiometric) },
        )

        Spacer(modifier = Modifier.height(Dimens.spacing24))

        Text(
            text = stringResource(R.string.pin_intro_skip),
            style = AppTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = AppTheme.colors.primary,
            modifier = Modifier.clickable { onAction(PinSetupAction.Skip) },
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun PinEntryScreen(
    uiState: PinSetupUiState,
    onAction: (PinSetupAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .padding(horizontal = Dimens.spacing24),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(Dimens.spacing72))

        Text(
            text = if (uiState.stage == PinSetupStage.CREATE_PIN) {
                stringResource(R.string.pin_create_title)
            } else {
                stringResource(R.string.pin_confirm_title)
            },
            style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = AppTheme.colors.onBackground,
            textAlign = TextAlign.Center,
        )

        if (uiState.showPinMismatch) {
            Spacer(modifier = Modifier.height(Dimens.spacing8))
            Text(
                text = if (uiState.isLockedOut) {
                    stringResource(R.string.pin_locked_out_error)
                } else {
                    stringResource(
                        R.string.pin_mismatch_error_with_attempts,
                        MAX_CONFIRM_ATTEMPTS - uiState.failedAttempts,
                    )
                },
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.error,
            )
        }

        if (uiState.isLockedOut) {
            Spacer(modifier = Modifier.height(Dimens.spacing8))
            Text(
                text = stringResource(R.string.pin_recovery_helper),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(Dimens.spacing16))
            PinSecondaryActionButton(
                text = stringResource(R.string.pin_forgot_pin),
                onClick = { onAction(PinSetupAction.ForgotPin) },
            )
        }

        if (uiState.showRecoveryError) {
            Spacer(modifier = Modifier.height(Dimens.spacing8))
            Text(
                text = stringResource(R.string.pin_recovery_error),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.error,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(Dimens.spacing32))
        PinDots(enteredCount = uiState.enteredPin.length)
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = AppTheme.colors.surface,
                    shape = RoundedCornerShape(Dimens.radius24),
                )
                .padding(vertical = Dimens.spacing24),
            contentAlignment = Alignment.Center,
        ) {
            PinKeypad(
                onAction = onAction,
                enabled = !uiState.isLockedOut,
            )
        }
        Spacer(modifier = Modifier.height(Dimens.spacing24))
    }
}

@Composable
private fun PinDots(enteredCount: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spacing12)) {
        repeat(PIN_LENGTH) { index ->
            val filled = index < enteredCount
            Box(
                modifier = Modifier
                    .size(Dimens.spacing32)
                    .background(
                        color = if (filled) {
                            AppTheme.colors.primary
                        } else {
                            AppTheme.colors.onSurfaceVariant.copy(alpha = 0.3f)
                        },
                        shape = CircleShape,
                    ),
            )
        }
    }
}

@Composable
private fun PinKeypad(
    onAction: (PinSetupAction) -> Unit,
    enabled: Boolean,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing16),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PinDigitRow(digits = listOf(1, 2, 3), onAction = onAction, enabled = enabled)
        PinDigitRow(digits = listOf(4, 5, 6), onAction = onAction, enabled = enabled)
        PinDigitRow(digits = listOf(7, 8, 9), onAction = onAction, enabled = enabled)
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacing32),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KeypadActionText(
                label = stringResource(R.string.pin_delete),
                onClick = { onAction(PinSetupAction.DeleteDigit) },
                enabled = enabled,
            )
            PinDigit(
                value = 0,
                onClick = { onAction(PinSetupAction.EnterDigit(0)) },
                enabled = enabled,
            )
            KeypadIconButton(
                iconRes = com.moneytrack.designsystem.R.drawable.arrow_right_2,
                contentDescription = stringResource(R.string.pin_submit),
                onClick = { onAction(PinSetupAction.SubmitPin) },
                enabled = enabled,
            )
        }
    }
}

@Composable
private fun PinDigitRow(
    digits: List<Int>,
    onAction: (PinSetupAction) -> Unit,
    enabled: Boolean,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacing32),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        digits.forEach { digit ->
            PinDigit(
                value = digit,
                onClick = { onAction(PinSetupAction.EnterDigit(digit)) },
                enabled = enabled,
            )
        }
    }
}

@Composable
private fun PinDigit(
    value: Int,
    onClick: () -> Unit,
    enabled: Boolean,
) {
    Box(
        modifier = Modifier
            .size(Dimens.spacing72)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = value.toString(),
            style = AppTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Medium),
            color = if (enabled) AppTheme.colors.onBackground else AppTheme.colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun KeypadIconButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    enabled: Boolean,
) {
    Box(
        modifier = Modifier
            .size(Dimens.spacing72)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = if (enabled) AppTheme.colors.primary else AppTheme.colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun KeypadActionText(
    label: String,
    onClick: () -> Unit,
    enabled: Boolean,
) {
    Box(
        modifier = Modifier
            .size(Dimens.spacing72)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = AppTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = if (enabled) AppTheme.colors.primary else AppTheme.colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun PinPrimaryActionButton(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.buttonLargeHeight)
            .background(color = AppTheme.colors.primary, shape = CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = AppTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = AppTheme.colors.onPrimary,
        )
    }
}

@Composable
private fun PinSecondaryActionButton(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.buttonLargeHeight)
            .background(
                color = AppTheme.colors.primary.copy(alpha = 0.12f),
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = AppTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = AppTheme.colors.primary,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PinSetupIntroPreview() {
    MoneyTrackTheme(darkTheme = false) {
        PinSetupScreen(
            uiState = PinSetupUiState(
                stage = PinSetupStage.INTRO,
                enteredPin = "",
                firstPin = "",
                showPinMismatch = false,
            ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PinSetupCreatePinPreview() {
    MoneyTrackTheme(darkTheme = false) {
        PinSetupScreen(
            uiState = PinSetupUiState(
                stage = PinSetupStage.CREATE_PIN,
                enteredPin = "12",
                firstPin = "",
                showPinMismatch = false,
            ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PinSetupConfirmPinPreview() {
    MoneyTrackTheme(darkTheme = false) {
        PinSetupScreen(
            uiState = PinSetupUiState(
                stage = PinSetupStage.CONFIRM_PIN,
                enteredPin = "123",
                firstPin = "1234",
                showPinMismatch = false,
            ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PinSetupSuccessPreview() {
    MoneyTrackTheme(darkTheme = false) {
        PinSetupScreen(
            uiState = PinSetupUiState(
                stage = PinSetupStage.SUCCESS,
                enteredPin = "",
                firstPin = "",
                showPinMismatch = false,
            ),
            onAction = {},
        )
    }
}
