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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import ui.components.navigation.button.ButtonGroup
import ui.components.navigation.button.IconActionButton
import ui.components.navigation.button.IconButtonShape
import ui.components.navigation.button.IconButtonVariant
import ui.components.navigation.common.SelectorChip
import ui.theme.AppTheme
import ui.theme.Dimens

@Composable
fun FinancialReportCard(
    amount: String,
    selectedChart: ReportChartType,
    onChartChange: (ReportChartType) -> Unit,
    onMonthClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radius20),
        color = AppTheme.colors.surface
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacing16),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing16)
        ) {

            ReportHeader(
                selectedChart = selectedChart,
                onChartChange = onChartChange,
                onMonthClick = onMonthClick
            )

            Text(
                text = amount,
                style = AppTheme.typography.displaySmall,
                color = AppTheme.colors.onSurface
            )

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
            selected = false,
            onClick = onMonthClick,
            leadingIcon = ImageVector.vectorResource(
                id = com.moneytrack.designsystem.R.drawable.arrow_down_2
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        ButtonGroup {
            IconActionButton(
                icon = ImageVector.vectorResource(
                    id = com.moneytrack.designsystem.R.drawable.line_chart_2
                ),
                contentDescription = "Line chart",
                onClick = { onChartChange(ReportChartType.LINE) },
                variant = if (selectedChart == ReportChartType.LINE)
                    IconButtonVariant.FILLED
                else IconButtonVariant.OUTLINED,
                shape = IconButtonShape.ROUNDED_RECT,
                iconTint = AppTheme.colors.primary
            )

            IconActionButton(
                icon = ImageVector.vectorResource(
                    id = com.moneytrack.designsystem.R.drawable.pie_chart
                ),
                contentDescription = "Donut chart",
                onClick = { onChartChange(ReportChartType.DONUT) },
                variant = if (selectedChart == ReportChartType.DONUT)
                    IconButtonVariant.FILLED
                else IconButtonVariant.OUTLINED,
                shape = IconButtonShape.ROUNDED_RECT,
                iconTint = AppTheme.colors.primary
            )
        }
    }
}


@Composable
private fun LineChartPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.chartLineHeight)
            .background(
                color = AppTheme.colors.surfaceVariant,
                shape = RoundedCornerShape(Dimens.radius16)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Line Chart",
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.onSurfaceVariant
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
            .height(Dimens.chartDonutHeight)
            .background(
                color = AppTheme.colors.surfaceVariant,
                shape = RoundedCornerShape(Dimens.radius16)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = amount,
            style = AppTheme.typography.displaySmall,
            color = AppTheme.colors.onSurface
        )
    }
}


@Preview(name = "Financial Report – Light & Dark")
@Composable
private fun FinancialReportPreview() {
    Column {

        ui.theme.MoneyTrackTheme(darkTheme = false) {
            FinancialReportPreviewContent()
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        ui.theme.MoneyTrackTheme(darkTheme = true) {
            FinancialReportPreviewContent()
        }
    }
}

@Composable
private fun FinancialReportPreviewContent() {
    Column(
        modifier = Modifier
            .background(AppTheme.colors.background)
            .padding(Dimens.spacing16),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing16)
    ) {
        FinancialReportCard(
            amount = "$ 2000",
            selectedChart = ReportChartType.LINE,
            onChartChange = {},
            onMonthClick = {}
        )

        FinancialReportCard(
            amount = "$ 2000",
            selectedChart = ReportChartType.DONUT,
            onChartChange = {},
            onMonthClick = {}
        )
    }
}
