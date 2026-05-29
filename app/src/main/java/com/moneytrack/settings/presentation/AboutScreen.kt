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
import ui.components.surface.MoneyTrackScreenBackground
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

private data class AboutSectionUiModel(
    val title: String,
    val description: String,
    val iconRes: Int,
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
            title = stringResource(id = R.string.about_version_title),
            description = stringResource(id = R.string.about_version_desc, uiState.versionName),
            iconRes = DsR.drawable.warning,
        ),
        AboutSectionUiModel(
            title = stringResource(id = R.string.about_privacy_title),
            description = stringResource(id = R.string.about_privacy_desc),
            iconRes = DsR.drawable.warning,
        ),
        AboutSectionUiModel(
            title = stringResource(id = R.string.about_storage_title),
            description = stringResource(id = R.string.about_storage_desc),
            iconRes = DsR.drawable.transaction,
        ),
        AboutSectionUiModel(
            title = stringResource(id = R.string.about_reminders_title),
            description = stringResource(id = R.string.about_reminders_desc),
            iconRes = DsR.drawable.notifiaction,
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
                AboutHeader(onBackClick = onBackClick)
                Spacer(modifier = Modifier.padding(top = 18.dp))
                AboutHero(
                    appName = stringResource(id = R.string.app_name),
                    summary = stringResource(id = R.string.about_summary),
                )
                Spacer(modifier = Modifier.padding(top = 16.dp))
                sections.forEach { section ->
                    AboutInfoCard(section = section)
                    Spacer(modifier = Modifier.padding(top = 14.dp))
                }
                Spacer(modifier = Modifier.padding(top = Dimens.spacing20))
            }
        }
    }
}

@Composable
private fun AboutHeader(onBackClick: () -> Unit) {
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
            text = stringResource(id = R.string.about_title),
            style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = AppTheme.colors.onBackground,
        )
    }
}

@Composable
private fun AboutHero(
    appName: String,
    summary: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(92.dp)
                .background(
                    color = AppTheme.colors.surface.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(Dimens.radius24),
                )
                .border(
                    width = 1.dp,
                    color = AppTheme.colors.outline.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(Dimens.radius24),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = DsR.drawable.line_chart_2),
                contentDescription = null,
                tint = AppTheme.colors.primary,
                modifier = Modifier.size(44.dp),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing4),
        ) {
            Text(
                text = appName,
                style = AppTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.onBackground,
            )
            Text(
                text = summary,
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AboutInfoCard(section: AboutSectionUiModel) {
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
                    imageVector = ImageVector.vectorResource(id = section.iconRes),
                    contentDescription = null,
                    tint = AppTheme.colors.primary,
                    modifier = Modifier.size(14.dp),
                )
            }
            Spacer(modifier = Modifier.width(Dimens.spacing12))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = section.title,
                    style = AppTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = AppTheme.colors.onBackground,
                )
                Text(
                    text = section.description,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant,
                )
            }
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
