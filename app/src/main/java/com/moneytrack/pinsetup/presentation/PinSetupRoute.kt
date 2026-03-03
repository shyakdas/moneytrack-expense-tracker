package com.moneytrack.pinsetup.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun PinSetupRoute(
    onCompleted: () -> Unit,
    viewModel: PinSetupViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                PinSetupEvent.Completed -> onCompleted()
            }
        }
    }

    PinSetupScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}
