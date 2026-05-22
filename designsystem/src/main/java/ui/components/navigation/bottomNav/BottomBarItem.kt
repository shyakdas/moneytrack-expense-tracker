// Copyright (c) 2026 shyakdas

package ui.components.navigation.bottomNav

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneytrack.designsystem.R
import ui.motion.pressScale
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme
import ui.theme.MotionTokens

@Composable
internal fun BottomBarItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val color by animateColorAsState(
        targetValue =
            if (isSelected) AppTheme.colors.onPrimary
            else AppTheme.colors.onSurfaceVariant,
        label = "BottomNavColorAnimation"
    )
    val selectedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = MotionTokens.pressSpring(),
        label = "BottomNavSelectedScale",
    )
    val iconOffset by animateDpAsState(
        targetValue = 0.dp,
        animationSpec = MotionTokens.pressSpring(),
        label = "BottomNavIconOffset",
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .pressScale(
                interactionSource = interactionSource,
                pressedScale = 0.92f,
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .testTag("NavItem_${item.route}"),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.buttonLargeHeight)
                .graphicsLayer {
                    scaleX = selectedScale
                    scaleY = selectedScale
                }
                .height(Dimens.buttonLargeHeight),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = item.icon),
                contentDescription = item.label,
                tint = color,
                modifier = Modifier
                    .size(Dimens.icon28)
                    .graphicsLayer {
                        translationY = iconOffset.toPx()
                    }
            )
        }
    }
}


@Preview(name = "BottomBarItem – Light & Dark")
@Composable
private fun BottomBarItemPreview() {
    Column {
        MoneyTrackTheme(darkTheme = false) {
            BottomBarItem(
                item = BottomNavItem(
                    route = "home",
                    icon = R.drawable.home,
                    label = "Home"
                ),
                isSelected = true,
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.size(Dimens.spacing16))

        MoneyTrackTheme(darkTheme = true) {
            BottomBarItem(
                item = BottomNavItem(
                    route = "profile",
                    icon = R.drawable.user,
                    label = "Profile"
                ),
                isSelected = false,
                onClick = {}
            )
        }
    }
}
