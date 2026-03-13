// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.data.repository

import com.moneytrack.profile.data.local.ProfilePreferencesDataSource
import com.moneytrack.profile.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val dataSource: ProfilePreferencesDataSource,
) : ProfileRepository {
    override fun observeDisplayName(): Flow<String> = dataSource.displayNameFlow

    override suspend fun saveDisplayName(displayName: String) {
        dataSource.saveDisplayName(displayName = displayName)
    }
}
