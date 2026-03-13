// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.domain.usecase

import com.moneytrack.locale.AppCurrencyManager
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

class ObserveAppCurrencyCodeUseCase @Inject constructor(
    private val appCurrencyManager: AppCurrencyManager,
) {
    operator fun invoke(): StateFlow<String> = appCurrencyManager.currencyCode
}
