// Copyright (c) 2026 shyakdas

package ui.components.navigation.topNav

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.moneytrack.designsystem.R
import ui.components.navigation.common.SelectorChip
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
internal fun ProfileSelectorNavigation(
    config: TopNavigationConfig.ProfileWithSelector,
    containerColor: Color = AppTheme.colors.surface,
) {
    val actionIconTint = if (config.actionIconTint == Color.Unspecified) {
        AppTheme.colors.onSurface
    } else {
        config.actionIconTint
    }

    Surface(
        color = containerColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.buttonLLargeHeight)
                .padding(horizontal = Dimens.spacing16),
            verticalAlignment = Alignment.CenterVertically
        ) {

            ProfileAvatar(
                painter = config.profileImage,
                avatarContent = config.profileAvatarContent,
                onClick = { /* profile click */ }
            )

            Spacer(modifier = Modifier.weight(1f))

            SelectorChip(
                label = config.selectedMonth,
                onClick = config.onMonthClick,
                selected = false,
                leadingIcon = ImageVector.vectorResource(id = R.drawable.arrow_down_2),
                highlighted = true,
            )
            if (config.selectedYear != null && config.onYearClick != null) {
                Spacer(modifier = Modifier.width(Dimens.spacing8))
                SelectorChip(
                    label = config.selectedYear,
                    onClick = config.onYearClick,
                    selected = false,
                    leadingIcon = ImageVector.vectorResource(id = R.drawable.arrow_down_2),
                    highlighted = true,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = config.onActionClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.notifiaction),
                    contentDescription = "Notification",
                    tint = actionIconTint
                )
            }
        }
    }
}


@Composable
private fun ProfileAvatar(
    painter: Painter,
    avatarContent: (@Composable () -> Unit)?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(Dimens.buttonSmallHeight)
            .border(
                width = Dimens.spacing2,
                color = AppTheme.colors.primary,
                shape = CircleShape
            )
            .padding(Dimens.spacing2)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (avatarContent != null) {
            avatarContent()
        } else {
            Image(
                painter = painter,
                contentDescription = "Profile",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Preview(name = "Profile Selector Navigation – Light & Dark")
@Composable
private fun ProfileSelectorNavigationPreview() {
    Column {
        MoneyTrackTheme(darkTheme = false) {
            Surface(color = AppTheme.colors.background) {
                ProfileSelectorNavigation(
                    config = TopNavigationConfig.ProfileWithSelector(
                        profileImage = ColorPainter(Color.Gray),
                        selectedMonth = "October",
                        onMonthClick = {},
                        onActionClick = {}
                    )
                )
            }
        }

       Spacer(
            modifier = Modifier.height(Dimens.spacing16)
        )

        MoneyTrackTheme(darkTheme = true) {
            Surface(color = AppTheme.colors.background) {
                ProfileSelectorNavigation(
                    config = TopNavigationConfig.ProfileWithSelector(
                        profileImage = ColorPainter(Color.Gray),
                        selectedMonth = "October",
                        onMonthClick = {},
                        onActionClick = {}
                    )
                )
            }
        }
    }
}
