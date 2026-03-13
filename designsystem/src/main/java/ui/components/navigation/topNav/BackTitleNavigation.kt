// Copyright (c) 2026 shyakdas

package ui.components.navigation.topNav

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.moneytrack.designsystem.R
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
internal fun BackTitleNavigation(
    config: TopNavigationConfig.BackWithTitle,
    containerColor: Color,
) {
    Surface(
        color = containerColor,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.buttonLargeHeight)
                .padding(horizontal = Dimens.spacing16),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = config.onBackClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.arrow_left),
                    contentDescription = "Back",
                    tint = AppTheme.colors.onSurface
                )
            }

            Text(
                text = config.title,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = AppTheme.typography.headlineSmall,
                color = AppTheme.colors.onSurface
            )

            if (config.showMore) {
                IconButton(onClick = { config.onMoreClick?.invoke() }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.more_horizontal),
                        contentDescription = "More",
                        tint = AppTheme.colors.onSurface
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(Dimens.spacing48))
            }
        }
    }
}

@Preview(name = "Back Title Navigation – Light & Dark")
@Composable
private fun BackTitleNavigationPreview() {
    Column {

        MoneyTrackTheme(darkTheme = false) {
            Surface(color = AppTheme.colors.background) {
                BackTitleNavigation(
                    config = TopNavigationConfig.BackWithTitle(
                        title = "Notification",
                        showMore = true,
                        onBackClick = {},
                        onMoreClick = {}
                    ),
                    containerColor = AppTheme.colors.background,
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        MoneyTrackTheme(darkTheme = true) {
            Surface(color = AppTheme.colors.background) {
                BackTitleNavigation(
                    config = TopNavigationConfig.BackWithTitle(
                        title = "Notification",
                        showMore = true,
                        onBackClick = {},
                        onMoreClick = {}
                    ),
                    containerColor = AppTheme.colors.background,
                )
            }
        }
    }
}
