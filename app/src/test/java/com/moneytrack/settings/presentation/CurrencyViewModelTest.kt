// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import com.moneytrack.locale.AppCurrencyManager
import com.moneytrack.locale.CountryProvider
import com.moneytrack.locale.CurrencyCatalog
import com.moneytrack.settings.domain.repository.CurrencyPreferenceRepository
import com.moneytrack.settings.domain.usecase.ObserveAppCurrencyCodeUseCase
import com.moneytrack.settings.domain.usecase.ObserveSelectedCurrencyCodeUseCase
import com.moneytrack.settings.domain.usecase.SaveSelectedCurrencyCodeUseCase
import com.moneytrack.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    @Test
    fun uiState_placesSelectedCurrencyFirst_andKeepsOthersAlphabetical() = runTest {
        val repository = FakeCurrencyPreferenceRepository()
        val viewModel = createViewModel(
            repository = repository,
            scope = backgroundScope,
        )
        val collectJob = launch { viewModel.uiState.collect { } }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("INR", state.selectedCurrencyCode)
        assertEquals("INR", state.currencies.first().code)
        assertTrue(
            state.currencies
                .drop(1)
                .zipWithNext()
                .all { (current, next) -> current.countryName <= next.countryName },
        )
        collectJob.cancel()
    }

    @Test
    fun onCurrencySelected_savesSelection_andUpdatesUiState() = runTest {
        val repository = FakeCurrencyPreferenceRepository()
        val viewModel = createViewModel(
            repository = repository,
            scope = backgroundScope,
        )
        val collectJob = launch { viewModel.uiState.collect { } }

        viewModel.onCurrencySelected("USD")
        advanceUntilIdle()

        assertEquals("USD", repository.savedCurrencyCode)
        assertEquals("USD", viewModel.uiState.value.selectedCurrencyCode)
        assertEquals("USD", viewModel.uiState.value.currencies.first().code)
        collectJob.cancel()
    }

    @Test
    fun invalidSavedCurrencyCode_fallsBackToDeviceDefault_andRepairsStoredValue() = runTest {
        val repository = FakeCurrencyPreferenceRepository(initialCurrencyCode = "XUA")
        val viewModel = createViewModel(
            repository = repository,
            scope = backgroundScope,
        )
        val collectJob = launch { viewModel.uiState.collect { } }

        advanceUntilIdle()

        assertEquals("INR", viewModel.uiState.value.selectedCurrencyCode)
        assertEquals("INR", repository.savedCurrencyCode)
        collectJob.cancel()
    }

    private fun createViewModel(
        repository: FakeCurrencyPreferenceRepository,
        scope: CoroutineScope,
    ): CurrencyViewModel {
        val currencyCatalog = CurrencyCatalog()
        val appCurrencyManager = AppCurrencyManager(
            observeSelectedCurrencyCodeUseCase = ObserveSelectedCurrencyCodeUseCase(repository),
            saveSelectedCurrencyCodeUseCase = SaveSelectedCurrencyCodeUseCase(repository),
            countryProvider = FakeCountryProvider(),
            currencyCatalog = currencyCatalog,
            scope = scope,
        )
        return CurrencyViewModel(
            observeAppCurrencyCodeUseCase = ObserveAppCurrencyCodeUseCase(appCurrencyManager),
            saveSelectedCurrencyCodeUseCase = SaveSelectedCurrencyCodeUseCase(repository),
            currencyCatalog = currencyCatalog,
        )
    }

    private class FakeCurrencyPreferenceRepository(
        initialCurrencyCode: String? = null,
    ) : CurrencyPreferenceRepository {
        private val selectedCurrencyCode = MutableStateFlow<String?>(null)
        var savedCurrencyCode: String? = null

        init {
            selectedCurrencyCode.value = initialCurrencyCode
        }

        override fun observeSelectedCurrencyCode(): Flow<String?> = selectedCurrencyCode.asStateFlow()

        override suspend fun saveSelectedCurrencyCode(currencyCode: String) {
            savedCurrencyCode = currencyCode
            selectedCurrencyCode.value = currencyCode
        }
    }

    private class FakeCountryProvider : CountryProvider {
        override fun getCountryCode(): String = "IN"

        override fun getCurrencySymbol(): String = "₹"
    }
}
