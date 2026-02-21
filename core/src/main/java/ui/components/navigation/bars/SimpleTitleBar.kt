package ui.components.navigation.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
fun SimpleTitleBar(
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.buttonLargeHeight)
            .background(AppTheme.colors.background)
            .padding(horizontal = Dimens.spacing16),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = title,
            style = AppTheme.typography.headlineSmall,
            color = AppTheme.colors.onSurface
        )
    }
}

@Preview(name = "Simple Title Bar – Light & Dark")
@Composable
private fun SimpleTitleBarPreview() {
    Column {
        MoneyTrackTheme(darkTheme = false) {
            SimpleTitleBar(title = "Yesterday")
        }

        Spacer(
            modifier = Modifier.height(Dimens.spacing16)
        )

       MoneyTrackTheme(darkTheme = true) {
            SimpleTitleBar(title = "Yesterday")
        }
    }
}

