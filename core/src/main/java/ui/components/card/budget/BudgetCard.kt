package ui.components.card.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BudgetCard(
    category: String,
    remainingAmount: String,
    spentText: String,
    progress: Float,
    status: BudgetStatus,
    modifier: Modifier = Modifier
) {
    val showWarning = status == BudgetStatus.EXCEEDED

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryPill(title = category)

                Spacer(modifier = Modifier.weight(1f))

                if (showWarning) {
                    WarningIcon()
                }
            }

            Text(
                text = "Remaining $remainingAmount",
                style = MaterialTheme.typography.headlineSmall
            )

            BudgetProgressBar(
                progress = progress,
                isExceeded = showWarning
            )

            Text(
                text = spentText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (showWarning) {
                Text(
                    text = "You’ve exceed the limit!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFFF4D4F)
                )
            }
        }
    }
}

@Composable
private fun CategoryPill(
    title: String
) {
    Row(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(12.dp)
                .background(Color(0xFF00A86B), CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun BudgetProgressBar(
    progress: Float,
    isExceeded: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(14.dp)
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
                    color = Color(0xFF00A86B),
                    shape = RoundedCornerShape(50.dp)
                )
        )
    }
}

@Composable
private fun WarningIcon() {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(Color(0xFFFF4D4F), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "!",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
    }
}

@Preview(
    name = "Budget Card",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun BudgetCardPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
}
