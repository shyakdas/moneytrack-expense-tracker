// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.moneytrack.R
import com.moneytrack.common.ui.LottieAnimationView
import com.moneytrack.designsystem.R as DsR
import ui.theme.AppTheme
import ui.theme.Dimens

@Composable
internal fun ProfileAvatar() {
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
                .clip(CircleShape)
                .background(
                    color = AppTheme.colors.surfaceVariant,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            LottieAnimationView(
                rawRes = R.raw.lottie_profile_people,
                modifier = Modifier.fillMaxSize(),
                speed = 1.2f,
            )
        }
    }
}

@Composable
internal fun ProfileEditButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
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

@Composable
internal fun ClearDataActionButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.buttonLargeHeight)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Dimens.spacing16),
        color = backgroundColor,
        contentColor = AppTheme.colors.onPrimary,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.onPrimary,
            )
        }
    }
}
