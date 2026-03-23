// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneytrack.R
import ui.components.navigation.topNav.TopNavigation
import ui.components.navigation.topNav.TopNavigationConfig
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

private data class AboutSectionUiModel(
    val title: String,
    val description: String,
)

@Composable
fun AboutRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: AboutViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    AboutScreen(
        uiState = uiState,
        onBackClick = onBackClick,
    )
}

@Composable
fun AboutScreen(
    uiState: AboutUiState,
    onBackClick: () -> Unit,
) {
    val sections = listOf(
        AboutSectionUiModel(
            title = stringResource(id = R.string.about_privacy_title),
            description = stringResource(id = R.string.about_privacy_desc),
        ),
        AboutSectionUiModel(
            title = stringResource(id = R.string.about_storage_title),
            description = stringResource(id = R.string.about_storage_desc),
        ),
        AboutSectionUiModel(
            title = stringResource(id = R.string.about_reminders_title),
            description = stringResource(id = R.string.about_reminders_desc),
        ),
    )

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
                    title = stringResource(id = R.string.about_title),
                    onBackClick = onBackClick,
                ),
                containerColor = Color.Transparent,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimens.spacing16)
                    .padding(top = Dimens.spacing12, bottom = Dimens.spacing24),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacing16),
            ) {
                AboutHero(
                    appName = stringResource(id = R.string.app_name),
                    summary = stringResource(id = R.string.about_summary),
                )
                AboutVersionCard(versionName = uiState.versionName)
                sections.forEach { section ->
                    AboutInfoCard(section = section)
                }
            }
        }
    }
}

@Composable
private fun AboutHero(
    appName: String,
    summary: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Dimens.spacing8,
                vertical = Dimens.spacing8,
            ),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing8),
    ) {
        Text(
            text = appName,
            style = AppTheme.typography.headlineMedium,
            color = AppTheme.colors.onBackground,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = summary,
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun AboutVersionCard(versionName: String) {
    AboutInfoCard(
        section = AboutSectionUiModel(
            title = stringResource(id = R.string.about_version_title),
            description = stringResource(
                id = R.string.about_version_desc,
                versionName,
            ),
        ),
    )
}

@Composable
private fun AboutInfoCard(section: AboutSectionUiModel) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radius24),
        color = AppTheme.colors.surface,
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacing20),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing8),
        ) {
            Text(
                text = section.title,
                style = AppTheme.typography.headlineSmall,
                color = AppTheme.colors.onBackground,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = section.description,
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant,
            )
        }
    }
}

@Suppress("UnusedPrivateMember")
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AboutScreenLightPreview() {
    MoneyTrackTheme(darkTheme = false) {
        AboutScreen(
            uiState = AboutUiState(versionName = "1.0.0-dev"),
            onBackClick = {},
        )
    }
}

@Suppress("UnusedPrivateMember")
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun AboutScreenDarkPreview() {
    MoneyTrackTheme(darkTheme = true) {
        AboutScreen(
            uiState = AboutUiState(versionName = "1.0.0-dev"),
            onBackClick = {},
        )
    }
}
