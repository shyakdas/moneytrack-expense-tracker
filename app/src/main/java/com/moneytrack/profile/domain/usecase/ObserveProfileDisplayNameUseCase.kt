// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.domain.usecase

import com.moneytrack.profile.domain.repository.ProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveProfileDisplayNameUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    operator fun invoke(): Flow<String> = profileRepository.observeDisplayName()
}
