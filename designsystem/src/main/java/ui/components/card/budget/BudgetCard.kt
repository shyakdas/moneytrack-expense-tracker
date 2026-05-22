// Copyright (c) 2026 shyakdas

package ui.components.card.budget

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme
import ui.theme.MotionTokens

@Composable
fun BudgetCard(
    category: String,
    remainingAmount: String,
    spentText: String,
    progress: Float,
    status: BudgetStatus,
    modifier: Modifier = Modifier
) {
    val isExceeded = status == BudgetStatus.EXCEEDED

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = MotionTokens.standardTween())
            .background(
                color = AppTheme.colors.surface,
                shape = RoundedCornerShape(Dimens.radius12)
            )
            .padding(Dimens.spacing20),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing12)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BudgetCategoryPill(title = category)

            Spacer(modifier = Modifier.weight(1f))

            if (isExceeded) {
                BudgetWarningIcon()
            }
        }

        Text(
            text = "Remaining $remainingAmount",
            style = AppTheme.typography.titleLarge,
            color = AppTheme.colors.onSurface
        )

        BudgetProgressBar(
            progress = progress,
            isExceeded = isExceeded
        )

        Text(
            text = spentText,
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.onSurfaceVariant
        )

        if (isExceeded) {
            Text(
                text = "You’ve exceeded the limit!",
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.error
            )
        }
    }
}

@Composable
private fun BudgetCategoryPill(
    title: String
) {
    Row(
        modifier = Modifier
            .background(
                color = AppTheme.colors.surfaceVariant,
                shape = RoundedCornerShape(Dimens.radius12)
            )
            .padding(
                horizontal = Dimens.spacing16,
                vertical = Dimens.spacing10
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(Dimens.spacing12)
                .background(
                    color = AppTheme.colors.primary,
                    shape = CircleShape
                )
        )

        Spacer(modifier = Modifier.width(Dimens.spacing8))

        Text(
            text = title,
            style = AppTheme.typography.labelLarge,
            color = AppTheme.colors.onSurface
        )
    }
}

@Composable
private fun BudgetProgressBar(
    progress: Float,
    isExceeded: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = MotionTokens.emphasizedTween(),
        label = "BudgetProgress",
    )
    val progressColor =
        if (isExceeded) AppTheme.colors.error
        else AppTheme.colors.success

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.progressSmallHeight)
            .background(
                color = AppTheme.colors.surfaceVariant,
                shape = RoundedCornerShape(Dimens.radius50)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .fillMaxHeight()
                .background(
                    color = progressColor,
                    shape = RoundedCornerShape(Dimens.radius50)
                )
        )
    }
}

@Composable
private fun BudgetWarningIcon() {
    Box(
        modifier = Modifier
            .size(Dimens.warningIconSize)
            .background(
                color = AppTheme.colors.error,
                shape = RoundedCornerShape(Dimens.radius12)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "!",
            style = AppTheme.typography.titleLarge,
            color = AppTheme.colors.onPrimary
        )
    }
}


@Preview(name = "Budget Card – Light & Dark")
@Composable
private fun BudgetCardPreview() {
    Column {
        MoneyTrackTheme(darkTheme = false) {
            BudgetPreviewContent()
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        MoneyTrackTheme(darkTheme = true) {
            BudgetPreviewContent()
        }
    }
}

@Composable
private fun BudgetPreviewContent() {
    Column(
        modifier = Modifier
            .background(AppTheme.colors.background)
            .padding(Dimens.spacing16),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing16)
    ) {

        BudgetCard(
            category = "Medical",
            remainingAmount = "$0",
            spentText = "$1200 of $1000",
            progress = 1f,
            status = BudgetStatus.EXCEEDED
        )

        BudgetCard(
            category = "Medical",
            remainingAmount = "$200",
            spentText = "$600 of $1000",
            progress = 0.6f,
            status = BudgetStatus.NORMAL
        )
    }
}
