// Copyright (c) 2026 shyakdas

package com.moneytrack.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.moneytrack.R
import ui.theme.AppTheme

@Composable
fun HomePlaceholderScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.home_placeholder_title),
            style = AppTheme.typography.headlineSmall,
            color = AppTheme.colors.onBackground,
        )
        Text(
            text = stringResource(R.string.home_placeholder_subtitle),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.onSurfaceVariant,
        )
    }
}
