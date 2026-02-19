package ui.components.card.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ui.components.navigation.button.ButtonVariant
import ui.components.navigation.button.LargeButton
import ui.components.navigation.common.ChipGroup
import ui.theme.Violet100

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomSheet(
    sheetContent: BottomSheetContent,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
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
            .padding(24.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        AttachmentItem("Camera", com.moneytrack.core.R.drawable.camera, data.onCamera, Modifier.weight(1f))
        AttachmentItem("Image", com.moneytrack.core.R.drawable.gallery, data.onImage, Modifier.weight(1f))
        AttachmentItem("Document", com.moneytrack.core.R.drawable.document, data.onDocument, Modifier.weight(1f))
    }
}

@Composable
private fun AttachmentItem(label: String, iconRes: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = label,
            tint = Violet100
        )
        Spacer (Modifier.height(8.dp))
        Text (label, color = Violet100)
    }
}

@Composable
private fun ConfirmationContent(
    data: BottomSheetContent.Confirmation
) {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(data.title, style = MaterialTheme.typography.titleLarge)
        Text(data.description, style = MaterialTheme.typography.bodyMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text("Filter Transaction", style = MaterialTheme.typography.titleLarge)

        Text("Filter By", style = MaterialTheme.typography.titleMedium)
        ChipGroup(
            options = listOf("Income", "Expense", "Transfer"),
            selectedOption = "Expense",
            onOptionSelected = {}
        )

        Text("Sort By", style = MaterialTheme.typography.titleMedium)

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

