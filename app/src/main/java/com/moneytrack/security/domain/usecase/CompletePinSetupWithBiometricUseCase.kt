package com.moneytrack.security.domain.usecase

import com.moneytrack.security.domain.model.PinSetupStatus
import com.moneytrack.security.domain.repository.SecurityRepository
import javax.inject.Inject

class CompletePinSetupWithBiometricUseCase @Inject constructor(
    private val repository: SecurityRepository,
) {
    suspend operator fun invoke() {
        repository.setPinSetupStatus(PinSetupStatus.BIOMETRIC_ENABLED)
    }
}
