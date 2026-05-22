// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneytrack.R
import com.moneytrack.designsystem.R as DsR
import com.moneytrack.reminder.notification.ExpenseReminderScheduler
import ui.components.navigation.topNav.TopNavigation
import ui.components.navigation.topNav.TopNavigationConfig
import ui.components.surface.MoneyTrackScreenBackground
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

private data class NotificationOptionUiModel(
    val notificationsPerDay: Int,
    val title: String,
)

@Composable
fun NotificationRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: NotificationViewModel = hiltViewModel()
    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                NotificationEvent.Completed -> {
                    ExpenseReminderScheduler.scheduleAll(context)
                    onBackClick()
                }
            }
        }
    }

    NotificationScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onNotificationCountSelected = viewModel::onNotificationCountSelected,
    )
}

@Composable
fun NotificationScreen(
    uiState: NotificationUiState,
    onBackClick: () -> Unit,
    onNotificationCountSelected: (Int) -> Unit,
) {
    val options = notificationOptions()

    Scaffold(
        containerColor = AppTheme.colors.background,
    ) { innerPadding ->
        MoneyTrackScreenBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                TopNavigation(
                    config = TopNavigationConfig.BackWithTitle(
                        title = stringResource(id = R.string.notification_title),
                        onBackClick = onBackClick,
                    ),
                    containerColor = Color.Transparent,
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.spacing16)
                        .padding(top = Dimens.spacing12),
                ) {
                    items(options, key = { it.notificationsPerDay }) { notificationOption ->
                        NotificationRow(
                            notificationOption = notificationOption,
                            isSelected =
                                notificationOption.notificationsPerDay ==
                                    uiState.selectedNotificationsPerDay,
                            onClick = { onNotificationCountSelected(notificationOption.notificationsPerDay) },
                        )
                    }
                }

                NotificationFooterNote()
            }
        }
    }
}

@Composable
private fun notificationOptions(): List<NotificationOptionUiModel> = listOf(
    NotificationOptionUiModel(
        notificationsPerDay = 1,
        title = stringResource(id = R.string.settings_notification_one),
    ),
    NotificationOptionUiModel(
        notificationsPerDay = 3,
        title = stringResource(id = R.string.settings_notification_three),
    ),
    NotificationOptionUiModel(
        notificationsPerDay = 5,
        title = stringResource(id = R.string.settings_notification_five),
    ),
)

@Composable
private fun NotificationFooterNote() {
    Text(
        text = stringResource(id = R.string.settings_notification_note),
        style = AppTheme.typography.bodySmall,
        color = AppTheme.colors.onBackground.copy(alpha = 0.6f),
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(
                horizontal = Dimens.spacing16,
                vertical = Dimens.spacing16,
            ),
    )
}

@Composable
private fun NotificationRow(
    notificationOption: NotificationOptionUiModel,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.spacing20, vertical = Dimens.spacing20),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = notificationOption.title,
            style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = AppTheme.colors.onBackground,
        )

        if (isSelected) {
            Icon(
                imageVector = ImageVector.vectorResource(id = DsR.drawable.success),
                contentDescription = null,
                tint = AppTheme.colors.primary,
            )
        }
    }
    HorizontalDivider(
        color = AppTheme.colors.outline.copy(alpha = 0.28f),
        thickness = 1.dp,
    )
}

@Suppress("UnusedPrivateMember")
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun NotificationScreenLightPreview() {
    MoneyTrackTheme(darkTheme = false) {
        NotificationScreen(
            uiState = NotificationUiState(selectedNotificationsPerDay = 3),
            onBackClick = {},
            onNotificationCountSelected = {},
        )
    }
}

@Suppress("UnusedPrivateMember")
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun NotificationScreenDarkPreview() {
    MoneyTrackTheme(darkTheme = true) {
        NotificationScreen(
            uiState = NotificationUiState(selectedNotificationsPerDay = 5),
            onBackClick = {},
            onNotificationCountSelected = {},
        )
    }
}
