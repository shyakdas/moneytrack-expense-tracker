// Copyright (c) 2026 shyakdas

package com.moneytrack.security.domain.usecase

import com.moneytrack.security.domain.model.PinSetupStatus
import com.moneytrack.security.domain.repository.SecurityRepository
import javax.inject.Inject

class CompletePinSetupWithPinUseCase @Inject constructor(
    private val repository: SecurityRepository,
) {
    suspend operator fun invoke(pin: String) {
        repository.savePinHash(pin.sha256())
        repository.setPinSetupStatus(PinSetupStatus.PIN_ENABLED)
    }
}
