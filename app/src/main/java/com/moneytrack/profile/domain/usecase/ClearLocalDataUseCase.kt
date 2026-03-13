// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.domain.usecase

import com.moneytrack.profile.data.local.LocalAppDataResetter
import javax.inject.Inject

class ClearLocalDataUseCase @Inject constructor(
    private val localAppDataResetter: LocalAppDataResetter,
) {
    suspend operator fun invoke() {
        localAppDataResetter.clearAllLocalData()
    }
}
