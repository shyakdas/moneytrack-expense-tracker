// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.domain.usecase

import com.moneytrack.settings.domain.repository.CurrencyPreferenceRepository
import javax.inject.Inject

class SaveSelectedCurrencyCodeUseCase @Inject constructor(
    private val currencyPreferenceRepository: CurrencyPreferenceRepository,
) {
    suspend operator fun invoke(currencyCode: String) {
        currencyPreferenceRepository.saveSelectedCurrencyCode(currencyCode = currencyCode)
    }
}
