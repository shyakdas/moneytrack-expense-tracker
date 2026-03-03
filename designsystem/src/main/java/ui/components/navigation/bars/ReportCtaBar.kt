package ui.components.navigation.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.moneytrack.designsystem.R
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
fun ReportCtaBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.spacing16)
            .background(
                color = AppTheme.colors.primary.copy(alpha = 0.12f), // ✅ theme-aware
                shape = RoundedCornerShape(Dimens.radius16)
            )
            .clickable(onClick = onClick)
            .padding(Dimens.spacing16),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "See your financial report",
            style = AppTheme.typography.bodyLarge,
            color = AppTheme.colors.primary
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.arrow_right_2),
            contentDescription = null,
            tint = AppTheme.colors.primary
        )
    }
}


@Preview(name = "Report CTA Bar – Light & Dark")
@Composable
private fun ReportCtaBarPreview() {
    Column {

        MoneyTrackTheme(darkTheme = false) {
            ReportCtaBar(onClick = {})
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        MoneyTrackTheme(darkTheme = true) {
            ReportCtaBar(onClick = {})
        }
    }
}
