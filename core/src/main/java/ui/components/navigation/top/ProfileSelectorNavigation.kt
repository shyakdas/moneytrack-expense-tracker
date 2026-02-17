package ui.components.navigation.top

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.moneytrack.core.R
import ui.components.common.SelectorChip
import ui.theme.Violet100

@Composable
internal fun ProfileSelectorNavigation(
    config: TopNavigationConfig.ProfileWithSelector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        ProfileAvatar(
            painter = config.profileImage,
            onClick = { /* profile click */ }
        )

        Spacer(modifier = Modifier.weight(1f))

        SelectorChip(
            label = config.selectedMonth,
            onClick = config.onMonthClick,
            leadingIcon = ImageVector.vectorResource(id = R.drawable.arrow_down_2)
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = config.onActionClick) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.notifiaction),
                contentDescription = "Back",
                tint = Violet100
            )
        }
    }
}

@Composable
private fun ProfileAvatar(
    painter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .border(
                width = 2.dp,
                color = Violet100,
                shape = CircleShape
            )
            .padding(2.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = "Profile",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Preview(
    name = "Profile Selector Navigation",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun ProfileSelectorNavigationPreview() {
    MaterialTheme {
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
