package ui.components.navigation.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.moneytrack.core.R
import ui.components.navigation.button.IconActionButton
import ui.components.navigation.button.IconButtonShape
import ui.components.navigation.button.IconButtonVariant
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

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
            .height(Dimens.buttonLargeHeight)
            .background(AppTheme.colors.background)
            .padding(horizontal = Dimens.spacing16),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconActionButton(
            icon = ImageVector.vectorResource(R.drawable.arrow_left_2),
            contentDescription = "Previous month",
            onClick = onPrevious,
            variant = IconButtonVariant.FILLED,
            shape = IconButtonShape.CIRCLE
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = month,
            style = AppTheme.typography.headlineSmall,
            color = AppTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.weight(1f))

        IconActionButton(
            icon = ImageVector.vectorResource(R.drawable.arrow_right_2),
            contentDescription = "Next month",
            onClick = onNext,
            variant = IconButtonVariant.FILLED,
            shape = IconButtonShape.CIRCLE
        )
    }
}

@Preview(name = "Month Navigation Bar – Light & Dark")
@Composable
private fun MonthNavigationBarPreview() {
    Column {

        MoneyTrackTheme(darkTheme = false) {
            MonthNavigationBar(
                month = "May",
                onPrevious = {},
                onNext = {}
            )
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        MoneyTrackTheme(darkTheme = true) {
            MonthNavigationBar(
                month = "May",
                onPrevious = {},
                onNext = {}
            )
        }
    }
}
