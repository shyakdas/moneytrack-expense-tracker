// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneytrack.R
import com.moneytrack.designsystem.R as DsR
import ui.components.navigation.bottomNav.BottomNavItem
import ui.components.navigation.bottomNav.PrimaryBottomNavigation
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

private const val ROUTE_HOME = "home"
private const val ROUTE_TRANSACTION = "transaction"
private const val ROUTE_BUDGET = "budget"
private const val ROUTE_PROFILE = "profile"

data class ProfileUiState(
    val name: String,
)

private data class ProfileActionItem(
    val titleRes: Int,
    val iconRes: Int,
    val iconBackground: Color,
    val iconTint: Color,
)

@Composable
fun ProfileRoute(
    onHomeClick: () -> Unit,
    onTransactionClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
) {
    ProfileScreen(
        uiState = ProfileUiState(
            name = stringResource(id = R.string.profile_name_default),
        ),
        onBottomRouteClick = { route ->
            when (route) {
                ROUTE_HOME -> onHomeClick()
                ROUTE_TRANSACTION -> onTransactionClick()
                else -> Unit
            }
        },
        onAddExpenseClick = onAddExpenseClick,
    )
}

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onBottomRouteClick: (String) -> Unit,
    onAddExpenseClick: () -> Unit,
) {
    val bottomItems = remember {
        listOf(
            BottomNavItem(ROUTE_HOME, DsR.drawable.home, "Home"),
            BottomNavItem(ROUTE_TRANSACTION, DsR.drawable.transaction, "Transaction"),
            BottomNavItem(ROUTE_BUDGET, DsR.drawable.line_chart_2, "Budget"),
            BottomNavItem(ROUTE_PROFILE, DsR.drawable.user, "Profile"),
        )
    }
    val actionItems = listOf(
        ProfileActionItem(
            titleRes = R.string.profile_action_settings,
            iconRes = DsR.drawable.settings,
            iconBackground = AppTheme.colors.primary.copy(alpha = 0.14f),
            iconTint = AppTheme.colors.primary,
        ),
        ProfileActionItem(
            titleRes = R.string.profile_action_export,
            iconRes = DsR.drawable.variant_export_data,
            iconBackground = AppTheme.colors.primary.copy(alpha = 0.14f),
            iconTint = AppTheme.colors.primary,
        ),
        ProfileActionItem(
            titleRes = R.string.profile_action_logout,
            iconRes = DsR.drawable.logout,
            iconBackground = AppTheme.colors.error.copy(alpha = 0.14f),
            iconTint = AppTheme.colors.error,
        ),
    )

    Scaffold(
        containerColor = AppTheme.colors.background,
        bottomBar = {
            Box(modifier = Modifier.navigationBarsPadding()) {
                PrimaryBottomNavigation(
                    items = bottomItems,
                    selectedRoute = ROUTE_PROFILE,
                    onItemClick = { item -> onBottomRouteClick(item.route) },
                    onFabClick = onAddExpenseClick,
                )
            }
        },
    ) { innerPadding ->
        ProfileContent(
            uiState = uiState,
            innerPadding = innerPadding,
            actionItems = actionItems,
        )
    }
}

@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    innerPadding: PaddingValues,
    actionItems: List<ProfileActionItem>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .padding(innerPadding)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.spacing16),
    ) {
        Spacer(modifier = Modifier.height(Dimens.spacing8))
        ProfileHeader(
            name = uiState.name,
        )
        Spacer(modifier = Modifier.height(Dimens.spacing32))
        ProfileActionsCard(actionItems = actionItems)
        Spacer(modifier = Modifier.height(Dimens.spacing24))
    }
}

@Composable
private fun ProfileHeader(
    name: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.profileAvatarSize)
                .border(
                    width = Dimens.borderThick,
                    color = AppTheme.colors.primary,
                    shape = CircleShape,
                )
                .padding(Dimens.spacing4),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = AppTheme.colors.surfaceVariant,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(id = DsR.drawable.user),
                    contentDescription = null,
                    tint = AppTheme.colors.primary,
                    modifier = Modifier.size(Dimens.profileAvatarIconSize),
                )
            }
        }
        Spacer(modifier = Modifier.width(Dimens.spacing16))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = name,
                style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.onBackground,
            )
        }
        IconButton(
            onClick = {},
            modifier = Modifier
                .size(Dimens.iconContainerSize)
                .background(
                    color = AppTheme.colors.surfaceVariant,
                    shape = RoundedCornerShape(Dimens.radius16),
                ),
        ) {
            Icon(
                painter = painterResource(id = DsR.drawable.edit),
                contentDescription = stringResource(id = R.string.profile_edit_content_desc),
                tint = AppTheme.colors.onBackground,
            )
        }
    }
}

@Composable
private fun ProfileActionsCard(
    actionItems: List<ProfileActionItem>,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radius24),
        color = AppTheme.colors.surface,
    ) {
        Column {
            actionItems.forEachIndexed { index, item ->
                ProfileActionRow(item = item)
                if (index != actionItems.lastIndex) {
                    HorizontalDivider(
                        color = AppTheme.colors.outline.copy(alpha = 0.22f),
                        thickness = 1.dp,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileActionRow(
    item: ProfileActionItem,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = Dimens.spacing20, vertical = Dimens.spacing20),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.chipHeight)
                .background(
                    color = item.iconBackground,
                    shape = RoundedCornerShape(Dimens.radius20),
                ),
                contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = item.iconRes),
                contentDescription = null,
                tint = item.iconTint,
                modifier = Modifier.size(Dimens.icon24),
            )
        }
        Spacer(modifier = Modifier.width(Dimens.spacing16))
        Text(
            text = stringResource(id = item.titleRes),
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colors.onBackground,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ProfileScreenPreview() {
    MoneyTrackTheme(darkTheme = false) {
        ProfileScreen(
            uiState = ProfileUiState(
                name = "Budget Maverick",
            ),
            onBottomRouteClick = {},
            onAddExpenseClick = {},
        )
    }
}
