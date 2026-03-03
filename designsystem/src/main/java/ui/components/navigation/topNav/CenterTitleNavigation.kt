package ui.components.navigation.topNav

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import ui.theme.Dimens
import androidx.compose.material3.Surface
import ui.theme.AppTheme
import ui.theme.MoneyTrackTheme

@Composable
internal fun CenterTitleNavigation(
    config: TopNavigationConfig.TitleOnly
) {
    Surface(
        color = AppTheme.colors.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.buttonLargeHeight)
                .padding(horizontal = Dimens.spacing16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = config.title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = AppTheme.typography.headlineSmall,
                color = AppTheme.colors.onSurface
            )
        }
    }
}

@Preview(name = "Center Title Navigation – Light & Dark")
@Composable
private fun CenterTitleNavigationPreview() {
    androidx.compose.foundation.layout.Column {

        MoneyTrackTheme(darkTheme = false) {
            Surface(color = AppTheme.colors.background) {
                CenterTitleNavigation(
                    config = TopNavigationConfig.TitleOnly(
                        title = "Dashboard"
                    )
                )
            }
        }

        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.height(Dimens.spacing16)
        )

        MoneyTrackTheme(darkTheme = true) {
            Surface(color = AppTheme.colors.background) {
                CenterTitleNavigation(
                    config = TopNavigationConfig.TitleOnly(
                        title = "Dashboard"
                    )
                )
            }
        }
    }
}
