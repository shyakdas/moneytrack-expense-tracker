package ui.components.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TopNavigation(
    config: TopNavigationConfig,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 2.dp
    ) {
        when (config) {

            is TopNavigationConfig.BackWithTitle -> {
                BackTitleNavigation(config)
            }

            is TopNavigationConfig.TitleOnly -> {
                CenterTitleNavigation(config)
            }

            is TopNavigationConfig.ProfileWithSelector -> {
                ProfileSelectorNavigation(config)
            }

            is TopNavigationConfig.DropdownWithFilter -> {
                DropdownFilterNavigation(config)
            }
        }
    }
}
