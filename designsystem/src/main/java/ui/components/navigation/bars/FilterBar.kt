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
import ui.components.navigation.button.IconActionButton
import ui.components.navigation.button.IconButtonShape
import ui.components.navigation.button.IconButtonVariant
import ui.components.navigation.common.SelectorChip
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
fun FilterBar(
    onMonthClick: () -> Unit,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.filterHeight)
            .padding(horizontal = Dimens.spacing16),
        verticalAlignment = Alignment.CenterVertically
    ) {

        SelectorChip(
            label = "Month",
            onClick = onMonthClick,
            selected = false,
            leadingIcon = ImageVector.vectorResource(R.drawable.arrow_down_2)
        )

        Spacer(modifier = Modifier.weight(1f))

        IconActionButton(
            icon = ImageVector.vectorResource(R.drawable.sort),
            contentDescription = "Sort",
            onClick = onSortClick,
            variant = IconButtonVariant.OUTLINED,
            shape = IconButtonShape.ROUNDED_RECT
        )
    }
}

@Preview(
    name = "Filter Bar – Light & Dark",
    showBackground = true
)
@Composable
private fun FilterBarPreview() {
    Column {
        MoneyTrackTheme(darkTheme = false) {
            FilterBar(
                onMonthClick = {},
                onSortClick = {}
            )
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        MoneyTrackTheme(darkTheme = true) {
            FilterBar(
                onMonthClick = {},
                onSortClick = {}
            )
        }
    }
}
