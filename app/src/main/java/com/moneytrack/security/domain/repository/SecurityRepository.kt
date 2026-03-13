// Copyright (c) 2026 shyakdas

package com.moneytrack.security.domain.repository

import kotlinx.coroutines.flow.Flow
import com.moneytrack.security.domain.model.PinSetupStatus

interface SecurityRepository {
    fun observePinSetupStatus(): Flow<PinSetupStatus>
    suspend fun setPinSetupStatus(status: PinSetupStatus)
    suspend fun savePinHash(pinHash: String)
    suspend fun clearPinHash()
    suspend fun getPinHash(): String?
}
