package ui.components.navigation.bars

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.moneytrack.core.R
import ui.components.navigation.button.IconActionButton
import ui.components.navigation.button.IconButtonShape
import ui.components.navigation.button.IconButtonVariant
import ui.components.navigation.common.SelectorChip

@Composable
fun FilterBar(
    onMonthClick: () -> Unit,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        SelectorChip(
            label = "Month",
            onClick = onMonthClick,
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

