package ui.components.navigation.topNav

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun CenterTitleNavigation(
    config: TopNavigationConfig.TitleOnly
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = config.title,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview(
    name = "Center Title Navigation",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun CenterTitleNavigationPreview() {
    MaterialTheme {
        CenterTitleNavigation(
            config = TopNavigationConfig.TitleOnly(
                title = "Dashboard"
            )
        )
    }
}
