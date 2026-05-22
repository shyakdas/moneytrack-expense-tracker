// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneytrack.R
import com.moneytrack.designsystem.R as DsR
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import ui.components.navigation.topNav.TopNavigation
import ui.components.navigation.topNav.TopNavigationConfig
import ui.components.surface.MoneyTrackScreenBackground
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

private data class SecurityOptionUiModel(
    val option: SecurityOption,
    val title: String,
)

@Composable
fun SecurityRoute(
    onBackClick: () -> Unit,
    onPinOptionClick: () -> Unit,
) {
    val viewModel: SecurityViewModel = hiltViewModel()
    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                SecurityEvent.RequestBiometricPrompt -> {
                    val success = enableBiometricLock(context)
                    viewModel.onBiometricSelectionResult(success)
                }
                SecurityEvent.OpenPinSetup -> onPinOptionClick()
                SecurityEvent.Completed -> onBackClick()
            }
        }
    }

    SecurityScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onOptionSelected = viewModel::onOptionSelected,
    )
}

@Composable
fun SecurityScreen(
    uiState: SecurityUiState,
    onBackClick: () -> Unit,
    onOptionSelected: (SecurityOption) -> Unit,
) {
    val options = listOf(
        SecurityOptionUiModel(SecurityOption.PIN, stringResource(id = R.string.settings_security_pin)),
        SecurityOptionUiModel(SecurityOption.BIOMETRIC, stringResource(id = R.string.settings_security_biometric)),
        SecurityOptionUiModel(SecurityOption.NONE, stringResource(id = R.string.settings_security_none)),
    )

    Scaffold(
        containerColor = AppTheme.colors.background,
    ) { innerPadding ->
        MoneyTrackScreenBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                TopNavigation(
                    config = TopNavigationConfig.BackWithTitle(
                        title = stringResource(id = R.string.security_title),
                        onBackClick = onBackClick,
                    ),
                    containerColor = Color.Transparent,
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Dimens.spacing16)
                        .padding(top = Dimens.spacing12),
                ) {
                    items(options, key = { it.option.name }) { securityOption ->
                        SecurityRow(
                            securityOption = securityOption,
                            isSelected = securityOption.option == uiState.selectedOption,
                            onClick = { onOptionSelected(securityOption.option) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SecurityRow(
    securityOption: SecurityOptionUiModel,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.spacing20, vertical = Dimens.spacing20),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = securityOption.title,
            style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = AppTheme.colors.onBackground,
        )

        if (isSelected) {
            Icon(
                imageVector = ImageVector.vectorResource(id = DsR.drawable.success),
                contentDescription = null,
                tint = AppTheme.colors.primary,
            )
        }
    }
    HorizontalDivider(
        color = AppTheme.colors.outline.copy(alpha = 0.28f),
        thickness = 1.dp,
    )
}

private suspend fun enableBiometricLock(context: Context): Boolean {
    val activity = context.findFragmentActivity()
    val authenticators =
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
    return if (activity != null && canAuthenticate(activity, authenticators)) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(activity.getString(R.string.settings_security_biometric))
            .setSubtitle(activity.getString(R.string.pin_auth_prompt_subtitle))
            .setAllowedAuthenticators(authenticators)
            .build()
        suspendCancellableCoroutine { continuation ->
            val biometricPrompt = BiometricPrompt(
                activity,
                ContextCompat.getMainExecutor(activity),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        if (continuation.isActive) continuation.resume(true)
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        if (continuation.isActive) continuation.resume(false)
                    }

                    override fun onAuthenticationFailed() {
                        // Keep prompt open until success/error.
                    }
                },
            )
            biometricPrompt.authenticate(promptInfo)
        }
    } else {
        false
    }
}

private tailrec fun Context.findFragmentActivity(): FragmentActivity? =
    when (this) {
        is FragmentActivity -> this
        is ContextWrapper -> baseContext.findFragmentActivity()
        else -> null
    }

private fun canAuthenticate(
    activity: FragmentActivity,
    authenticators: Int,
): Boolean = BiometricManager.from(activity).canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS

@Suppress("UnusedPrivateMember")
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun SecurityScreenLightPreview() {
    MoneyTrackTheme(darkTheme = false) {
        SecurityScreen(
            uiState = SecurityUiState(selectedOption = SecurityOption.BIOMETRIC),
            onBackClick = {},
            onOptionSelected = {},
        )
    }
}

@Suppress("UnusedPrivateMember")
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun SecurityScreenDarkPreview() {
    MoneyTrackTheme(darkTheme = true) {
        SecurityScreen(
            uiState = SecurityUiState(selectedOption = SecurityOption.NONE),
            onBackClick = {},
            onOptionSelected = {},
        )
    }
}
