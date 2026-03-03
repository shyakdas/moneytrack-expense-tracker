package ui.components.card.category

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
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun CategoryCard(
    icon: ImageVector,
    title: String,
    amount: String,
    progress: Float,
    type: CategoryAmountType,
    modifier: Modifier = Modifier
) {
    val amountColor = when (type) {
        CategoryAmountType.EXPENSE -> AppTheme.colors.error
        CategoryAmountType.INCOME -> AppTheme.colors.primary
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = AppTheme.colors.surface,
                shape = RoundedCornerShape(Dimens.radius20)
            )
            .padding(Dimens.spacing16),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing16)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryPill(
                icon = icon,
                title = title
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = amount,
                style = AppTheme.typography.titleLarge,
                color = amountColor
            )
        }

        CategoryProgressBar(progress = progress)
    }
}

@Composable
private fun CategoryPill(
    icon: ImageVector,
    title: String
) {
    Row(
        modifier = Modifier
            .background(
                color = AppTheme.colors.surfaceVariant,
                shape = RoundedCornerShape(Dimens.radius50)
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
                    shape = RoundedCornerShape(Dimens.radius50)
                )
        )

        Spacer(modifier = Modifier.width(Dimens.spacing8))

        Text(
            text = title,
            style = AppTheme.typography.bodyLarge,
            color = AppTheme.colors.onSurface
        )
    }
}

@Composable
private fun CategoryProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.progressHeight)
            .background(
                color = AppTheme.colors.surfaceVariant,
                shape = RoundedCornerShape(Dimens.radius50)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .background(
                    color = AppTheme.colors.primary,
                    shape = RoundedCornerShape(Dimens.radius50)
                )
        )
    }
}


@Preview(name = "Category Card – Light & Dark")
@Composable
private fun CategoryCardPreview() {
    Column {

        ui.theme.MoneyTrackTheme(darkTheme = false) {
            CategoryPreviewContent()
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        ui.theme.MoneyTrackTheme(darkTheme = true) {
            CategoryPreviewContent()
        }
    }
}

@Composable
private fun CategoryPreviewContent() {
    Column(
        modifier = Modifier
            .background(AppTheme.colors.background)
            .padding(Dimens.spacing16),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing16)
    ) {

        CategoryCard(
            icon = ImageVector.vectorResource(id = R.drawable.shopping_bag),
            title = "Utilities",
            amount = "- $600",
            progress = 0.7f,
            type = CategoryAmountType.EXPENSE
        )

        CategoryCard(
            icon = ImageVector.vectorResource(id = R.drawable.shopping_bag),
            title = "Utilities",
            amount = "+ $600",
            progress = 0.7f,
            type = CategoryAmountType.INCOME
        )
    }
}
