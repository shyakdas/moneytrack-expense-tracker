// Copyright (c) 2026 shyakdas

package ui.components.navigation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ui.theme.AppTheme
import ui.theme.Dimens

@Composable
fun SeeAllPill(
    modifier: Modifier = Modifier,
    text: String = "See All",
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .height(Dimens.spacing36)
            .background(
                color = AppTheme.colors.primary.copy(alpha = 0.12f), // ✅ theme-aware
                shape = RoundedCornerShape(Dimens.radius40)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.spacing16),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.primary
        )
    }
}

@Preview(name = "See All Pill – Light & Dark")
@Composable
private fun SeeAllPillPreview() {
    androidx.compose.foundation.layout.Column {

        ui.theme.MoneyTrackTheme(darkTheme = false) {
            SeeAllPill(onClick = {})
        }

        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.height(Dimens.spacing16)
        )

        ui.theme.MoneyTrackTheme(darkTheme = true) {
            SeeAllPill(onClick = {})
        }
    }
}
