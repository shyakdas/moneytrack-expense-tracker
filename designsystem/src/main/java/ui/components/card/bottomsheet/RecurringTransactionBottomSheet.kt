// Copyright (c) 2026 shyakdas

package ui.components.card.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.components.navigation.button.ButtonVariant
import ui.components.navigation.button.LargeButton
import ui.components.surface.MoneyTrackBottomSheet
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringTransactionBottomSheet(
    state: RecurringState,
    onFrequencyClick: () -> Unit,
    onEndClick: () -> Unit,
    onNext: () -> Unit,
    onDismiss: () -> Unit
) {
    MoneyTrackBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .padding(Dimens.spacing24)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing20)
        ) {

            FrequencyDropdown(
                label = "Frequency",
                onClick = onFrequencyClick
            )

            FrequencyDropdown(
                label = "End After",
                onClick = onEndClick
            )

            LargeButton(
                text = "Next",
                onClick = onNext,
                variant = ButtonVariant.PRIMARY
            )
        }
    }
}


@Composable
private fun FrequencyDropdown(
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.inputHeight)
            .border(
                width = Dimens.spacing1,
                color = AppTheme.colors.outline,
                shape = RoundedCornerShape(Dimens.radius12)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.spacing16),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = label,
            style = AppTheme.typography.bodyLarge,
            color = AppTheme.colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(com.moneytrack.designsystem.R.drawable.arrow_down_2),
            contentDescription = null,
            tint = AppTheme.colors.onSurfaceVariant
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Recurring Bottom Sheet – Light & Dark")
@Composable
private fun RecurringBottomSheetPreview() {
    Column {
        MoneyTrackTheme(darkTheme = false) {
            PreviewContent()
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        MoneyTrackTheme(darkTheme = true) {
            PreviewContent()
        }
    }
}

@Composable
private fun PreviewContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.background)
            .padding(Dimens.spacing16)
    ) {
        RecurringTransactionBottomSheet(
            state = RecurringState(),
            onFrequencyClick = {},
            onEndClick = {},
            onNext = {},
            onDismiss = {}
        )
    }
}
