// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneytrack.R
import com.moneytrack.designsystem.R as DsR
import com.moneytrack.settings.domain.model.AppThemeMode
import ui.components.navigation.topNav.TopNavigation
import ui.components.navigation.topNav.TopNavigationConfig
import ui.components.surface.MoneyTrackScreenBackground
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

private data class ThemeOptionUiModel(
    val themeMode: AppThemeMode,
    val title: String,
)

@Composable
fun ThemeRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: ThemeViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    ThemeScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onThemeModeSelected = { appThemeMode ->
            viewModel.onThemeModeSelected(appThemeMode)
            onBackClick()
        },
    )
}

@Composable
fun ThemeScreen(
    uiState: ThemeUiState,
    onBackClick: () -> Unit,
    onThemeModeSelected: (AppThemeMode) -> Unit,
) {
    val themeOptions = listOf(
        ThemeOptionUiModel(
            themeMode = AppThemeMode.DARK,
            title = stringResource(id = R.string.settings_theme_dark),
        ),
        ThemeOptionUiModel(
            themeMode = AppThemeMode.LIGHT,
            title = stringResource(id = R.string.settings_theme_light),
        ),
        ThemeOptionUiModel(
            themeMode = AppThemeMode.SYSTEM,
            title = stringResource(id = R.string.settings_theme_system),
        ),
    )

    Scaffold(
        containerColor = AppTheme.colors.background,
    ) { innerPadding ->
        MoneyTrackScreenBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                TopNavigation(
                    config = TopNavigationConfig.BackWithTitle(
                        title = stringResource(id = R.string.theme_title),
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
                    items(themeOptions, key = { it.themeMode.name }) { themeOption ->
                        ThemeRow(
                            themeOption = themeOption,
                            isSelected = themeOption.themeMode == uiState.selectedThemeMode,
                            onClick = { onThemeModeSelected(themeOption.themeMode) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeRow(
    themeOption: ThemeOptionUiModel,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.spacing20, vertical = Dimens.spacing20),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = themeOption.title,
            style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = AppTheme.colors.onBackground,
        )

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
private fun ThemeScreenLightPreview() {
    MoneyTrackTheme(darkTheme = false) {
        ThemeScreen(
            uiState = ThemeUiState(selectedThemeMode = AppThemeMode.DARK),
            onBackClick = {},
            onThemeModeSelected = {},
        )
    }
}

@Suppress("UnusedPrivateMember")
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ThemeScreenDarkPreview() {
    MoneyTrackTheme(darkTheme = true) {
        ThemeScreen(
            uiState = ThemeUiState(selectedThemeMode = AppThemeMode.SYSTEM),
            onBackClick = {},
            onThemeModeSelected = {},
        )
    }
}
