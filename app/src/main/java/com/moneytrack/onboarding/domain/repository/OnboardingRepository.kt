// Copyright (c) 2026 shyakdas

package com.moneytrack.onboarding.domain.repository

import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {
    fun observeCompletion(): Flow<Boolean>
    suspend fun setCompleted(completed: Boolean)
}
