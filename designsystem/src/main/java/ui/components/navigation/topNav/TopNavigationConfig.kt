// Copyright (c) 2026 shyakdas

package ui.components.navigation.topNav

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

sealed class TopNavigationConfig {

    data class BackWithTitle(
        val title: String,
        val showMore: Boolean = false,
        val onBackClick: () -> Unit,
        val onMoreClick: (() -> Unit)? = null
    ) : TopNavigationConfig()

    data class TitleOnly(
        val title: String
    ) : TopNavigationConfig()

    data class ProfileWithSelector(
        val profileImage: Painter,
        val profileAvatarContent: (@Composable () -> Unit)? = null,
        val selectedMonth: String,
        val onMonthClick: () -> Unit,
        val onActionClick: () -> Unit,
        val actionIconTint: Color = Color.Unspecified,
    ) : TopNavigationConfig()

    data class DropdownWithFilter(
        val label: String,
        val showBadge: Boolean = false,
        val badgeCount: Int = 0,
        val onDropdownClick: () -> Unit,
        val onFilterClick: () -> Unit
    ) : TopNavigationConfig()
}
