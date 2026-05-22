// Copyright (c) 2026 shyakdas

package ui.components.card.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.moneytrack.designsystem.R
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
fun TransactionCard(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    amount: String,
    time: String,
    type: TransactionType,
    modifier: Modifier = Modifier
) {
    val amountColor = when (type) {
        TransactionType.EXPENSE -> AppTheme.colors.error
        TransactionType.INCOME -> AppTheme.colors.success
    }
    val iconContainerColor = when (type) {
        TransactionType.EXPENSE -> AppTheme.colors.error.copy(alpha = 0.12f)
        TransactionType.INCOME -> AppTheme.colors.successContainer
    }
    val iconTint = when (type) {
        TransactionType.EXPENSE -> AppTheme.colors.error
        TransactionType.INCOME -> AppTheme.colors.success
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radius16),
        color = AppTheme.colors.surface,
        tonalElevation = Dimens.elevation2,
    ) {
        Row(
            modifier = Modifier.padding(Dimens.spacing16),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(Dimens.iconContainerSize)
                    .background(
                        color = iconContainerColor,
                        shape = RoundedCornerShape(Dimens.radius16)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(Dimens.icon24)
                )
            }

            Spacer(modifier = Modifier.width(Dimens.spacing16))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacing4)
            ) {
                Text(
                    text = title,
                    style = AppTheme.typography.titleMedium,
                    color = AppTheme.colors.onSurface
                )

                subtitle?.takeIf { it.isNotBlank() }?.let { subtitleText ->
                    Text(
                        text = subtitleText,
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(Dimens.spacing12))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(Dimens.spacing4)
            ) {
                Text(
                    text = amount,
                    style = AppTheme.typography.titleMedium,
                    color = amountColor
                )

                Text(
                    text = time,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(name = "Transaction Card – Light & Dark")
@Composable
private fun TransactionCardPreview() {
    Column {
        MoneyTrackTheme(darkTheme = false) {
            TransactionPreviewContent()
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        MoneyTrackTheme(darkTheme = true) {
            TransactionPreviewContent()
        }
    }
}

@Composable
private fun TransactionPreviewContent() {
    Column(
        modifier = Modifier
            .background(AppTheme.colors.background)
            .padding(Dimens.spacing16),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing12)
    ) {
        TransactionCard(
            icon = ImageVector.vectorResource(id = R.drawable.shopping_bag),
            title = "Shopping",
            subtitle = "Buy an Avocado...",
            amount = "- Rp 229.000",
            time = "03:30 PM",
            type = TransactionType.EXPENSE
        )

        TransactionCard(
            icon = ImageVector.vectorResource(id = R.drawable.shopping_bag),
            title = "Salary",
            subtitle = "Monthly salary",
            amount = "+ Rp 3.129.000",
            time = "04:30 PM",
            type = TransactionType.INCOME
        )
    }
}
