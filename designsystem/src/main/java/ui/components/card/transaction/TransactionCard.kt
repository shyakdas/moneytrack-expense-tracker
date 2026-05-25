// Copyright (c) 2026 shyakdas

package ui.components.card.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneytrack.designsystem.R
import java.util.Locale
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
fun TransactionCard(
    icon: ImageVector,
    category: String? = null,
    title: String,
    subtitle: String?,
    amount: String,
    date: String? = null,
    time: String,
    type: TransactionType,
    modifier: Modifier = Modifier
) {
    val amountColor = when (type) {
        TransactionType.EXPENSE -> AppTheme.colors.error
        TransactionType.INCOME -> AppTheme.colors.success
    }
    val typeChipContainerColor = when (type) {
        TransactionType.EXPENSE -> AppTheme.colors.error.copy(alpha = 0.12f)
        TransactionType.INCOME -> AppTheme.colors.successContainer
    }
    val typeChipTextColor = when (type) {
        TransactionType.EXPENSE -> AppTheme.colors.error
        TransactionType.INCOME -> AppTheme.colors.success
    }
    val accent = categoryAccent(category = category, fallbackTitle = title, type = type)
    val iconContainerColor = accent.containerColor
    val iconTint = accent.tintColor

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = Dimens.spacing1,
                color = AppTheme.colors.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(Dimens.radius16),
            ),
        shape = RoundedCornerShape(Dimens.radius16),
        color = AppTheme.colors.surface,
        tonalElevation = Dimens.elevation0,
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacing12),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing8),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(Dimens.spacing48)
                        .background(
                            color = iconContainerColor,
                            shape = RoundedCornerShape(Dimens.radius16),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(28.dp),
                    )
                }
                Spacer(modifier = Modifier.width(Dimens.spacing12))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacing4),
                ) {
                    Text(
                        text = title,
                        style = AppTheme.typography.titleSmall,
                        color = AppTheme.colors.onSurface,
                    )
                    subtitle?.takeIf { it.isNotBlank() }?.let { subtitleText ->
                        Text(
                            text = subtitleText,
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colors.onSurfaceVariant,
                            maxLines = 1,
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacing6),
                ) {
                    Text(
                        text = amount,
                        style = AppTheme.typography.titleMedium,
                        color = amountColor,
                    )
                    Surface(
                        shape = RoundedCornerShape(Dimens.radius8),
                        color = typeChipContainerColor,
                    ) {
                        Text(
                            text = if (type == TransactionType.EXPENSE) "Expense" else "Income",
                            modifier = Modifier.padding(horizontal = Dimens.spacing8, vertical = Dimens.spacing2),
                            style = AppTheme.typography.labelMedium,
                            color = typeChipTextColor,
                        )
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8),
            ) {
                Text(
                    text = date.orEmpty(),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant,
                )
                Box(
                    modifier = Modifier
                        .size(Dimens.spacing4)
                        .background(
                            color = AppTheme.colors.onSurfaceVariant.copy(alpha = 0.6f),
                            shape = CircleShape,
                        ),
                )
                Text(
                    text = time,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun categoryAccent(
    category: String?,
    fallbackTitle: String,
    type: TransactionType,
): CategoryAccent {
    if (type == TransactionType.INCOME) {
        return CategoryAccent(
            containerColor = AppTheme.colors.successContainer,
            tintColor = AppTheme.colors.success,
        )
    }
    val normalized = (category ?: fallbackTitle).lowercase(Locale.getDefault())
    return when (normalized) {
        "food", "restaurant" -> CategoryAccent(
            containerColor = Color(0x1AFD3C4A),
            tintColor = Color(0xFFFD3C4A),
        )
        "transport", "travel", "car", "fuel" -> CategoryAccent(
            containerColor = Color(0x1A0077FF),
            tintColor = Color(0xFF0077FF),
        )
        "shopping" -> CategoryAccent(
            containerColor = Color(0x1AFCAC12),
            tintColor = Color(0xFFFCAC12),
        )
        "bills", "bill", "utilities" -> CategoryAccent(
            containerColor = Color(0x1AFD5662),
            tintColor = Color(0xFFFD5662),
        )
        "rent", "home" -> CategoryAccent(
            containerColor = Color(0x1A8F57FF),
            tintColor = Color(0xFF8F57FF),
        )
        "health", "medical", "pharmacy" -> CategoryAccent(
            containerColor = Color(0x1A00A86B),
            tintColor = Color(0xFF00A86B),
        )
        "entertainment" -> CategoryAccent(
            containerColor = Color(0x1A57A5FF),
            tintColor = Color(0xFF57A5FF),
        )
        "education" -> CategoryAccent(
            containerColor = Color(0x1AFCBB3C),
            tintColor = Color(0xFFFCBB3C),
        )
        "subscription" -> CategoryAccent(
            containerColor = Color(0x1A2AB784),
            tintColor = Color(0xFF2AB784),
        )
        "others", "other" -> CategoryAccent(
            containerColor = Color(0x1A91919F),
            tintColor = Color(0xFF91919F),
        )
        else -> fallbackCategoryAccent(normalized)
    }
}

private data class CategoryAccent(
    val containerColor: Color,
    val tintColor: Color,
)

@Composable
private fun fallbackCategoryAccent(key: String): CategoryAccent {
    val palette = listOf(
        CategoryAccent(AppTheme.colors.primaryContainer, AppTheme.colors.primary),
        CategoryAccent(AppTheme.colors.successContainer, AppTheme.colors.success),
        CategoryAccent(AppTheme.colors.warningContainer, AppTheme.colors.warning),
        CategoryAccent(AppTheme.colors.surfaceVariant, AppTheme.colors.onPrimaryContainer),
    )
    val index = (key.hashCode() and Int.MAX_VALUE) % palette.size
    return palette[index]
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
            category = "Shopping",
            title = "Shopping",
            subtitle = "Buy an Avocado...",
            amount = "- Rp 229.000",
            date = "17 May 2025",
            time = "03:30 PM",
            type = TransactionType.EXPENSE
        )

        TransactionCard(
            icon = ImageVector.vectorResource(id = R.drawable.shopping_bag),
            category = "Salary",
            title = "Salary",
            subtitle = "Monthly salary",
            amount = "+ Rp 3.129.000",
            date = "17 May 2025",
            time = "04:30 PM",
            type = TransactionType.INCOME
        )
    }
}
