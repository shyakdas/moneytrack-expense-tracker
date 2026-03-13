// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
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
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

private enum class SettingsAction {
    CURRENCY,
    THEME,
    NONE,
}

private data class SettingsItemUiModel(
    val title: String,
    val value: String? = null,
    val action: SettingsAction = SettingsAction.NONE,
)

@Composable
fun SettingsRoute(
    onBackClick: () -> Unit,
    onCurrencyClick: () -> Unit,
    onThemeClick: () -> Unit,
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    SettingsScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onCurrencyClick = onCurrencyClick,
        onThemeClick = onThemeClick,
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onBackClick: () -> Unit,
    onCurrencyClick: () -> Unit,
    onThemeClick: () -> Unit,
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
                    title = stringResource(id = R.string.settings_title),
                    onBackClick = onBackClick,
                ),
                containerColor = Color.Transparent,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.spacing16),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacing20),
            ) {
                Spacer(modifier = Modifier.height(Dimens.spacing12))
                SettingsCard(
                    items = primarySettingsItems(uiState = uiState),
                    onCurrencyClick = onCurrencyClick,
                    onThemeClick = onThemeClick,
                )
                SettingsCard(
                    items = secondarySettingsItems(),
                    onCurrencyClick = onCurrencyClick,
                    onThemeClick = onThemeClick,
                )
            }
        }
    }
}

@Composable
private fun primarySettingsItems(
    uiState: SettingsUiState,
): List<SettingsItemUiModel> = listOf(
    SettingsItemUiModel(
        title = stringResource(id = R.string.settings_currency),
        value = uiState.currencySymbol,
        action = SettingsAction.CURRENCY,
    ),
    SettingsItemUiModel(
        title = stringResource(id = R.string.settings_language),
        value = uiState.language,
    ),
    SettingsItemUiModel(
        title = stringResource(id = R.string.settings_theme),
        value = uiState.themeLabel(),
        action = SettingsAction.THEME,
    ),
    SettingsItemUiModel(
        title = stringResource(id = R.string.settings_security),
        value = uiState.securityLabel(),
    ),
    SettingsItemUiModel(
        title = stringResource(id = R.string.settings_notification),
        value = pluralStringResource(
            id = R.plurals.settings_notification_summary,
            uiState.notificationsPerDay,
            uiState.notificationsPerDay,
        ),
    ),
)

@Composable
private fun secondarySettingsItems(): List<SettingsItemUiModel> = listOf(
    SettingsItemUiModel(
        title = stringResource(id = R.string.settings_about),
    ),
    SettingsItemUiModel(
        title = stringResource(id = R.string.settings_help),
    ),
)

@Composable
private fun SettingsCard(
    items: List<SettingsItemUiModel>,
    onCurrencyClick: () -> Unit,
    onThemeClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radius24),
        color = AppTheme.colors.surface,
    ) {
        Column {
            items.forEachIndexed { index, item ->
                SettingsRow(
                    item = item,
                    onClick = {
                        if (item.action == SettingsAction.CURRENCY) {
                            onCurrencyClick()
                        } else if (item.action == SettingsAction.THEME) {
                            onThemeClick()
                        }
                    },
                )
                if (index != items.lastIndex) {
                    HorizontalDivider(
                        color = AppTheme.colors.outline.copy(alpha = 0.2f),
                        thickness = 1.dp,
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(
    item: SettingsItemUiModel,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = item.action != SettingsAction.NONE,
                onClick = onClick,
            )
            .padding(horizontal = Dimens.spacing20, vertical = Dimens.spacing20),
    ) {
        Text(
            text = item.title,
            style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = AppTheme.colors.onBackground,
            modifier = Modifier.align(Alignment.CenterStart),
        )

        SettingsRowTrailing(
            value = item.value,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}

@Composable
private fun SettingsRowTrailing(
    value: String?,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacing12),
    ) {
        if (!value.isNullOrBlank()) {
            Text(
                text = value,
                style = AppTheme.typography.bodyLarge,
                color = AppTheme.colors.onSurfaceVariant,
            )
        }
        Icon(
            imageVector = ImageVector.vectorResource(DsR.drawable.arrow_right_2),
            contentDescription = null,
            tint = AppTheme.colors.primary,
        )
    }
}

@Composable
private fun SettingsUiState.themeLabel(): String =
    when (themeMode) {
        AppThemeMode.SYSTEM -> stringResource(id = R.string.settings_theme_system)
        AppThemeMode.LIGHT -> stringResource(id = R.string.settings_theme_light)
        AppThemeMode.DARK -> stringResource(id = R.string.settings_theme_dark)
    }

@Composable
private fun SettingsUiState.securityLabel(): String =
    when (securityType) {
        SettingsSecurityType.PIN -> stringResource(id = R.string.settings_security_pin)
        SettingsSecurityType.BIOMETRIC -> stringResource(id = R.string.settings_security_biometric)
        SettingsSecurityType.NOT_SET -> stringResource(id = R.string.settings_security_not_set)
    }

@Suppress("UnusedPrivateMember")
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun SettingsScreenPreview() {
    MoneyTrackTheme(darkTheme = false) {
        SettingsScreen(
            uiState = SettingsUiState(
                currencySymbol = "₹",
                language = "English",
                themeMode = AppThemeMode.SYSTEM,
                securityType = SettingsSecurityType.BIOMETRIC,
                notificationsPerDay = 3,
            ),
            onBackClick = {},
            onCurrencyClick = {},
            onThemeClick = {},
        )
    }
}
