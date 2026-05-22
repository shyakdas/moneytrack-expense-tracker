// Copyright (c) 2026 shyakdas

@file:Suppress("MagicNumber", "LongMethod", "TooManyFunctions", "UnusedPrivateMember")

package com.moneytrack.pinauth.presentation

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.moneytrack.R
import ui.components.navigation.button.LargeButton
import ui.components.surface.MoneyTrackCard
import ui.components.surface.MoneyTrackScreenBackground
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

private const val PIN_LENGTH = 4

@Composable
fun PinAuthScreen(
    uiState: PinAuthUiState,
    onAction: (PinAuthAction) -> Unit,
) {
    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.background),
        )
        return
    }

    when (uiState.mode) {
        PinAuthMode.PIN -> PinAuthPinScreen(uiState = uiState, onAction = onAction)
        PinAuthMode.BIOMETRIC -> PinAuthBiometricScreen(uiState = uiState, onAction = onAction)
    }
}

@Composable
private fun PinAuthPinScreen(
    uiState: PinAuthUiState,
    onAction: (PinAuthAction) -> Unit,
) {
    MoneyTrackScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.spacing24),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(Dimens.spacing72))
            Text(
                text = stringResource(R.string.pin_auth_title),
                style = AppTheme.typography.headlineMedium,
                color = AppTheme.colors.onBackground,
                textAlign = TextAlign.Center,
            )

            if (uiState.showPinError) {
                Spacer(modifier = Modifier.height(Dimens.spacing8))
                Text(
                    text = stringResource(R.string.pin_auth_error),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.error,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(Dimens.spacing32))
            PinDots(enteredCount = uiState.enteredPin.length)
            Spacer(modifier = Modifier.weight(1f))

            MoneyTrackCard {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    PinKeypad(onAction = onAction)
                }
            }
            Spacer(modifier = Modifier.height(Dimens.spacing24))
        }
    }
}

@Composable
private fun PinAuthBiometricScreen(
    uiState: PinAuthUiState,
    onAction: (PinAuthAction) -> Unit,
) {
    MoneyTrackScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.spacing24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.spacing72)
                    .background(AppTheme.colors.primaryContainer, shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(id = com.moneytrack.designsystem.R.drawable.settings),
                    contentDescription = null,
                    tint = AppTheme.colors.primary,
                )
            }
            Spacer(modifier = Modifier.height(Dimens.spacing24))
            Text(
                text = stringResource(R.string.pin_auth_biometric_title),
                style = AppTheme.typography.headlineMedium,
                color = AppTheme.colors.onBackground,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(Dimens.spacing12))
            Text(
                text = stringResource(R.string.pin_auth_biometric_subtitle),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            if (uiState.showBiometricError) {
                Spacer(modifier = Modifier.height(Dimens.spacing16))
                Text(
                    text = stringResource(R.string.pin_auth_biometric_error),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.error,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.height(Dimens.spacing24))
            LargeButton(
                text = stringResource(R.string.pin_auth_biometric_retry),
                onClick = { onAction(PinAuthAction.RequestBiometricAuth) },
            )
        }
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
                            AppTheme.colors.surfaceVariant
                        },
                        shape = CircleShape,
                    ),
            )
        }
    }
}

@Composable
private fun PinKeypad(onAction: (PinAuthAction) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing16),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PinDigitRow(digits = listOf(1, 2, 3), onAction = onAction)
        PinDigitRow(digits = listOf(4, 5, 6), onAction = onAction)
        PinDigitRow(digits = listOf(7, 8, 9), onAction = onAction)
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacing32),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KeypadActionText(
                label = stringResource(R.string.pin_delete),
                onClick = { onAction(PinAuthAction.DeleteDigit) },
            )
            PinDigit(
                value = 0,
                onClick = { onAction(PinAuthAction.EnterDigit(0)) },
            )
            KeypadIconButton(
                iconRes = com.moneytrack.designsystem.R.drawable.arrow_right_2,
                contentDescription = stringResource(R.string.pin_submit),
                onClick = { onAction(PinAuthAction.SubmitPin) },
            )
        }
    }
}

@Composable
private fun PinDigitRow(
    digits: List<Int>,
    onAction: (PinAuthAction) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacing32),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        digits.forEach { digit ->
            PinDigit(
                value = digit,
                onClick = { onAction(PinAuthAction.EnterDigit(digit)) },
            )
        }
    }
}

@Composable
private fun PinDigit(
    value: Int,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(Dimens.spacing72)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = value.toString(),
            style = AppTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Medium),
            color = AppTheme.colors.onBackground,
        )
    }
}

@Composable
private fun KeypadIconButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(Dimens.spacing72)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = AppTheme.colors.primary,
        )
    }
}

@Composable
private fun KeypadActionText(
    label: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(Dimens.spacing72)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = AppTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = AppTheme.colors.primary,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PinAuthPinPreview() {
    MoneyTrackTheme(darkTheme = false) {
        PinAuthScreen(
            uiState = PinAuthUiState(
                isLoading = false,
                mode = PinAuthMode.PIN,
                enteredPin = "12",
            ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PinAuthBiometricPreview() {
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
