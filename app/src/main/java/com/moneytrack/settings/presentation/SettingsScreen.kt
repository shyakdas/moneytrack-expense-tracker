// Copyright (c) 2026 shyakdas

@file:Suppress("LongMethod")

package com.moneytrack.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneytrack.R
import com.moneytrack.designsystem.R as DsR
import com.moneytrack.settings.domain.model.AppThemeMode
import ui.components.surface.MoneyTrackScreenBackground
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

private enum class SettingsAction {
    CURRENCY,
    LANGUAGE,
    THEME,
    SECURITY,
    NOTIFICATION,
    ABOUT,
    NONE,
}

private const val LANGUAGE_COMING_SOON_MESSAGE =
    "English is currently available. More languages are coming soon in a future release."

private enum class SettingsSectionType {
    GENERAL,
    SECURITY_ALERTS,
    SUPPORT,
}

private data class SettingsItemUiModel(
    val title: String,
    val subtitle: String,
    val value: String? = null,
    val action: SettingsAction = SettingsAction.NONE,
    val iconRes: Int,
)

private data class SettingsSectionUiModel(
    val type: SettingsSectionType,
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val items: List<SettingsItemUiModel>,
)

@Composable
fun SettingsRoute(
    actions: SettingsScreenActions,
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    SettingsScreen(
        uiState = uiState,
        actions = actions,
    )
}

data class SettingsScreenActions(
    val onBackClick: () -> Unit,
    val onCurrencyClick: () -> Unit,
    val onThemeClick: () -> Unit,
    val onSecurityClick: () -> Unit,
    val onNotificationClick: () -> Unit,
    val onAboutClick: () -> Unit,
)

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
) {
    val context = LocalContext.current
    val sections = settingsSections(uiState = uiState)
    Scaffold(
        containerColor = AppTheme.colors.background,
    ) { innerPadding ->
        MoneyTrackScreenBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimens.spacing16),
            ) {
                Spacer(modifier = Modifier.height(Dimens.spacing8))
                SettingsHeroHeader(onBackClick = actions.onBackClick)
                Spacer(modifier = Modifier.height(24.dp))
                sections.forEach { section ->
                    SettingsSection(
                        section = section,
                        onItemClick = { item ->
                            when (item.action) {
                                SettingsAction.CURRENCY -> actions.onCurrencyClick()
                                SettingsAction.LANGUAGE -> {
                                    Toast
                                        .makeText(
                                            context,
                                            LANGUAGE_COMING_SOON_MESSAGE,
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                }
                                SettingsAction.THEME -> actions.onThemeClick()
                                SettingsAction.SECURITY -> actions.onSecurityClick()
                                SettingsAction.NOTIFICATION -> actions.onNotificationClick()
                                SettingsAction.ABOUT -> actions.onAboutClick()
                                SettingsAction.NONE -> Unit
                            }
                        },
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
private fun settingsSections(uiState: SettingsUiState): List<SettingsSectionUiModel> = listOf(
    SettingsSectionUiModel(
        type = SettingsSectionType.GENERAL,
        title = "General",
        subtitle = "Basic preferences and appearance",
        iconRes = DsR.drawable.settings,
        items = listOf(
            SettingsItemUiModel(
                title = stringResource(id = R.string.settings_currency),
                subtitle = "Select your default currency",
                value = uiState.currencySymbol,
                action = SettingsAction.CURRENCY,
                iconRes = DsR.drawable.currency_exchange,
            ),
            SettingsItemUiModel(
                title = stringResource(id = R.string.settings_language),
                subtitle = "Choose your preferred language",
                value = uiState.language,
                action = SettingsAction.LANGUAGE,
                iconRes = DsR.drawable.search,
            ),
            SettingsItemUiModel(
                title = stringResource(id = R.string.settings_theme),
                subtitle = "Customize app appearance",
                value = uiState.themeLabel(),
                action = SettingsAction.THEME,
                iconRes = DsR.drawable.show,
            ),
        ),
    ),
    SettingsSectionUiModel(
        type = SettingsSectionType.SECURITY_ALERTS,
        title = "Security & Alerts",
        subtitle = "Protect your account and stay updated",
        iconRes = DsR.drawable.warning,
        items = listOf(
            SettingsItemUiModel(
                title = stringResource(id = R.string.settings_security),
                subtitle = "Manage app security",
                value = uiState.securityLabel(),
                action = SettingsAction.SECURITY,
                iconRes = DsR.drawable.logout,
            ),
            SettingsItemUiModel(
                title = stringResource(id = R.string.settings_notification),
                subtitle = "Set how often you want updates",
                value = pluralStringResource(
                    id = R.plurals.settings_notification_summary,
                    uiState.notificationsPerDay,
                    uiState.notificationsPerDay,
                ),
                action = SettingsAction.NOTIFICATION,
                iconRes = DsR.drawable.notifiaction,
            ),
        ),
    ),
    SettingsSectionUiModel(
        type = SettingsSectionType.SUPPORT,
        title = "Support",
        subtitle = "Get help and learn more",
        iconRes = DsR.drawable.resource_new,
        items = listOf(
            SettingsItemUiModel(
                title = stringResource(id = R.string.settings_about),
                subtitle = "App info and legal details",
                action = SettingsAction.ABOUT,
                iconRes = DsR.drawable.document,
            ),
            SettingsItemUiModel(
                title = stringResource(id = R.string.settings_help),
                subtitle = "FAQs and contact support",
                action = SettingsAction.NONE,
                iconRes = DsR.drawable.search,
            ),
        ),
    ),
)

@Composable
private fun SettingsHeroHeader(onBackClick: () -> Unit) {
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
            text = stringResource(id = R.string.settings_title),
            style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = AppTheme.colors.onBackground,
        )
    }
}

@Composable
private fun SettingsSection(
    section: SettingsSectionUiModel,
    onItemClick: (SettingsItemUiModel) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing8),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacing10),
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        color = AppTheme.colors.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(Dimens.radius16),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = section.iconRes),
                    contentDescription = null,
                    tint = AppTheme.colors.primary,
                    modifier = Modifier.size(13.dp),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacing2)) {
                Text(
                    text = section.title,
                    style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = AppTheme.colors.onBackground,
                )
                Text(
                    text = section.subtitle,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant,
                )
            }
        }

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
            Column(
                modifier = Modifier.padding(vertical = Dimens.spacing4),
            ) {
                section.items.forEachIndexed { index, item ->
                    SettingsActionRow(item = item, onClick = { onItemClick(item) })
                    if (index != section.items.lastIndex) {
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
}

@Composable
private fun SettingsActionRow(
    item: SettingsItemUiModel,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = item.action != SettingsAction.NONE, onClick = onClick)
            .padding(horizontal = Dimens.spacing12, vertical = Dimens.spacing10),
        verticalAlignment = Alignment.CenterVertically,
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
                imageVector = ImageVector.vectorResource(id = item.iconRes),
                contentDescription = null,
                tint = AppTheme.colors.primary,
                modifier = Modifier.size(13.dp),
            )
        }
        Spacer(modifier = Modifier.width(Dimens.spacing10))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = AppTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = AppTheme.colors.onBackground,
            )
            Text(
                text = item.subtitle,
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacing10),
        ) {
            if (!item.value.isNullOrBlank()) {
                Text(
                    text = item.value,
                    style = AppTheme.typography.bodySmall,
                    textAlign = TextAlign.End,
                    color = AppTheme.colors.onSurface,
                )
            }
            Icon(
                imageVector = ImageVector.vectorResource(id = DsR.drawable.arrow_right_2),
                contentDescription = null,
                tint = AppTheme.colors.onSurfaceVariant,
                modifier = Modifier.size(14.dp),
            )
        }
    }
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
            actions = SettingsScreenActions(
                onBackClick = {},
                onCurrencyClick = {},
                onThemeClick = {},
                onSecurityClick = {},
                onNotificationClick = {},
                onAboutClick = {},
            ),
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
        SettingsSecurityType.NOT_SET -> stringResource(id = R.string.settings_security_none)
    }
