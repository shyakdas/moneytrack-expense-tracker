package ui.components.navigation.common

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.theme.Violet100
import ui.theme.Violet20

@Composable
fun SeeAllPill(
    modifier: Modifier = Modifier,
    text: String = "See All",
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .height(40.dp)
            .background(
                color = Violet20,
                shape = RoundedCornerShape(40.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Violet100
        )
    }
}


@Preview(
    name = "See All Pill",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun SeeAllPillPreview() {
    MaterialTheme {
        SeeAllPill(
            onClick = {}
        )
    }
}
