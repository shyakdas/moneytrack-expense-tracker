// Copyright (c) 2026 shyakdas

package ui.components.navigation.bars

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.moneytrack.designsystem.R
import ui.components.navigation.button.ButtonGroup
import ui.components.navigation.button.IconActionButton
import ui.components.navigation.button.IconButtonShape
import ui.components.navigation.button.IconButtonVariant
import ui.components.navigation.common.SelectorChip
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
fun ChartActionBar(
    onMonthClick: () -> Unit,
    onLineChartClick: () -> Unit,
    onPieChartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.buttonLargeHeight)
            .padding(horizontal = Dimens.spacing16),
        verticalAlignment = Alignment.CenterVertically
    ) {

        SelectorChip(
            label = "Month",
            selected = false,
            onClick = onMonthClick
        )

        Spacer(modifier = Modifier.weight(1f))

        ButtonGroup {
            IconActionButton(
                icon = ImageVector.vectorResource(R.drawable.line_chart_2),
                contentDescription = "Line chart",
                onClick = onLineChartClick,
                variant = IconButtonVariant.FILLED,
                shape = IconButtonShape.ROUNDED_RECT
            )

            IconActionButton(
                icon = ImageVector.vectorResource(R.drawable.warning),
                contentDescription = "Pie chart",
                onClick = onPieChartClick,
                variant = IconButtonVariant.OUTLINED,
                shape = IconButtonShape.ROUNDED_RECT
            )
        }
    }
}

@Preview(name = "Chart Action Bar – Light & Dark")
@Composable
private fun ChartActionBarPreview() {
    Column {
        MoneyTrackTheme(darkTheme = false) {
            ChartActionBar(
                onMonthClick = {},
                onLineChartClick = {},
                onPieChartClick = {}
            )
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        MoneyTrackTheme(darkTheme = true) {
            ChartActionBar(
                onMonthClick = {},
                onLineChartClick = {},
                onPieChartClick = {}
            )
        }
    }
}
