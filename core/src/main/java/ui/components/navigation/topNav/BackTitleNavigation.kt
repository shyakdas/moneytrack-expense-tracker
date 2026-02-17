package ui.components.navigation.topNav

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneytrack.core.R

@Composable
internal fun BackTitleNavigation(
    config: TopNavigationConfig.BackWithTitle
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = config.onBackClick) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.arrow_left),
                contentDescription = "Back"
            )
        }

        Text(
            text = config.title,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )

        if (config.showMore) {
            IconButton(onClick = { config.onMoreClick?.invoke() }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.more_horizontal),
                    contentDescription = "Back"
                )
            }
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}

@Preview(
    name = "Back Title Navigation",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun BackTitleNavigationPreview() {
    MaterialTheme {
        BackTitleNavigation(
            config = TopNavigationConfig.BackWithTitle(
                title = "Notification",
                showMore = true,
                onBackClick = {},
                onMoreClick = {}
            )
        )
    }
}
