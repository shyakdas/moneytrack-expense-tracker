// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.domain.usecase

import com.moneytrack.settings.domain.model.AppThemeMode
import com.moneytrack.settings.domain.repository.ThemePreferenceRepository
import javax.inject.Inject

class SaveAppThemeModeUseCase @Inject constructor(
    private val themePreferenceRepository: ThemePreferenceRepository,
) {
    suspend operator fun invoke(appThemeMode: AppThemeMode) {
        themePreferenceRepository.saveAppThemeMode(appThemeMode)
    }
}
