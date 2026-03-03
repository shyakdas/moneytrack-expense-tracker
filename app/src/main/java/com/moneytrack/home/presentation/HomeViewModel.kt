package com.moneytrack.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.home.domain.model.Budget
import com.moneytrack.home.domain.usecase.ObserveBudgetUseCase
import com.moneytrack.home.domain.usecase.UpsertBudgetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeBudgetUseCase: ObserveBudgetUseCase,
    private val upsertBudgetUseCase: UpsertBudgetUseCase,
) : ViewModel() {

    private val _budget = MutableStateFlow<Budget?>(null)
    val budget: StateFlow<Budget?> = _budget.asStateFlow()

    init {
        viewModelScope.launch {
            observeBudgetUseCase().collect { budget ->
                _budget.update { budget }
            }
        }
    }

    fun saveBudget(
        amount: Double,
        description: String?,
    ) {
        viewModelScope.launch {
            upsertBudgetUseCase(
                amount = amount,
                description = description?.takeIf { it.isNotBlank() },
            )
        }
    }
}
