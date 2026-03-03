package ui.components.card.bottomsheet

sealed class BottomSheetContent {

    data class AttachmentPicker(
        val onCamera: () -> Unit,
        val onImage: () -> Unit,
        val onDocument: () -> Unit
    ) : BottomSheetContent()

    data class Confirmation(
        val title: String,
        val description: String,
        val confirmText: String,
        val cancelText: String,
        val onConfirm: () -> Unit,
        val onCancel: () -> Unit
    ) : BottomSheetContent()

    data class Filter(
        val onApply: () -> Unit,
        val onReset: () -> Unit
    ) : BottomSheetContent()
}
