package ui.components.navigation

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
        val selectedMonth: String,
        val onMonthClick: () -> Unit,
        val onActionClick: () -> Unit
    ) : TopNavigationConfig()

    data class DropdownWithFilter(
        val label: String,
        val showBadge: Boolean = false,
        val badgeCount: Int = 0,
        val onDropdownClick: () -> Unit,
        val onFilterClick: () -> Unit
    ) : TopNavigationConfig()
}
