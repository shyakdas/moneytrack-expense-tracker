package ui.components.card

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

@Composable
fun TransactionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    amount: String,
    time: String,
    type: TransactionType,
    modifier: Modifier = Modifier
) {
    val amountColor = when (type) {
        TransactionType.EXPENSE -> Color(0xFFFF4D4F)
        TransactionType.INCOME -> Color(0xFF00A86B)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(0xFFFFEFD2),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFFFA000),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = amount,
                    style = MaterialTheme.typography.titleMedium,
                    color = amountColor
                )

                Text(
                    text = time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(
    name = "Transaction Card – Expense & Income",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun TransactionCardPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                subtitle = "Buy an Avocado...",
                amount = "+ Rp 3.129.000",
                time = "04:30 PM",
                type = TransactionType.INCOME
            )
        }
    }
}
