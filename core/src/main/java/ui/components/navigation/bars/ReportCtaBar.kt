package ui.components.navigation.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneytrack.core.R
import ui.theme.Violet100
import ui.theme.Violet20

@Composable
fun ReportCtaBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = Violet20,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "See your financial report",
            style = MaterialTheme.typography.bodyLarge,
            color = Violet100
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.arrow_right_2),
            contentDescription = null,
            tint = Violet100
        )
    }
}

@Preview(
    name = "Report CTA Bar",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun ReportCtaBarPreview() {
    MaterialTheme {
        ReportCtaBar(
            onClick = {}
        )
    }
}
