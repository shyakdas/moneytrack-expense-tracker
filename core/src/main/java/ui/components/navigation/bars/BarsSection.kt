package ui.components.navigation.bars

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun BarsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {

        FilterBar(
            onMonthClick = {},
            onSortClick = {}
        )

        SimpleTitleBar(title = "Yesterday")

        SectionHeaderBar(
            title = "Spend Frequency",
            onSeeAllClick = {}
        )

        MonthNavigationBar(
            month = "May",
            onPrevious = {},
            onNext = {}
        )

        ChartActionBar(
            onMonthClick = {},
            onLineChartClick = {},
            onPieChartClick = {}
        )

        ReportCtaBar(onClick = {})
    }
}

