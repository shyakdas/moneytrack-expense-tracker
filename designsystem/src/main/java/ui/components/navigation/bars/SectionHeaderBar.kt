// Copyright (c) 2026 shyakdas

package ui.components.navigation.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ui.components.navigation.common.SeeAllPill
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
fun SectionHeaderBar(
    title: String,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.buttonLargeHeight)
            .background(AppTheme.colors.background)
            .padding(horizontal = Dimens.spacing16),
        verticalAlignment = Alignment.CenterVertically
    ) {

        androidx.compose.material3.Text(
            text = title,
            style = AppTheme.typography.headlineSmall,
            color = AppTheme.colors.onSurface
        )

        Spacer(modifier = Modifier.weight(1f))

        SeeAllPill(onClick = onSeeAllClick)
    }
}


@Preview(name = "Section Header Bar – Light & Dark")
@Composable
private fun SectionHeaderBarPreview() {
    Column {

        MoneyTrackTheme(darkTheme = false) {
            SectionHeaderBar(
                title = "Spend Frequency",
                onSeeAllClick = {}
            )
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        MoneyTrackTheme(darkTheme = true) {
            SectionHeaderBar(
                title = "Spend Frequency",
                onSeeAllClick = {}
            )
        }
    }
}
