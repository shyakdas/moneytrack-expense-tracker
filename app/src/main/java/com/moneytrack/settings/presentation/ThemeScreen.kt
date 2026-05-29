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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import ui.components.surface.MoneyTrackScreenBackground
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

private data class ThemeOptionUiModel(
    val themeMode: AppThemeMode,
    val title: String,
    val subtitle: String,
    val iconRes: Int,
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
            subtitle = "Use a dark color palette for low light environments.",
            iconRes = DsR.drawable.show,
        ),
        ThemeOptionUiModel(
            themeMode = AppThemeMode.LIGHT,
            title = stringResource(id = R.string.settings_theme_light),
            subtitle = "Use a light color palette for bright environments.",
            iconRes = DsR.drawable.resource_new,
        ),
        ThemeOptionUiModel(
            themeMode = AppThemeMode.SYSTEM,
            title = stringResource(id = R.string.settings_theme_system),
            subtitle = "Match your device appearance settings.",
            iconRes = DsR.drawable.transaction_color,
        ),
    )

    Scaffold(
        containerColor = AppTheme.colors.background,
    ) { innerPadding ->
        MoneyTrackScreenBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Dimens.spacing16)
                    .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.padding(top = Dimens.spacing8))
                ThemeHeader(onBackClick = onBackClick)
                Spacer(modifier = Modifier.padding(top = 22.dp))
                themeOptions.forEach { option ->
                    ThemeOptionCard(
                        option = option,
                        isSelected = option.themeMode == uiState.selectedThemeMode,
                        onClick = { onThemeModeSelected(option.themeMode) },
                    )
                    Spacer(modifier = Modifier.padding(top = 14.dp))
                }
                ThemeInfoCard()
                Spacer(modifier = Modifier.padding(top = Dimens.spacing20))
            }
        }
    }
}

@Composable
private fun ThemeHeader(onBackClick: () -> Unit) {
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
            text = stringResource(id = R.string.theme_title),
            style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = AppTheme.colors.onBackground,
        )
    }
}

@Composable
private fun ThemeOptionCard(
    option: ThemeOptionUiModel,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isSelected) {
                    AppTheme.colors.primary.copy(alpha = 0.5f)
                } else {
                    AppTheme.colors.outline.copy(alpha = 0.28f)
                },
                shape = RoundedCornerShape(Dimens.radius24),
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Dimens.radius24),
        color = AppTheme.colors.surface.copy(alpha = 0.88f),
    ) {
        Row(
            modifier = Modifier
            .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
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
                    imageVector = ImageVector.vectorResource(id = option.iconRes),
                    contentDescription = null,
                    tint = AppTheme.colors.primary,
                    modifier = Modifier.size(14.dp),
                )
            }
            Spacer(modifier = Modifier.width(Dimens.spacing12))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    style = AppTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = AppTheme.colors.onBackground,
                )
                Text(
                    text = option.subtitle,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant,
                )
            }
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(
                            color = AppTheme.colors.primary.copy(alpha = 0.24f),
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = DsR.drawable.success),
                        contentDescription = null,
                        tint = AppTheme.colors.primary,
                        modifier = Modifier.size(14.dp),
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .border(
                            width = 1.dp,
                            color = AppTheme.colors.outline.copy(alpha = 0.6f),
                            shape = CircleShape,
                        ),
                )
            }
        }
    }
}

@Composable
private fun ThemeInfoCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = AppTheme.colors.outline.copy(alpha = 0.22f),
                shape = RoundedCornerShape(Dimens.radius24),
            ),
        shape = RoundedCornerShape(Dimens.radius24),
        color = AppTheme.colors.surface.copy(alpha = 0.88f),
    ) {
        Row(
            modifier = Modifier
            .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
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
                    imageVector = ImageVector.vectorResource(id = DsR.drawable.warning),
                    contentDescription = null,
                    tint = AppTheme.colors.primary,
                    modifier = Modifier.size(14.dp),
                )
            }
            Spacer(modifier = Modifier.width(Dimens.spacing12))
            Text(
                text = "System follows your device appearance settings.",
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant,
            )
        }
    }
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
