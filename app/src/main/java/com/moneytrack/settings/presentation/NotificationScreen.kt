// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import ui.components.surface.MoneyTrackScreenBackground
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

private data class NotificationOptionUiModel(
    val notificationsPerDay: Int,
    val title: String,
    val subtitle: String,
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
                    .padding(innerPadding)
                    .padding(horizontal = Dimens.spacing16)
                    .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.padding(top = Dimens.spacing8))
                NotificationHeader(onBackClick = onBackClick)
                Spacer(modifier = Modifier.padding(top = 22.dp))
                options.forEach { option ->
                    NotificationOptionCard(
                        notificationOption = option,
                        isSelected = option.notificationsPerDay == uiState.selectedNotificationsPerDay,
                        onClick = { onNotificationCountSelected(option.notificationsPerDay) },
                    )
                    Spacer(modifier = Modifier.padding(top = 14.dp))
                }
                NotificationInfoCard()
                Spacer(modifier = Modifier.padding(top = Dimens.spacing20))
            }
        }
    }
}

@Composable
private fun NotificationHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier
                .size(44.dp)
                .clickable(onClick = onBackClick),
            shape = CircleShape,
            color = AppTheme.colors.surfaceVariant.copy(alpha = 0.55f),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = DsR.drawable.arrow_left),
                    contentDescription = null,
                    tint = AppTheme.colors.onSurface,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
        Spacer(modifier = Modifier.width(Dimens.spacing12))
        Text(
            text = stringResource(id = R.string.notification_title),
            style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = AppTheme.colors.onBackground,
        )
    }
}

@Composable
private fun notificationOptions(): List<NotificationOptionUiModel> = listOf(
    NotificationOptionUiModel(
        notificationsPerDay = 1,
        title = stringResource(id = R.string.settings_notification_one),
        subtitle = "Gentle reminder",
    ),
    NotificationOptionUiModel(
        notificationsPerDay = 3,
        title = stringResource(id = R.string.settings_notification_three),
        subtitle = "Balanced reminder",
    ),
    NotificationOptionUiModel(
        notificationsPerDay = 5,
        title = stringResource(id = R.string.settings_notification_five),
        subtitle = "Frequent reminder",
    ),
)

@Composable
@Suppress("LongMethod")
private fun NotificationOptionCard(
    notificationOption: NotificationOptionUiModel,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isSelected) {
                    AppTheme.colors.primary.copy(alpha = 0.5f)
                } else {
                    AppTheme.colors.outline.copy(alpha = 0.28f)
                },
                shape = RoundedCornerShape(Dimens.radius24),
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Dimens.radius24),
        color = AppTheme.colors.surface.copy(alpha = 0.88f),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = AppTheme.colors.primary.copy(alpha = 0.14f),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = DsR.drawable.notifiaction),
                    contentDescription = null,
                    tint = AppTheme.colors.primary,
                    modifier = Modifier.size(14.dp),
                )
            }
            Spacer(modifier = Modifier.width(Dimens.spacing12))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notificationOption.title,
                    style = AppTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = AppTheme.colors.onBackground,
                )
                Text(
                    text = notificationOption.subtitle,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant,
                )
            }
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(
                            color = AppTheme.colors.primary.copy(alpha = 0.24f),
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = DsR.drawable.success),
                        contentDescription = null,
                        tint = AppTheme.colors.primary,
                        modifier = Modifier.size(14.dp),
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .border(
                            width = 1.dp,
                            color = AppTheme.colors.outline.copy(alpha = 0.6f),
                            shape = CircleShape,
                        ),
                )
            }
        }
    }
}

@Composable
private fun NotificationInfoCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = AppTheme.colors.outline.copy(alpha = 0.22f),
                shape = RoundedCornerShape(Dimens.radius24),
            ),
        shape = RoundedCornerShape(Dimens.radius24),
        color = AppTheme.colors.surface.copy(alpha = 0.88f),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = AppTheme.colors.primary.copy(alpha = 0.14f),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = DsR.drawable.warning),
                    contentDescription = null,
                    tint = AppTheme.colors.primary,
                    modifier = Modifier.size(14.dp),
                )
            }
            Spacer(modifier = Modifier.width(Dimens.spacing12))
            Text(
                text = "Helps you remember to log expenses so nothing gets missed.",
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant,
            )
        }
    }
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
