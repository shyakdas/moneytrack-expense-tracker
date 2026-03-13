// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.profile.domain.usecase.ObserveProfileDisplayNameUseCase
import com.moneytrack.profile.domain.usecase.SaveProfileDisplayNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    observeProfileDisplayNameUseCase: ObserveProfileDisplayNameUseCase,
    private val saveProfileDisplayNameUseCase: SaveProfileDisplayNameUseCase,
) : ViewModel() {

    private val _draftName = MutableStateFlow("")
    private val _isEditSheetVisible = MutableStateFlow(false)

    val uiState: StateFlow<ProfileUiState> = combine(
        observeProfileDisplayNameUseCase(),
        _draftName,
        _isEditSheetVisible,
    ) { displayName, draftName, isEditSheetVisible ->
        val resolvedName = displayName.ifBlank { DEFAULT_PROFILE_NAME }
        val resolvedDraft = if (isEditSheetVisible) draftName else resolvedName
        ProfileUiState(
            name = resolvedName,
            editName = resolvedDraft,
            isEditSheetVisible = isEditSheetVisible,
            isSaveEnabled = resolvedDraft.trim().isNotEmpty(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(WHILE_SUBSCRIBED_TIMEOUT_MS),
        initialValue = ProfileUiState(
            name = DEFAULT_PROFILE_NAME,
            editName = "",
            isEditSheetVisible = false,
            isSaveEnabled = false,
        ),
    )

    fun showEditSheet() {
        _draftName.update { uiState.value.name }
        _isEditSheetVisible.update { true }
    }

    fun hideEditSheet() {
        _isEditSheetVisible.update { false }
        _draftName.update { "" }
    }

    fun onNameChanged(name: String) {
        _draftName.update { name.take(MAX_NAME_LENGTH) }
    }

    fun saveName() {
        val trimmedName = _draftName.value.trim()
        if (trimmedName.isEmpty()) return

        viewModelScope.launch {
            saveProfileDisplayNameUseCase(displayName = trimmedName)
            _isEditSheetVisible.update { false }
            _draftName.update { "" }
        }
    }

    private companion object {
        private const val DEFAULT_PROFILE_NAME = "Saver"
        private const val MAX_NAME_LENGTH = 24
        private const val WHILE_SUBSCRIBED_TIMEOUT_MS = 5_000L
    }
}

data class ProfileUiState(
    val name: String,
    val editName: String,
    val isEditSheetVisible: Boolean,
    val isSaveEnabled: Boolean,
)
