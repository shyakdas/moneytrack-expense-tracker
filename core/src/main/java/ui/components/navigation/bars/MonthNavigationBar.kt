package ui.components.navigation.bars

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

@Composable
fun MonthNavigationBar(
    month: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        IconActionButton(
            icon = ImageVector.vectorResource(R.drawable.arrow_right_2),
            contentDescription = "Previous month",
            onClick = onPrevious,
            variant = IconButtonVariant.FILLED,
            shape = IconButtonShape.CIRCLE
        )

        Spacer(modifier = Modifier.width(24.dp))

        Text(
            text = month,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.width(24.dp))

        IconActionButton(
            icon = ImageVector.vectorResource(R.drawable.arrow_right_2),
            contentDescription = "Next month",
            onClick = onNext,
            variant = IconButtonVariant.FILLED,
            shape = IconButtonShape.CIRCLE
        )
    }
}
