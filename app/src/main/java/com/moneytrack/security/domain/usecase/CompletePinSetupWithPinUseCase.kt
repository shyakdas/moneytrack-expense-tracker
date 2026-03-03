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

private fun String.sha256(): String {
    val bytes = java.security.MessageDigest
        .getInstance("SHA-256")
        .digest(toByteArray())
    return bytes.joinToString(separator = "") { byte ->
        "%02x".format(byte)
    }
}
