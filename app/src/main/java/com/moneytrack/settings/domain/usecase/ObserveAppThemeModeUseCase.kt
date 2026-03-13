// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.domain.usecase

import com.moneytrack.settings.domain.model.AppThemeMode
import com.moneytrack.settings.domain.repository.ThemePreferenceRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveAppThemeModeUseCase @Inject constructor(
    private val themePreferenceRepository: ThemePreferenceRepository,
) {
    operator fun invoke(): Flow<AppThemeMode> = themePreferenceRepository.observeAppThemeMode()
}
