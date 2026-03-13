// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.domain.repository

import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeDisplayName(): Flow<String>
    suspend fun saveDisplayName(displayName: String)
}
