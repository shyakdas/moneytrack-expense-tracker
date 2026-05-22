// Copyright (c) 2026 shyakdas

package ui.components.card.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import ui.components.navigation.button.ButtonVariant
import ui.components.navigation.button.LargeButton
import ui.components.navigation.common.ChipGroup
import ui.components.surface.MoneyTrackBottomSheet
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
fun AppBottomSheet(
    sheetContent: BottomSheetContent,
    onDismiss: () -> Unit
) {
    MoneyTrackBottomSheet(onDismissRequest = onDismiss) {
        when (sheetContent) {
            is BottomSheetContent.AttachmentPicker ->
                AttachmentPickerContent(sheetContent)

            is BottomSheetContent.Confirmation ->
                ConfirmationContent(sheetContent)

            is BottomSheetContent.Filter ->
                FilterContent(sheetContent)
        }
    }
}


@Composable
private fun AttachmentPickerContent(
    data: BottomSheetContent.AttachmentPicker
) {
    Row(
        modifier = Modifier
            .padding(Dimens.spacing24)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacing16)
    ) {
        AttachmentItem(
            label = "Camera",
            iconRes = com.moneytrack.designsystem.R.drawable.camera,
            onClick = data.onCamera,
            modifier = Modifier.weight(1f)
        )

        AttachmentItem(
            label = "Image",
            iconRes = com.moneytrack.designsystem.R.drawable.gallery,
            onClick = data.onImage,
            modifier = Modifier.weight(1f)
        )

        AttachmentItem(
            label = "Document",
            iconRes = com.moneytrack.designsystem.R.drawable.document,
            onClick = data.onDocument,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AttachmentItem(
    label: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.radius16))
            .background(AppTheme.colors.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(vertical = Dimens.spacing20),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = label,
            tint = AppTheme.colors.primary
        )

        Spacer(modifier = Modifier.height(Dimens.spacing8))

        Text(
            text = label,
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.primary
        )
    }
}


@Composable
private fun ConfirmationContent(
    data: BottomSheetContent.Confirmation
) {
    Column(
        modifier = Modifier.padding(Dimens.spacing24),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing16)
    ) {

        Text(
            text = data.title,
            style = AppTheme.typography.titleLarge,
            color = AppTheme.colors.onSurface
        )

        Text(
            text = data.description,
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.onSurfaceVariant
        )

        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spacing12)) {
            LargeButton(
                text = data.cancelText,
                onClick = data.onCancel,
                variant = ButtonVariant.SECONDARY,
                modifier = Modifier.weight(1f)
            )

            LargeButton(
                text = data.confirmText,
                onClick = data.onConfirm,
                variant = ButtonVariant.PRIMARY,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
private fun FilterContent(
    data: BottomSheetContent.Filter
) {
    Column(
        modifier = Modifier.padding(Dimens.spacing24),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing20)
    ) {

        Text(
            text = "Filter Transaction",
            style = AppTheme.typography.titleLarge,
            color = AppTheme.colors.onSurface
        )

        Text(
            text = "Filter By",
            style = AppTheme.typography.titleMedium
        )

        ChipGroup(
            options = listOf("Income", "Expense", "Transfer"),
            selectedOption = "Expense",
            onOptionSelected = {}
        )

        Text(
            text = "Sort By",
            style = AppTheme.typography.titleMedium
        )

        ChipGroup(
            options = listOf("Highest", "Lowest", "Newest", "Oldest"),
            selectedOption = "Highest",
            onOptionSelected = {}
        )

        LargeButton(
            text = "Apply",
            onClick = data.onApply,
            variant = ButtonVariant.PRIMARY
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "BottomSheet – Attachment Picker (Light & Dark)")
@Composable
private fun BottomSheetAttachmentPreview() {
    Column {
        MoneyTrackTheme(darkTheme = false) {
            AppBottomSheet(
                sheetContent = BottomSheetContent.AttachmentPicker(
                    onCamera = {},
                    onImage = {},
                    onDocument = {}
                ),
                onDismiss = {}
            )
        }

        MoneyTrackTheme(darkTheme = true) {
            AppBottomSheet(
                sheetContent = BottomSheetContent.AttachmentPicker(
                    onCamera = {},
                    onImage = {},
                    onDocument = {}
                ),
                onDismiss = {}
            )
        }
    }
}
