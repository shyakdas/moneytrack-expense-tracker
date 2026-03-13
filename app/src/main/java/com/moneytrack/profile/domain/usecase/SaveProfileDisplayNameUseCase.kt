// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.domain.usecase

import com.moneytrack.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class SaveProfileDisplayNameUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(displayName: String) {
        profileRepository.saveDisplayName(displayName = displayName)
    }
}
