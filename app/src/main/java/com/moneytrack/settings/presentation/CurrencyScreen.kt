// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneytrack.R
import com.moneytrack.designsystem.R as DsR
import com.moneytrack.settings.domain.model.CurrencyOption
import ui.components.navigation.topNav.TopNavigation
import ui.components.navigation.topNav.TopNavigationConfig
import ui.theme.AppTheme
import ui.theme.Dimens

@Composable
fun CurrencyRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: CurrencyViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    CurrencyScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onCurrencySelected = viewModel::onCurrencySelected,
    )
}

@Composable
fun CurrencyScreen(
    uiState: CurrencyUiState,
    onBackClick: () -> Unit,
    onCurrencySelected: (String) -> Unit,
) {
    Scaffold(
        containerColor = AppTheme.colors.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.background)
                .padding(innerPadding),
        ) {
            TopNavigation(
                config = TopNavigationConfig.BackWithTitle(
                    title = stringResource(id = R.string.currency_title),
                    onBackClick = onBackClick,
                ),
                containerColor = Color.Transparent,
            )

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.spacing16)
                    .padding(top = Dimens.spacing12),
                shape = RoundedCornerShape(Dimens.radius24),
                color = AppTheme.colors.surface,
            ) {
                LazyColumn {
                    items(
                        items = uiState.currencies,
                        key = CurrencyOption::code,
                    ) { currency ->
                        CurrencyRow(
                            currency = currency,
                            isSelected = currency.code == uiState.selectedCurrencyCode,
                            onClick = { onCurrencySelected(currency.code) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrencyRow(
    currency: CurrencyOption,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.spacing20, vertical = Dimens.spacing20),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacing12),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = currency.countryName,
                style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = AppTheme.colors.onBackground,
            )
            Text(
                text = "${currency.code}  ${currency.symbol}",
                style = AppTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = AppTheme.colors.onSurfaceVariant,
            )
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .background(
                        color = AppTheme.colors.primary,
                        shape = CircleShape,
                    )
                    .padding(Dimens.spacing6),
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(DsR.drawable.success),
                    contentDescription = null,
                    tint = AppTheme.colors.onPrimary,
                )
            }
        }
    }
    HorizontalDivider(
        color = AppTheme.colors.outline.copy(alpha = 0.16f),
        thickness = 1.dp,
    )
}
