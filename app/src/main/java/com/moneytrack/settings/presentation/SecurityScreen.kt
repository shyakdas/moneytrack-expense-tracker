// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import ui.components.surface.MoneyTrackScreenBackground
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

private data class SecurityOptionUiModel(
    val option: SecurityOption,
    val title: String,
    val subtitle: String,
    val iconRes: Int,
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
        SecurityOptionUiModel(
            option = SecurityOption.PIN,
            title = stringResource(id = R.string.settings_security_pin),
            subtitle = "Unlock using a 4-digit passcode",
            iconRes = DsR.drawable.document,
        ),
        SecurityOptionUiModel(
            option = SecurityOption.BIOMETRIC,
            title = stringResource(id = R.string.settings_security_biometric),
            subtitle = "Use fingerprint or face unlock",
            iconRes = DsR.drawable.show,
        ),
        SecurityOptionUiModel(
            option = SecurityOption.NONE,
            title = stringResource(id = R.string.settings_security_none),
            subtitle = "No extra lock for this app",
            iconRes = DsR.drawable.warning,
        ),
    )

    Scaffold(
        containerColor = AppTheme.colors.background,
    ) { innerPadding ->
        MoneyTrackScreenBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Dimens.spacing16)
                    .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.padding(top = Dimens.spacing8))
                SecurityHeader(onBackClick = onBackClick)
                Spacer(modifier = Modifier.padding(top = 22.dp))
                options.forEach { securityOption ->
                    SecurityOptionCard(
                        securityOption = securityOption,
                        isSelected = securityOption.option == uiState.selectedOption,
                        onClick = { onOptionSelected(securityOption.option) },
                    )
                    Spacer(modifier = Modifier.padding(top = 14.dp))
                }
                SecurityInfoCard()
                Spacer(modifier = Modifier.padding(top = Dimens.spacing20))
            }
        }
    }
}

@Composable
private fun SecurityHeader(
    onBackClick: () -> Unit,
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier
                .size(44.dp)
                .clickable(onClick = onBackClick),
            shape = CircleShape,
            color = AppTheme.colors.surfaceVariant.copy(alpha = 0.55f),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = DsR.drawable.arrow_left),
                    contentDescription = null,
                    tint = AppTheme.colors.onSurface,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
        Spacer(modifier = Modifier.width(Dimens.spacing12))
        Text(
            text = stringResource(id = R.string.security_title),
            style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = AppTheme.colors.onBackground,
        )
    }
}

@Composable
private fun SecurityOptionCard(
    securityOption: SecurityOptionUiModel,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isSelected) {
                    AppTheme.colors.primary.copy(alpha = 0.5f)
                } else {
                    AppTheme.colors.outline.copy(alpha = 0.28f)
                },
                shape = RoundedCornerShape(Dimens.radius24),
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Dimens.radius24),
        color = AppTheme.colors.surface.copy(alpha = 0.88f),
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = AppTheme.colors.primary.copy(alpha = 0.14f),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = securityOption.iconRes),
                    contentDescription = null,
                    tint = AppTheme.colors.primary,
                    modifier = Modifier.size(14.dp),
                )
            }
            Spacer(modifier = Modifier.width(Dimens.spacing12))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = securityOption.title,
                    style = AppTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = AppTheme.colors.onBackground,
                )
                Text(
                    text = securityOption.subtitle,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant,
                )
            }
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(
                            color = AppTheme.colors.primary.copy(alpha = 0.24f),
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = DsR.drawable.success),
                        contentDescription = null,
                        tint = AppTheme.colors.primary,
                        modifier = Modifier.size(14.dp),
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .border(
                            width = 1.dp,
                            color = AppTheme.colors.outline.copy(alpha = 0.6f),
                            shape = CircleShape,
                        ),
                )
            }
        }
    }
}

@Composable
private fun SecurityInfoCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = AppTheme.colors.outline.copy(alpha = 0.22f),
                shape = RoundedCornerShape(Dimens.radius24),
            ),
        shape = RoundedCornerShape(Dimens.radius24),
        color = AppTheme.colors.surface.copy(alpha = 0.88f),
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = AppTheme.colors.primary.copy(alpha = 0.14f),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = DsR.drawable.warning),
                    contentDescription = null,
                    tint = AppTheme.colors.primary,
                    modifier = Modifier.size(14.dp),
                )
            }
            Spacer(modifier = Modifier.width(Dimens.spacing12))
            Text(
                text = "You can change security anytime from settings.",
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant,
            )
        }
    }
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
