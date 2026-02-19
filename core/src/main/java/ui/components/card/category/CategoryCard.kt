package ui.components.card.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import com.moneytrack.core.R
import ui.theme.Light100

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
        CategoryAmountType.EXPENSE -> Color(0xFFFF4D4F)
        CategoryAmountType.INCOME -> Color(0xFF00A86B)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    style = MaterialTheme.typography.titleLarge,
                    color = amountColor
                )
            }

            CategoryProgressBar(progress = progress)
        }
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
                color = Light100,
                shape = RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(12.dp)
                .background(Color(0xFFFFA000), RoundedCornerShape(50.dp))
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
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
            .height(16.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(50.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .background(
                    color = Color(0xFFFFA000),
                    shape = RoundedCornerShape(50.dp)
                )
        )
    }
}

@Preview(
    name = "Category Card – Expense & Income",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun CategoryCardPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
}
