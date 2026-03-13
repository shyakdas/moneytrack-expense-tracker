// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.domain.usecase

import com.moneytrack.settings.domain.repository.CurrencyPreferenceRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveSelectedCurrencyCodeUseCase @Inject constructor(
    private val currencyPreferenceRepository: CurrencyPreferenceRepository,
) {
    operator fun invoke(): Flow<String?> = currencyPreferenceRepository.observeSelectedCurrencyCode()
}
