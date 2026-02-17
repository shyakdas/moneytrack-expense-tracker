package ui.components.navigation.bottomNav

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneytrack.core.R
import ui.theme.NeutralC6
import ui.theme.Violet100

@Composable
internal fun BottomBarItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    val color by animateColorAsState(
        targetValue = if (isSelected) Violet100 else NeutralC6,
        label = "BottomNavColorAnimation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        IconButton(
            onClick = onClick,
            modifier = Modifier.testTag("NavItem_${item.route}")
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = item.icon),
                contentDescription = item.label,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
        }

        Text(
            text = item.label,
            style = MaterialTheme.typography.labelLarge,
            color = color
        )
    }
}


@Preview(
    name = "BottomBarItem – States",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun BottomBarItemPreview() {
    MaterialTheme {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarItem(
                item = BottomNavItem(
                    route = "home",
                    icon = R.drawable.home,
                    label = "Home"
                ),
                isSelected = true,
                onClick = {}
            )

            Spacer(modifier = Modifier.size(24.dp))

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
