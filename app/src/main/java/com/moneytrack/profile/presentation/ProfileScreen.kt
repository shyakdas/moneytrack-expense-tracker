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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moneytrack.R
import com.moneytrack.designsystem.R as DsR
import ui.components.navigation.bottomNav.BottomNavItem
import ui.components.navigation.bottomNav.PrimaryBottomNavigation
import ui.components.navigation.button.LargeButton
import ui.components.form.input.InputField
import ui.components.surface.MoneyTrackCard
import ui.components.surface.MoneyTrackBottomSheet
import ui.components.surface.MoneyTrackScreenBackground
import ui.theme.AppTheme
import ui.theme.Dimens

private const val ROUTE_HOME = "home"
private const val ROUTE_TRANSACTION = "transaction"
private const val ROUTE_BUDGET = "budget"
private const val ROUTE_PROFILE = "profile"

private enum class ProfileActionType {
    SETTINGS,
    EXPORT_DATA,
    CLEAR_DATA,
}

private data class ProfileActionItem(
    val type: ProfileActionType,
    val titleRes: Int,
    val iconRes: Int,
    val iconBackground: Color,
    val iconTint: Color,
)

data class ProfileNavigationCallbacks(
    val onBottomRouteClick: (String) -> Unit,
    val onAddExpenseClick: () -> Unit,
)

data class ProfileActionCallbacks(
    val onEditClick: () -> Unit,
    val onSettingsClick: () -> Unit,
    val onDismissEditSheet: () -> Unit,
    val onEditNameChanged: (String) -> Unit,
    val onSaveName: () -> Unit,
    val onClearDataClick: () -> Unit,
    val onDismissClearDataSheet: () -> Unit,
    val onConfirmClearData: () -> Unit,
)

@Composable
fun ProfileRoute(
    onHomeClick: () -> Unit,
    onTransactionClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    androidx.compose.runtime.LaunchedEffect(uiState.clearDataCompleted) {
        if (uiState.clearDataCompleted) {
            viewModel.onClearDataHandled()
        }
    }

    ProfileScreen(
        uiState = uiState,
        navigationCallbacks = ProfileNavigationCallbacks(
            onBottomRouteClick = { route ->
                when (route) {
                    ROUTE_HOME -> onHomeClick()
                    ROUTE_TRANSACTION -> onTransactionClick()
                    else -> Unit
                }
            },
            onAddExpenseClick = onAddExpenseClick,
        ),
        actionCallbacks = ProfileActionCallbacks(
            onEditClick = viewModel::showEditSheet,
            onSettingsClick = onSettingsClick,
            onDismissEditSheet = viewModel::hideEditSheet,
            onEditNameChanged = viewModel::onNameChanged,
            onSaveName = viewModel::saveName,
            onClearDataClick = viewModel::showClearDataSheet,
            onDismissClearDataSheet = viewModel::hideClearDataSheet,
            onConfirmClearData = viewModel::clearLocalData,
        ),
    )
}

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    navigationCallbacks: ProfileNavigationCallbacks,
    actionCallbacks: ProfileActionCallbacks,
) {
    val bottomItems = remember {
        listOf(
            BottomNavItem(ROUTE_HOME, DsR.drawable.home, "Home"),
            BottomNavItem(ROUTE_TRANSACTION, DsR.drawable.transaction, "Transaction"),
            BottomNavItem(ROUTE_BUDGET, DsR.drawable.line_chart_2, "Budget"),
            BottomNavItem(ROUTE_PROFILE, DsR.drawable.user, "Profile"),
        )
    }

    Scaffold(
        containerColor = AppTheme.colors.background,
        bottomBar = {
            Box(modifier = Modifier.navigationBarsPadding()) {
                PrimaryBottomNavigation(
                    items = bottomItems,
                    selectedRoute = ROUTE_PROFILE,
                    onItemClick = { item -> navigationCallbacks.onBottomRouteClick(item.route) },
                    onFabClick = navigationCallbacks.onAddExpenseClick,
                )
            }
        },
    ) { innerPadding ->
        ProfileContent(
            uiState = uiState,
            innerPadding = innerPadding,
            actionItems = profileActionItems(),
            onEditClick = actionCallbacks.onEditClick,
            onActionClick = { actionType ->
                when (actionType) {
                    ProfileActionType.CLEAR_DATA -> actionCallbacks.onClearDataClick()
                    ProfileActionType.SETTINGS -> actionCallbacks.onSettingsClick()
                    ProfileActionType.EXPORT_DATA -> Unit
                }
            },
        )
    }

    if (uiState.isEditSheetVisible) {
        EditProfileNameBottomSheet(
            value = uiState.editName,
            onValueChange = actionCallbacks.onEditNameChanged,
            onDismiss = actionCallbacks.onDismissEditSheet,
            onSave = actionCallbacks.onSaveName,
            isSaveEnabled = uiState.isSaveEnabled,
        )
    }

    if (uiState.isClearDataSheetVisible) {
        ClearDataBottomSheet(
            onDismiss = actionCallbacks.onDismissClearDataSheet,
            onConfirm = actionCallbacks.onConfirmClearData,
        )
    }
}

@Composable
private fun profileActionItems(): List<ProfileActionItem> = listOf(
    ProfileActionItem(
        type = ProfileActionType.SETTINGS,
        titleRes = R.string.profile_action_settings,
        iconRes = DsR.drawable.settings,
        iconBackground = AppTheme.colors.primary.copy(alpha = 0.14f),
        iconTint = AppTheme.colors.primary,
    ),
    ProfileActionItem(
        type = ProfileActionType.EXPORT_DATA,
        titleRes = R.string.profile_action_export,
        iconRes = DsR.drawable.variant_export_data,
        iconBackground = AppTheme.colors.primary.copy(alpha = 0.14f),
        iconTint = AppTheme.colors.primary,
    ),
    ProfileActionItem(
        type = ProfileActionType.CLEAR_DATA,
        titleRes = R.string.profile_action_logout,
        iconRes = DsR.drawable.logout,
        iconBackground = AppTheme.colors.error.copy(alpha = 0.14f),
        iconTint = AppTheme.colors.error,
    ),
)

@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    innerPadding: PaddingValues,
    actionItems: List<ProfileActionItem>,
    onEditClick: () -> Unit,
    onActionClick: (ProfileActionType) -> Unit,
) {
    MoneyTrackScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.spacing16),
        ) {
            Spacer(modifier = Modifier.height(Dimens.spacing8))
            ProfileHeader(
                name = uiState.name,
                onEditClick = onEditClick,
            )
            Spacer(modifier = Modifier.height(Dimens.spacing24))
            ProfileActionsCard(
                actionItems = actionItems,
                onActionClick = onActionClick,
            )
            Spacer(modifier = Modifier.height(Dimens.spacing24))
        }
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    onEditClick: () -> Unit,
) {
    MoneyTrackCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProfileAvatar()
            Spacer(modifier = Modifier.width(Dimens.spacing16))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = name,
                    style = AppTheme.typography.headlineSmall,
                    color = AppTheme.colors.onBackground,
                )
                Text(
                    text = "Personal finance workspace",
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant,
                )
            }
            ProfileEditButton(onClick = onEditClick)
        }
    }
}

@Composable
private fun EditProfileNameBottomSheet(
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    isSaveEnabled: Boolean,
) {
    MoneyTrackBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacing24)
                .padding(bottom = Dimens.spacing24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing16),
        ) {
            Text(
                text = stringResource(id = R.string.profile_edit_sheet_title),
                style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.onBackground,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )

            Text(
                text = stringResource(id = R.string.profile_edit_sheet_desc),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )

            InputField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(id = R.string.profile_edit_name_placeholder),
            )

            LargeButton(
                text = stringResource(id = R.string.profile_edit_save),
                onClick = onSave,
                enabled = isSaveEnabled,
            )
        }
    }
}

@Composable
private fun ProfileActionsCard(
    actionItems: List<ProfileActionItem>,
    onActionClick: (ProfileActionType) -> Unit,
) {
    MoneyTrackCard(
        contentPadding = PaddingValues(Dimens.spacing4),
    ) {
        Column {
            actionItems.forEachIndexed { index, item ->
                ProfileActionRow(
                    item = item,
                    onClick = { onActionClick(item.type) },
                )
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
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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

@Composable
private fun ClearDataBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    MoneyTrackBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacing24)
                .padding(bottom = Dimens.spacing24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing16),
        ) {
            Text(
                text = stringResource(id = R.string.profile_clear_data_title),
                style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.onBackground,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )

            Text(
                text = stringResource(id = R.string.profile_clear_data_desc),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimens.radius16),
                color = AppTheme.colors.surfaceVariant,
            ) {
                Text(
                    text = stringResource(id = R.string.profile_clear_data_export_hint),
                    modifier = Modifier.padding(Dimens.spacing16),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurface,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }

            ClearDataActionButton(
                text = stringResource(id = R.string.profile_clear_data_no),
                onClick = onDismiss,
                    backgroundColor = AppTheme.colors.success,
            )

            ClearDataActionButton(
                text = stringResource(id = R.string.profile_clear_data_yes),
                onClick = onConfirm,
                    backgroundColor = AppTheme.colors.error,
            )
        }
    }
}
