package com.moneytrack.pinsetup.presentation

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moneytrack.R
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

@Composable
fun PinSetupRoute(
    onCompleted: () -> Unit,
    viewModel: PinSetupViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                PinSetupEvent.Completed -> onCompleted()
                PinSetupEvent.RequestRecoveryAuth -> {
                    val isVerified = runRecoveryVerification(context)
                    viewModel.onAction(PinSetupAction.RecoveryVerificationResult(isVerified))
                }
            }
        }
    }

    PinSetupScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}

private suspend fun runRecoveryVerification(context: Context): Boolean {
    val activity = context.findFragmentActivity()
    val authenticators =
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
    return if (activity != null && canAuthenticate(activity, authenticators)) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(activity.getString(R.string.pin_recovery_auth_title))
            .setSubtitle(activity.getString(R.string.pin_recovery_auth_subtitle))
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
                        // Keep prompt open; final outcome is sent by success or error callback.
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
