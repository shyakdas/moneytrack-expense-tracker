// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneytrack.R
import com.moneytrack.settings.domain.model.CurrencyOption
import com.moneytrack.designsystem.R as DsR
import ui.components.navigation.topNav.TopNavigation
import ui.components.navigation.topNav.TopNavigationConfig
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
        bottomBar = {
            CurrencySearchBar(
                searchQuery = uiState.searchQuery,
                onSearchQueryChanged = onSearchQueryChanged,
            )
        },
    ) { innerPadding ->
        MoneyTrackScreenBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                TopNavigation(
                    config = TopNavigationConfig.BackWithTitle(
                        title = stringResource(id = R.string.currency_title),
                        onBackClick = onBackClick,
                    ),
                    containerColor = Color.Transparent,
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Dimens.spacing16)
                        .padding(top = Dimens.spacing12),
                ) {
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
private fun CurrencySearchBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .imePadding()
            .navigationBarsPadding()
            .padding(
                start = Dimens.spacing16,
                end = Dimens.spacing16,
                top = Dimens.spacing8,
                bottom = Dimens.spacing16,
            ),
    ) {
        CurrencySearchField(
            searchQuery = searchQuery,
            onSearchQueryChanged = onSearchQueryChanged,
        )
    }
}

@Composable
private fun CurrencySearchField(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.buttonLargeHeight)
            .shadow(
                elevation = Dimens.elevation8,
                shape = RoundedCornerShape(Dimens.radius24),
                clip = false,
            )
            .background(
                color = AppTheme.colors.surface,
                shape = RoundedCornerShape(Dimens.radius24),
            )
            .border(
                width = Dimens.borderNormal,
                color = AppTheme.colors.outline.copy(alpha = 0.38f),
                shape = RoundedCornerShape(Dimens.radius24),
            )
            .padding(horizontal = Dimens.spacing20),
    ) {
        CurrencySearchPlaceholder(
            searchQuery = searchQuery,
            modifier = Modifier.align(Alignment.CenterStart),
        )

        BasicTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
            ),
            cursorBrush = SolidColor(AppTheme.colors.primary),
            textStyle = AppTheme.typography.bodyLarge.copy(
                color = AppTheme.colors.onSurface,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart),
        )
    }
}

@Composable
@Suppress("FunctionNaming")
private fun CurrencySearchPlaceholder(
    searchQuery: String,
    modifier: Modifier = Modifier,
) {
    if (searchQuery.isNotEmpty()) return

    Text(
        text = stringResource(id = R.string.currency_search_hint),
        style = AppTheme.typography.bodySmall,
        color = AppTheme.colors.onSurfaceVariant,
        modifier = modifier,
    )
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
            )
        }
    }
    HorizontalDivider(
        color = AppTheme.colors.outline.copy(alpha = 0.28f),
        thickness = 1.dp,
    )
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

@Suppress("UnusedPrivateMember")
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CurrencyScreenDarkPreview() {
    MoneyTrackTheme(darkTheme = true) {
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
