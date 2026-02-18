package ui.components.navigation.bars

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SimpleTitleBar(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 16.dp),
        style = MaterialTheme.typography.titleLarge
    )
}

@Preview(
    name = "Simple Title Bar",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun SimpleTitleBarPreview() {
    MaterialTheme {
        SimpleTitleBar(
            title = "Yesterday"
        )
    }
}
