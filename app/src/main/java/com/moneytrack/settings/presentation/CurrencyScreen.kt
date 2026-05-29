// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneytrack.R
import com.moneytrack.designsystem.R as DsR
import com.moneytrack.settings.domain.model.CurrencyOption
import ui.components.surface.MoneyTrackScreenBackground
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
fun CurrencyRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: CurrencyViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    CurrencyScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onCurrencySelected = { currencyCode ->
            viewModel.onCurrencySelected(currencyCode)
            onBackClick()
        },
    )
}

@Composable
fun CurrencyScreen(
    uiState: CurrencyUiState,
    onBackClick: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onCurrencySelected: (String) -> Unit,
) {
    Scaffold(
        containerColor = AppTheme.colors.background,
    ) { innerPadding ->
        MoneyTrackScreenBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Dimens.spacing16),
            ) {
                Spacer(modifier = Modifier.height(Dimens.spacing8))
                CurrencyHeader(onBackClick = onBackClick)
                Spacer(modifier = Modifier.height(18.dp))
                CurrencySearchField(
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChanged = onSearchQueryChanged,
                )
                Spacer(modifier = Modifier.height(14.dp))
                SelectedCurrencyCard(
                    selected = uiState.currencies.firstOrNull { it.code == uiState.selectedCurrencyCode },
                )
                Spacer(modifier = Modifier.height(14.dp))
                Box(modifier = Modifier.weight(1f)) {
                    CurrencyListCard(
                        currencies = uiState.currencies,
                        selectedCurrencyCode = uiState.selectedCurrencyCode,
                        onCurrencySelected = onCurrencySelected,
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrencyHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier
                .size(44.dp)
                .clickable(onClick = onBackClick),
            shape = CircleShape,
            color = AppTheme.colors.surfaceVariant.copy(alpha = 0.55f),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = DsR.drawable.arrow_left),
                    contentDescription = null,
                    tint = AppTheme.colors.onSurface,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
        Spacer(modifier = Modifier.width(Dimens.spacing12))
        Text(
            text = stringResource(id = R.string.currency_title),
            style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = AppTheme.colors.onBackground,
        )
    }
}

@Composable
private fun CurrencySearchField(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(
                color = AppTheme.colors.surface.copy(alpha = 0.88f),
                shape = RoundedCornerShape(Dimens.radius24),
            )
            .border(
                width = 1.dp,
                color = AppTheme.colors.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(Dimens.radius24),
            )
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = DsR.drawable.search),
            contentDescription = null,
            tint = AppTheme.colors.onSurfaceVariant,
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(Dimens.spacing10))
        Box(modifier = Modifier.weight(1f)) {
            if (searchQuery.isBlank()) {
                Text(
                    text = stringResource(id = R.string.currency_search_hint),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant,
                )
            }
            BasicTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                ),
                cursorBrush = SolidColor(AppTheme.colors.primary),
                textStyle = AppTheme.typography.bodySmall.copy(color = AppTheme.colors.onSurface),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun SelectedCurrencyCard(selected: CurrencyOption?) {
    if (selected == null) return
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = AppTheme.colors.primary.copy(alpha = 0.45f),
                shape = RoundedCornerShape(Dimens.radius24),
            ),
        shape = RoundedCornerShape(Dimens.radius24),
        color = AppTheme.colors.surface.copy(alpha = 0.9f),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacing16, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Selected Currency",
                    style = AppTheme.typography.labelSmall,
                    color = AppTheme.colors.primary,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = selected.countryName,
                    style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = AppTheme.colors.onBackground,
                )
                Text(
                    text = selected.code,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant,
                )
            }
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        color = AppTheme.colors.primary.copy(alpha = 0.22f),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = DsR.drawable.success),
                    contentDescription = null,
                    tint = AppTheme.colors.primary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun CurrencyListCard(
    currencies: List<CurrencyOption>,
    selectedCurrencyCode: String,
    onCurrencySelected: (String) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = AppTheme.colors.outline.copy(alpha = 0.28f),
                shape = RoundedCornerShape(Dimens.radius24),
            ),
        shape = RoundedCornerShape(Dimens.radius24),
        color = AppTheme.colors.surface.copy(alpha = 0.88f),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = Dimens.spacing4),
        ) {
            itemsIndexed(
                items = currencies,
                key = { _, item -> item.code },
            ) { index, currency ->
                CurrencyRow(
                    currency = currency,
                    isSelected = currency.code == selectedCurrencyCode,
                    onClick = { onCurrencySelected(currency.code) },
                )
                if (index != currencies.lastIndex) {
                    HorizontalDivider(
                        color = AppTheme.colors.outline.copy(alpha = 0.2f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = Dimens.spacing16),
                    )
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = Dimens.spacing10),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacing10),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = AppTheme.colors.primary.copy(alpha = 0.14f),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = DsR.drawable.currency_exchange),
                contentDescription = null,
                tint = AppTheme.colors.primary,
                modifier = Modifier.size(14.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = currency.countryName,
                style = AppTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = AppTheme.colors.onBackground,
            )
            Text(
                text = currency.code,
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant,
            )
        }
        if (isSelected) {
            Icon(
                imageVector = ImageVector.vectorResource(id = DsR.drawable.success),
                contentDescription = null,
                tint = AppTheme.colors.primary,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

@Suppress("UnusedPrivateMember")
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun CurrencyScreenLightPreview() {
    MoneyTrackTheme(darkTheme = false) {
        CurrencyScreen(
            uiState = currencyPreviewState(),
            onBackClick = {},
            onSearchQueryChanged = {},
            onCurrencySelected = {},
        )
    }
}

private fun currencyPreviewState(): CurrencyUiState = CurrencyUiState(
    selectedCurrencyCode = "INR",
    searchQuery = "",
    currencies = listOf(
        CurrencyOption(countryName = "India", code = "INR", symbol = "₹"),
        CurrencyOption(countryName = "United Kingdom", code = "GBP", symbol = "£"),
        CurrencyOption(countryName = "United States", code = "USD", symbol = "$"),
        CurrencyOption(countryName = "Germany", code = "EUR", symbol = "€"),
    ),
)
