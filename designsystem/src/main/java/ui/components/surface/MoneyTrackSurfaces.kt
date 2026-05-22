// Copyright (c) 2026 shyakdas

package ui.components.surface

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import ui.theme.AppTheme
import ui.theme.Dimens

@Composable
fun MoneyTrackScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AppTheme.colors.primaryContainer.copy(alpha = 0.34f),
                        AppTheme.colors.warningContainer.copy(alpha = 0.28f),
                        AppTheme.colors.background,
                    ),
                ),
            ),
    ) {
        content()
    }
}

@Composable
fun MoneyTrackCard(
    modifier: Modifier = Modifier,
    color: Color = AppTheme.colors.surface,
    contentPadding: PaddingValues = PaddingValues(Dimens.spacing20),
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = Dimens.borderNormal,
                color = AppTheme.colors.outline.copy(alpha = 0.42f),
                shape = RoundedCornerShape(Dimens.radius16),
            ),
        shape = RoundedCornerShape(Dimens.radius16),
        color = color,
        tonalElevation = Dimens.elevation0,
        shadowElevation = Dimens.elevation0,
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(contentPadding),
            content = content,
        )
    }
}
