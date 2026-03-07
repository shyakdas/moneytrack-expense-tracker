// Copyright (c) 2026 shyakdas

package com.moneytrack.security.domain.usecase

import kotlinx.coroutines.flow.Flow
import com.moneytrack.security.domain.model.PinSetupStatus
import com.moneytrack.security.domain.repository.SecurityRepository
import javax.inject.Inject

class GetPinSetupStatusUseCase @Inject constructor(
    private val repository: SecurityRepository,
) {
    operator fun invoke(): Flow<PinSetupStatus> = repository.observePinSetupStatus()
}
