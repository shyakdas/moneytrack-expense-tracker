package ui.components.card.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneytrack.core.R
import ui.components.navigation.button.ButtonGroup
import ui.components.navigation.button.IconActionButton
import ui.components.navigation.button.IconButtonShape
import ui.components.navigation.button.IconButtonVariant
import ui.components.navigation.common.SelectorChip
import ui.theme.Violet100

@Composable
fun FinancialReportCard(
    amount: String,
    selectedChart: ReportChartType,
    onChartChange: (ReportChartType) -> Unit,
    onMonthClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Header
            ReportHeader(
                selectedChart = selectedChart,
                onChartChange = onChartChange,
                onMonthClick = onMonthClick
            )

            // Amount
            Text(
                text = amount,
                style = MaterialTheme.typography.displaySmall
            )

            // Chart
            when (selectedChart) {
                ReportChartType.LINE -> LineChartPlaceholder()
                ReportChartType.DONUT -> DonutChartPlaceholder(amount)
            }
        }
    }
}

@Composable
private fun ReportHeader(
    selectedChart: ReportChartType,
    onChartChange: (ReportChartType) -> Unit,
    onMonthClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        SelectorChip(
            label = "Month",
            onClick = onMonthClick,
            leadingIcon = ImageVector.vectorResource(R.drawable.arrow_down_2)
        )

        Spacer(modifier = Modifier.weight(1f))

        ButtonGroup {

            IconActionButton(
                icon = ImageVector.vectorResource(R.drawable.line_chart_2),
                contentDescription = "Line chart",
                onClick = { onChartChange(ReportChartType.LINE) },
                variant = if (selectedChart == ReportChartType.LINE)
                    IconButtonVariant.FILLED
                else IconButtonVariant.OUTLINED,
                shape = IconButtonShape.ROUNDED_RECT,
                iconTint = Violet100
            )

            IconActionButton(
                icon = ImageVector.vectorResource(R.drawable.pie_chart),
                contentDescription = "Donut chart",
                onClick = { onChartChange(ReportChartType.DONUT) },
                variant = if (selectedChart == ReportChartType.DONUT)
                    IconButtonVariant.FILLED
                else IconButtonVariant.OUTLINED,
                shape = IconButtonShape.ROUNDED_RECT,
                iconTint = Violet100
            )
        }
    }
}

@Composable
private fun LineChartPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Line Chart",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun DonutChartPlaceholder(
    amount: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = amount,
            style = MaterialTheme.typography.displaySmall
        )
    }
}

@Preview(
    name = "Financial Report – Line",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun FinancialReportLinePreview() {
    MaterialTheme {
        FinancialReportCard(
            amount = "$ 2000",
            selectedChart = ReportChartType.LINE,
            onChartChange = {},
            onMonthClick = {}
        )
    }
}

@Preview(
    name = "Financial Report – Donut",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun FinancialReportDonutPreview() {
    MaterialTheme {
        FinancialReportCard(
            amount = "$ 2000",
            selectedChart = ReportChartType.DONUT,
            onChartChange = {},
            onMonthClick = {}
        )
    }
}
