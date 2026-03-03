package com.moneytrack.security.data.repository

import kotlinx.coroutines.flow.Flow
import com.moneytrack.security.data.local.SecurityPreferencesDataSource
import com.moneytrack.security.domain.model.PinSetupStatus
import com.moneytrack.security.domain.repository.SecurityRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityRepositoryImpl @Inject constructor(
    private val dataSource: SecurityPreferencesDataSource,
) : SecurityRepository {
    override fun observePinSetupStatus(): Flow<PinSetupStatus> = dataSource.pinSetupStatusFlow

    override suspend fun setPinSetupStatus(status: PinSetupStatus) {
        dataSource.setPinSetupStatus(status)
    }

    override suspend fun savePinHash(pinHash: String) {
        dataSource.savePinHash(pinHash)
    }

    override suspend fun getPinHash(): String? = dataSource.getPinHash()
}
