// Copyright (c) 2026 shyakdas

package com.moneytrack.pinauth.presentation

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneytrack.R
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

@Composable
fun PinAuthRoute(
    onAuthenticated: () -> Unit,
    viewModel: PinAuthViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                PinAuthEvent.Authenticated -> onAuthenticated()
                PinAuthEvent.RequestBiometricPrompt -> {
                    val isVerified = authenticateWithBiometric(context)
                    viewModel.onAction(PinAuthAction.BiometricAuthResult(isVerified))
                }
            }
        }
    }

    PinAuthScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}

private suspend fun authenticateWithBiometric(context: Context): Boolean {
    val activity = context.findFragmentActivity()
    val authenticators =
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
    return if (activity != null && canAuthenticate(activity, authenticators)) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(activity.getString(R.string.pin_auth_prompt_title))
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
                        // Keep prompt open. Success/error returns final state.
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
