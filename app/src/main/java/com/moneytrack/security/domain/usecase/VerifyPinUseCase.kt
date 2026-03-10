// Copyright (c) 2026 shyakdas

package com.moneytrack.security.domain.usecase

import com.moneytrack.security.domain.repository.SecurityRepository
import javax.inject.Inject

class VerifyPinUseCase @Inject constructor(
    private val repository: SecurityRepository,
) {
    suspend operator fun invoke(pin: String): Boolean {
        val savedHash = repository.getPinHash() ?: return false
        return savedHash == pin.sha256()
    }
}
