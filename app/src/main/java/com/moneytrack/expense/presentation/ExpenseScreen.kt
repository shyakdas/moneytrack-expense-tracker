// Copyright (c) 2026 shyakdas

@file:Suppress(
    "LongMethod",
    "UnusedPrivateMember",
    "LongParameterList",
    "TooManyFunctions",
    "MagicNumber",
)

package com.moneytrack.expense.presentation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import com.moneytrack.R
import com.moneytrack.designsystem.R as DsR
import com.moneytrack.expense.domain.model.ExpenseCategory
import com.moneytrack.expense.domain.model.RepeatFrequency
import com.moneytrack.transaction.presentation.toTransactionIconRes
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import ui.components.card.bottomsheet.SheetBlurHost
import ui.components.form.control.PrimarySwitch
import ui.components.form.input.InputField
import ui.components.navigation.button.LargeButton
import ui.components.surface.MoneyTrackBottomSheet
import ui.components.surface.MoneyTrackCard
import ui.components.surface.MoneyTrackScreenBackground
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

private const val CATEGORY_SHEET_VISIBLE_ROWS = 5
private const val FALLBACK_CATEGORY_COLOR_HEX = "#0B5D7A"
private const val SELECTED_ITEM_ALPHA = 0.12f
private const val ATTACHMENT_IMAGE_EXTENSION = ".jpg"
private const val ATTACHMENT_CACHE_DIR = "expense_attachments"
private const val ATTACHMENT_FILE_PREFIX = "receipt_"
private const val DATE_OUTPUT_PATTERN = "dd MMM yyyy"
private const val TIME_PATTERN = "hh:mm a"
private const val END_OF_DAY_HOUR = 23
private const val END_OF_DAY_MINUTE = 59
private const val END_OF_DAY_SECOND = 59
private const val END_OF_DAY_MILLISECOND = 999
private const val DEFAULT_REPEAT_AFTER_MONTHS = 4
private const val MIN_REPEAT_MONTHS = 1
private const val MAX_REPEAT_MONTHS = 12
private const val REPEAT_END_MATCH_TOLERANCE_DAYS = 2L
private const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L
private const val DESCRIPTION_MAX_LENGTH = 20
private val ExpenseRowIconContainerSize = Dimens.spacing32
private val ExpenseRowIconSize = 14.dp
private val ExpensePrimaryRowVerticalPadding = Dimens.spacing10
private val ExpenseFieldBoostedVerticalPadding = Dimens.spacing20
private val RepeatFieldBoostedPadding = Dimens.spacing16
private val CategoryTileSize = 104.dp
private const val CATEGORY_HINT_ANIMATION_OFFSET = 8f
private val fallbackCategoryColor = categoryColor(FALLBACK_CATEGORY_COLOR_HEX)
private val ExpenseTopStart = Color(0xFF0B111A)
private val ExpenseTopMiddle = Color(0xFF0A1422)
private val ExpenseTopEnd = Color(0xFF08101D)
private val ExpenseRowCard = Color(0xCC111A26)
private val ExpenseRowBorder = Color(0x2E95A7C0)
private val ExpenseLine = Color(0x26D9E2F1)
private val ExpenseIconBg = Color(0x2EFF6F6F)
private val ExpenseAccent = Color(0xFFFF6F6F)
private val ExpensePillBg = Color(0xCC1A2130)
private val ExpensePrimaryText = Color(0xFFF3F6FB)
private val ExpenseSecondaryText = Color(0xFFAAB6C8)
private val ExpenseContinueStart = Color(0xFFFF7D7D)
private val ExpenseContinueEnd = Color(0xFFFF6A6A)
private val ExpenseContinueText = Color(0xFF111622)

@Composable
@Suppress("CyclomaticComplexMethod")
fun ExpenseScreen(
    uiState: ExpenseUiState,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    onAmountChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onAttachmentSelected: (String, String, ExpenseAttachmentType) -> Unit,
    onAttachmentRemoved: () -> Unit,
    onRepeatConfigured: (RepeatFrequency, Long) -> Unit,
    onRepeatRemoved: () -> Unit,
    onCategorySelected: (Long) -> Unit,
    onOccurredAtChanged: (Long) -> Unit,
) {
    val context = LocalContext.current
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var showRepeatScreen by remember { mutableStateOf(false) }
    var showDescriptionSheet by remember { mutableStateOf(false) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    val isAnySheetVisible = showAttachmentSheet ||
        showRepeatScreen ||
        showDescriptionSheet
    val imageFallbackName = stringResource(id = R.string.expense_attachment_image_fallback_name)
    val documentFallbackName = stringResource(id = R.string.expense_attachment_document_fallback_name)

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        context.takePersistableReadPermission(uri)
        onAttachmentSelected(
            uri.toString(),
            context.resolveDisplayName(uri) ?: imageFallbackName,
            ExpenseAttachmentType.IMAGE,
        )
        showAttachmentSheet = false
    }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        context.takePersistableReadPermission(uri)
        onAttachmentSelected(
            uri.toString(),
            context.resolveDisplayName(uri) ?: documentFallbackName,
            ExpenseAttachmentType.DOCUMENT,
        )
        showAttachmentSheet = false
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { isCaptured ->
        val capturedUri = pendingCameraUri
        if (isCaptured && capturedUri != null) {
            onAttachmentSelected(
                capturedUri.toString(),
                context.resolveDisplayName(capturedUri) ?: imageFallbackName,
                ExpenseAttachmentType.IMAGE,
            )
        }
        pendingCameraUri = null
        showAttachmentSheet = false
    }

    SheetBlurHost(isSheetVisible = isAnySheetVisible) {
        ExpenseContent(
            amountInput = uiState.amountInput,
            amountText = uiState.amountText,
            description = uiState.description,
            isSubmitEnabled = uiState.isSubmitEnabled,
            isEditMode = uiState.isEditMode,
            attachment = uiState.attachment,
            repeatSchedule = uiState.repeatSchedule,
            categories = uiState.categories,
            selectedCategoryId = uiState.selectedCategoryId,
            selectedCategory = uiState.selectedCategory,
            onBackClick = onBackClick,
            onContinueClick = onContinueClick,
            onAmountChanged = onAmountChanged,
            onDescriptionChanged = onDescriptionChanged,
            onDescriptionClick = { showDescriptionSheet = true },
            onAttachmentClick = { showAttachmentSheet = true },
            onAttachmentRemoved = onAttachmentRemoved,
            onRepeatClick = { showRepeatScreen = true },
            onRepeatEnabledChange = { isEnabled ->
                if (isEnabled) {
                    showRepeatScreen = true
                } else {
                    onRepeatRemoved()
                }
            },
            onCategorySelected = onCategorySelected,
            occurredAtEpochMillis = uiState.occurredAtEpochMillis,
            onOccurredAtChanged = onOccurredAtChanged,
        )
    }

    if (showAttachmentSheet) {
        AttachmentPickerBottomSheet(
            onDismiss = { showAttachmentSheet = false },
            onCameraClick = {
                val captureUri = context.createCameraAttachmentUri()
                pendingCameraUri = captureUri
                cameraLauncher.launch(captureUri)
            },
            onImageClick = {
                imagePickerLauncher.launch(arrayOf("image/*"))
            },
            onDocumentClick = {
                documentPickerLauncher.launch(arrayOf("application/pdf", "*/*"))
            },
        )
    }

    if (showRepeatScreen) {
        RepeatTransactionScreen(
            initialRepeatSchedule = uiState.repeatSchedule,
            initialOccurredAt = uiState.occurredAtEpochMillis,
            onBackClick = { showRepeatScreen = false },
            onDoneClick = { isEnabled, frequency, endAtEpochMillis, startAtEpochMillis ->
                onOccurredAtChanged(startAtEpochMillis)
                if (isEnabled && frequency != null && endAtEpochMillis != null) {
                    onRepeatConfigured(frequency, endAtEpochMillis)
                } else {
                    onRepeatRemoved()
                }
                showRepeatScreen = false
            },
        )
    }

    if (showDescriptionSheet) {
        DescriptionBottomSheet(
            initialDescription = uiState.description,
            onDismiss = { showDescriptionSheet = false },
            onSave = { descriptionText ->
                onDescriptionChanged(descriptionText)
                showDescriptionSheet = false
            },
        )
    }
}

@Composable
internal fun ExpenseContent(
    amountInput: String,
    amountText: String,
    description: String,
    isSubmitEnabled: Boolean,
    isEditMode: Boolean,
    attachment: ExpenseAttachmentUiState?,
    repeatSchedule: ExpenseRepeatUiState?,
    categories: List<ExpenseCategory>,
    selectedCategoryId: Long?,
    selectedCategory: ExpenseCategory?,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    onAmountChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onDescriptionClick: () -> Unit,
    onAttachmentClick: () -> Unit,
    onAttachmentRemoved: () -> Unit,
    onRepeatClick: () -> Unit,
    onRepeatEnabledChange: (Boolean) -> Unit,
    onCategorySelected: (Long) -> Unit,
    occurredAtEpochMillis: Long,
    onOccurredAtChanged: (Long) -> Unit,
) {
    val dateText = remember(occurredAtEpochMillis) {
        SimpleDateFormat(DATE_OUTPUT_PATTERN, Locale.getDefault()).format(occurredAtEpochMillis)
    }
    val timeText = remember(occurredAtEpochMillis) {
        SimpleDateFormat(TIME_PATTERN, Locale.getDefault()).format(occurredAtEpochMillis)
    }
    val context = LocalContext.current

    MoneyTrackScreenBackground {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = Dimens.spacing24),
        ) {
            Spacer(modifier = Modifier.height(Dimens.spacing12))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier
                        .size(Dimens.iconButtonSize)
                        .clickable(onClick = onBackClick),
                    shape = CircleShape,
                    color = ExpensePillBg,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = DsR.drawable.arrow_left),
                        contentDescription = stringResource(id = R.string.expense_back_content_desc),
                        tint = ExpensePrimaryText,
                        modifier = Modifier
                            .padding(Dimens.spacing12)
                            .size(Dimens.icon20),
                    )
                }

                Text(
                    text = if (isEditMode) "Edit Expense" else "Add Expense",
                    modifier = Modifier.weight(1f),
                    style = AppTheme.typography.headlineSmall,
                    color = ExpensePrimaryText,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.size(Dimens.iconButtonSize))
            }

            Spacer(modifier = Modifier.height(Dimens.spacing24))
            Text(
                text = "How much did you spend?",
                style = AppTheme.typography.labelSmall,
                color = ExpenseSecondaryText,
            )
            Spacer(modifier = Modifier.height(Dimens.spacing8))
            AmountInputField(
                amountInput = amountInput,
                amountText = amountText,
                onAmountChanged = onAmountChanged,
            )
            Spacer(modifier = Modifier.height(Dimens.spacing12))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(ExpenseLine),
            )
            Spacer(modifier = Modifier.height(Dimens.spacing20))
            DateTimeInlineRow(
                dateText = dateText,
                timeText = timeText,
                onDateClick = {
                    context.showExpenseDatePicker(occurredAtEpochMillis) { updatedAt ->
                        onOccurredAtChanged(updatedAt)
                    }
                },
                onTimeClick = {
                    context.showExpenseTimePicker(occurredAtEpochMillis) { updatedAt ->
                        onOccurredAtChanged(updatedAt)
                    }
                },
            )
            Spacer(modifier = Modifier.height(Dimens.spacing16))
            CategoryHorizontalPicker(
                categories = categories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = onCategorySelected,
            )
            Spacer(modifier = Modifier.height(Dimens.spacing20))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .imePadding()
                .padding(horizontal = Dimens.spacing16),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            ) {
                GlassSectionCard {
                    DescriptionRow(
                        value = description,
                        onClick = onDescriptionClick,
                    )
                    GlassDivider()
                    ExpenseRow(
                        iconRes = DsR.drawable.attachment,
                        title = "Attachment (optional)",
                        subtitle = if (attachment == null) "Upload receipt or note" else attachment.name,
                        rowVerticalPadding = ExpenseFieldBoostedVerticalPadding,
                        onClick = onAttachmentClick,
                        showArrow = true,
                    )
                }
                Spacer(modifier = Modifier.height(Dimens.spacing16))

                GlassSectionCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onRepeatClick)
                            .padding(vertical = RepeatFieldBoostedPadding),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        LeadingIcon(iconRes = DsR.drawable.recurring_bill)
                        Spacer(modifier = Modifier.width(Dimens.spacing12))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Repeat transaction",
                                style = AppTheme.typography.titleSmall,
                                color = ExpensePrimaryText,
                            )
                            Text(
                                text = "Make this a recurring expense",
                                style = AppTheme.typography.labelSmall,
                                color = ExpenseSecondaryText,
                            )
                        }
                        PrimarySwitch(
                            checked = repeatSchedule != null,
                            onCheckedChange = onRepeatEnabledChange,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = Dimens.spacing8),
            ) {
                LargeButton(
                    text = "Save",
                    onClick = onContinueClick,
                    enabled = isSubmitEnabled,
                )
            }
            Spacer(modifier = Modifier.height(Dimens.spacing8))
        }
        }
    }
}

@Composable
private fun CategoryHorizontalPicker(
    categories: List<ExpenseCategory>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long) -> Unit,
) {
    val orderedCategories = remember(categories, selectedCategoryId) {
        val selected = categories.firstOrNull { it.id == selectedCategoryId } ?: return@remember categories
        buildList(categories.size) {
            add(selected)
            addAll(categories.filterNot { it.id == selected.id })
        }
    }
    val hintTransition = rememberInfiniteTransition(label = "category_hint")
    val hintOffsetX by hintTransition.animateFloat(
        initialValue = 0f,
        targetValue = CATEGORY_HINT_ANIMATION_OFFSET,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "category_hint_offset",
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacing12),
        ) {
            items(orderedCategories.size, key = { index -> orderedCategories[index].id }) { index ->
                val category = orderedCategories[index]
                CategoryTile(
                    category = category,
                    selected = category.id == selectedCategoryId,
                    onClick = { onCategorySelected(category.id) },
                )
            }
        }

        if (orderedCategories.size > 2) {
            Icon(
                imageVector = ImageVector.vectorResource(id = DsR.drawable.arrow_right_2),
                contentDescription = null,
                tint = ExpensePrimaryText.copy(alpha = 0.65f),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset { IntOffset(x = hintOffsetX.dp.roundToPx(), y = 0) }
                    .background(ExpensePillBg, CircleShape)
                    .padding(Dimens.spacing8)
                    .size(Dimens.icon16),
            )
        }
    }
}

@Composable
private fun CategoryTile(
    category: ExpenseCategory,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val accent = categoryColor(category.colorHex)
    val containerColor = if (selected) {
        accent
    } else {
        accent.copy(alpha = 0.18f)
    }

    Surface(
        modifier = Modifier
            .size(CategoryTileSize)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Dimens.radius16),
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(
            width = Dimens.borderNormal,
            color = if (selected) accent else ExpenseRowBorder,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.spacing12),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Surface(
                shape = CircleShape,
                color = if (selected) ExpensePrimaryText.copy(alpha = 0.9f) else ExpensePillBg,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = category.name.toTransactionIconRes()),
                    contentDescription = null,
                    tint = if (selected) accent else ExpensePrimaryText.copy(alpha = 0.85f),
                    modifier = Modifier
                        .padding(Dimens.spacing10)
                        .size(Dimens.icon20),
                )
            }
            Spacer(modifier = Modifier.height(Dimens.spacing8))
            Text(
                text = category.name,
                style = AppTheme.typography.titleSmall,
                color = if (selected) ExpensePrimaryText else ExpensePrimaryText.copy(alpha = 0.9f),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun GlassSectionCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radius20),
        color = ExpenseRowCard,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = Dimens.borderNormal,
                    color = ExpenseRowBorder,
                    shape = RoundedCornerShape(Dimens.radius20),
                )
                .padding(Dimens.spacing12),
            content = content,
        )
    }
}

@Composable
private fun ExpenseRow(
    iconRes: Int,
    title: String,
    subtitle: String?,
    rowVerticalPadding: androidx.compose.ui.unit.Dp = ExpensePrimaryRowVerticalPadding,
    trailingLabel: String? = null,
    showArrow: Boolean = false,
    onClick: (() -> Unit)? = null,
    onTrailingClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = rowVerticalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LeadingIcon(iconRes = iconRes)
        Spacer(modifier = Modifier.width(Dimens.spacing12))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = AppTheme.typography.titleSmall,
                color = ExpensePrimaryText,
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = AppTheme.typography.labelSmall,
                    color = ExpenseSecondaryText,
                )
            }
        }
        if (trailingLabel != null) {
            Surface(
                shape = RoundedCornerShape(Dimens.radius16),
                color = ExpensePillBg,
                modifier = Modifier.clickable { onTrailingClick?.invoke() },
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = Dimens.spacing12, vertical = Dimens.spacing8),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = trailingLabel,
                        style = AppTheme.typography.titleSmall,
                        color = ExpensePrimaryText,
                    )
                    Spacer(modifier = Modifier.width(Dimens.spacing6))
                    Icon(
                        imageVector = ImageVector.vectorResource(id = DsR.drawable.arrow_down_2),
                        contentDescription = null,
                        tint = ExpensePrimaryText,
                        modifier = Modifier.size(Dimens.icon16),
                    )
                }
            }
        } else if (showArrow) {
            Icon(
                imageVector = ImageVector.vectorResource(id = DsR.drawable.arrow_right_2),
                contentDescription = null,
                tint = ExpenseSecondaryText,
                modifier = Modifier.size(Dimens.icon16),
            )
        }
    }
}

@Composable
private fun LeadingIcon(iconRes: Int) {
    Box(
        modifier = Modifier
            .size(ExpenseRowIconContainerSize)
            .background(
                color = ExpenseIconBg,
                shape = RoundedCornerShape(Dimens.radius16),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = iconRes),
            contentDescription = null,
            tint = ExpenseAccent,
            modifier = Modifier.size(ExpenseRowIconSize),
        )
    }
}

@Composable
private fun DescriptionRow(
    value: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = ExpenseFieldBoostedVerticalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LeadingIcon(iconRes = DsR.drawable.edit)
        Spacer(modifier = Modifier.width(Dimens.spacing12))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Description (optional)",
                style = AppTheme.typography.titleSmall,
                color = ExpensePrimaryText,
            )
            Text(
                text = value.ifBlank { "Add a note" },
                style = AppTheme.typography.labelSmall,
                color = ExpenseSecondaryText.copy(alpha = 0.85f),
            )
        }
        Icon(
            imageVector = ImageVector.vectorResource(id = DsR.drawable.arrow_right_2),
            contentDescription = null,
            tint = ExpenseSecondaryText,
            modifier = Modifier.size(Dimens.icon16),
        )
    }
}

@Composable
private fun DescriptionBottomSheet(
    initialDescription: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var draft by remember(initialDescription) {
        mutableStateOf(initialDescription.take(DESCRIPTION_MAX_LENGTH))
    }
    MoneyTrackBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacing16)
                .padding(bottom = Dimens.spacing24),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing16),
        ) {
            Text(
                text = "Description",
                style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.onSurface,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = Dimens.borderNormal,
                        color = AppTheme.colors.outline,
                        shape = RoundedCornerShape(Dimens.radius12),
                    ),
                shape = RoundedCornerShape(Dimens.radius12),
                color = AppTheme.colors.surface,
            ) {
                BasicTextField(
                    value = draft,
                    onValueChange = { draft = it.take(DESCRIPTION_MAX_LENGTH) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.spacing16),
                    textStyle = AppTheme.typography.bodyMedium.copy(color = AppTheme.colors.onSurface),
                    cursorBrush = SolidColor(AppTheme.colors.primary),
                    decorationBox = { inner ->
                        if (draft.isBlank()) {
                            Text(
                                text = "Add a note",
                                style = AppTheme.typography.bodyMedium,
                                color = AppTheme.colors.onSurfaceVariant,
                            )
                        }
                        inner()
                    },
                )
            }
            LargeButton(
                text = "Save",
                onClick = { onSave(draft.trim()) },
            )
        }
    }
}

@Composable
private fun GlassDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(ExpenseLine),
    )
}

@Composable
private fun CoralButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    text: String = stringResource(id = R.string.expense_continue),
    showArrow: Boolean = true,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(Dimens.radius16),
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = if (enabled) {
                            listOf(ExpenseContinueStart, ExpenseContinueEnd)
                        } else {
                            listOf(
                                AppTheme.colors.onSurfaceVariant.copy(alpha = 0.35f),
                                AppTheme.colors.onSurfaceVariant.copy(alpha = 0.35f),
                            )
                        },
                    ),
                )
                .padding(horizontal = Dimens.spacing20, vertical = Dimens.spacing8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = text,
                style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = ExpenseContinueText,
            )
            Spacer(modifier = Modifier.weight(1f))
            if (showArrow) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = DsR.drawable.arrow_right_2),
                    contentDescription = null,
                    tint = ExpenseContinueText,
                )
            } else {
                Spacer(modifier = Modifier.size(Dimens.icon20))
            }
        }
    }
}

@Composable
private fun AmountInputField(
    amountInput: String,
    amountText: String,
    onAmountChanged: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                focusRequester.requestFocus()
                keyboardController?.show()
            },
    ) {
        BasicTextField(
            value = amountInput,
            onValueChange = onAmountChanged,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(color = Color.Transparent),
            cursorBrush = SolidColor(AppTheme.colors.onPrimary),
            decorationBox = {
                Text(
                    text = amountTextWithAccentSymbol(amountText),
                    style = AppTheme.typography.displayMedium,
                    color = ExpensePrimaryText,
                )
            },
        )
    }
}

@Composable
private fun DateTimeInlineRow(
    dateText: String,
    timeText: String,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacing12),
    ) {
        SmallInfoChip(
            iconRes = DsR.drawable.transaction,
            text = dateText,
            onClick = onDateClick,
            modifier = Modifier.weight(1f),
        )
        SmallInfoChip(
            iconRes = DsR.drawable.notifiaction,
            text = timeText,
            onClick = onTimeClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SmallInfoChip(
    iconRes: Int,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(Dimens.radius16),
        color = ExpensePillBg,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacing12, vertical = Dimens.spacing10),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacing10),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = iconRes),
                contentDescription = null,
                tint = ExpensePrimaryText.copy(alpha = 0.9f),
                modifier = Modifier.size(Dimens.icon16),
            )
            Text(
                text = text,
                style = AppTheme.typography.titleSmall,
                color = ExpensePrimaryText,
                maxLines = 1,
            )
        }
    }
}

private fun amountTextWithAccentSymbol(amountText: String) = buildAnnotatedString {
    val symbolEndIndex = amountText.indexOfFirst { it.isDigit() || it == '-' || it == '.' }
    if (symbolEndIndex <= 0) {
        append(amountText)
        return@buildAnnotatedString
    }

    pushStyle(SpanStyle(color = ExpenseAccent))
    append(amountText.substring(0, symbolEndIndex))
    pop()
    append(amountText.substring(symbolEndIndex))
}

@Composable
private fun CategorySelectorField(
    selectedCategory: ExpenseCategory?,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.inputHeight)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Dimens.radius16),
        color = AppTheme.colors.surfaceVariant,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = Dimens.borderNormal,
                    color = AppTheme.colors.outline,
                    shape = RoundedCornerShape(Dimens.radius16),
                )
                .padding(horizontal = Dimens.spacing12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (selectedCategory == null) {
                Text(
                    text = stringResource(id = R.string.expense_category_placeholder),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.onSurfaceVariant,
                )
            } else {
                SelectedCategoryChip(category = selectedCategory)
            }

            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = ImageVector.vectorResource(id = DsR.drawable.arrow_down_2),
                contentDescription = null,
                tint = AppTheme.colors.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SelectedCategoryChip(category: ExpenseCategory) {
    Surface(
        shape = RoundedCornerShape(Dimens.radius24),
        color = AppTheme.colors.primaryContainer,
        modifier = Modifier.padding(vertical = Dimens.spacing4),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimens.spacing12, vertical = Dimens.spacing8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.spacing16)
                    .background(
                        color = categoryColor(category.colorHex),
                        shape = CircleShape,
                    ),
            )
            Spacer(modifier = Modifier.width(Dimens.spacing8))
            Text(
                text = category.name,
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colors.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun CategoryPickerBottomSheet(
    uiState: ExpenseUiState,
    onDismiss: () -> Unit,
    onCategorySelected: (Long) -> Unit,
) {
    MoneyTrackBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacing16)
                .imePadding()
                .padding(bottom = Dimens.spacing24),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.expense_categories_sheet_title),
                    style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = AppTheme.colors.onSurface,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(modifier = Modifier.height(Dimens.spacing8))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.spacing72 * CATEGORY_SHEET_VISIBLE_ROWS),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacing8),
            ) {
                itemsIndexed(
                    items = uiState.categories,
                    key = { _, category -> category.id },
                ) { _, category ->
                    CategoryPickerItem(
                        category = category,
                        isSelected = uiState.selectedCategoryId == category.id,
                        onSelect = { onCategorySelected(category.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryPickerItem(
    category: ExpenseCategory,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(Dimens.radius16),
        color = if (isSelected) {
            AppTheme.colors.primary.copy(alpha = SELECTED_ITEM_ALPHA)
        } else {
            AppTheme.colors.surfaceVariant
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacing12, vertical = Dimens.spacing12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.spacing12)
                    .background(
                        color = categoryColor(category.colorHex),
                        shape = CircleShape,
                    ),
            )
            Spacer(modifier = Modifier.width(Dimens.spacing12))
            Text(
                text = category.name,
                style = AppTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.onSurface,
                modifier = Modifier.weight(1f),
            )
            if (isSelected) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = DsR.drawable.success),
                    contentDescription = null,
                    tint = AppTheme.colors.primary,
                )
            }
        }
    }
}

@Composable
private fun AttachmentInput(
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.inputHeight)
            .border(
                width = Dimens.borderNormal,
                color = AppTheme.colors.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(Dimens.radius16),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.spacing16),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = DsR.drawable.attachment),
            contentDescription = null,
            tint = AppTheme.colors.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(Dimens.spacing12))
        Text(
            text = stringResource(id = R.string.expense_add_attachment),
            style = AppTheme.typography.bodyLarge,
            color = AppTheme.colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun AttachmentPreview(
    attachment: ExpenseAttachmentUiState,
    onRemoveClick: () -> Unit,
) {
    Box {
        when (attachment.type) {
            ExpenseAttachmentType.IMAGE -> ImageAttachmentPreview(
                uriString = attachment.uriString,
                contentDescription = attachment.name,
            )
            ExpenseAttachmentType.DOCUMENT -> DocumentAttachmentPreview(name = attachment.name)
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(Dimens.icon24)
                .clickable(onClick = onRemoveClick),
            shape = CircleShape,
            color = AppTheme.colors.onSurfaceVariant.copy(alpha = 0.72f),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = DsR.drawable.close),
                    contentDescription = stringResource(id = R.string.expense_attachment_remove),
                    tint = AppTheme.colors.onPrimary,
                    modifier = Modifier.size(Dimens.icon16),
                )
            }
        }
    }
}

@Composable
private fun ImageAttachmentPreview(
    uriString: String,
    contentDescription: String,
) {
    val context = LocalContext.current
    val bitmap = remember(uriString) {
        runCatching {
            context.contentResolver.openInputStream(uriString.toUri())?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }?.asImageBitmap()
        }.getOrNull()
    }

    Surface(
        modifier = Modifier.size(Dimens.iconContainerSize * 2),
        shape = RoundedCornerShape(Dimens.radius16),
        color = AppTheme.colors.surfaceVariant,
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = DsR.drawable.gallery),
                    contentDescription = null,
                    tint = AppTheme.colors.primary,
                    modifier = Modifier.size(Dimens.icon24),
                )
            }
        }
    }
}

@Composable
private fun DocumentAttachmentPreview(
    name: String,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radius16),
        color = AppTheme.colors.surfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing16),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = DsR.drawable.document),
                contentDescription = null,
                tint = AppTheme.colors.primary,
                modifier = Modifier.size(Dimens.icon24),
            )
            Spacer(modifier = Modifier.width(Dimens.spacing12))
            Text(
                text = name,
                style = AppTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.onSurface,
            )
        }
    }
}

@Composable
private fun AttachmentPickerBottomSheet(
    onDismiss: () -> Unit,
    onCameraClick: () -> Unit,
    onImageClick: () -> Unit,
    onDocumentClick: () -> Unit,
) {
    MoneyTrackBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacing16)
                .padding(bottom = Dimens.spacing24),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(Dimens.spacing32)
                    .height(Dimens.spacing4)
                    .background(
                        color = AppTheme.colors.primary.copy(alpha = SELECTED_ITEM_ALPHA),
                        shape = RoundedCornerShape(Dimens.radius24),
                    ),
            )
            Spacer(modifier = Modifier.height(Dimens.spacing24))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8),
            ) {
                AttachmentOptionCard(
                    modifier = Modifier.weight(1f),
                    iconRes = DsR.drawable.camera,
                    label = stringResource(id = R.string.expense_attachment_camera),
                    onClick = onCameraClick,
                )
                AttachmentOptionCard(
                    modifier = Modifier.weight(1f),
                    iconRes = DsR.drawable.gallery,
                    label = stringResource(id = R.string.expense_attachment_image),
                    onClick = onImageClick,
                )
                AttachmentOptionCard(
                    modifier = Modifier.weight(1f),
                    iconRes = DsR.drawable.document,
                    label = stringResource(id = R.string.expense_attachment_document),
                    onClick = onDocumentClick,
                )
            }
        }
    }
}

@Composable
private fun AttachmentOptionCard(
    modifier: Modifier = Modifier,
    iconRes: Int,
    label: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(Dimens.radius16),
        color = AppTheme.colors.primary.copy(alpha = SELECTED_ITEM_ALPHA),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.spacing24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing12),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = iconRes),
                contentDescription = null,
                tint = AppTheme.colors.primary,
                modifier = Modifier.size(Dimens.icon24),
            )
            Text(
                text = label,
                style = AppTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.primary,
            )
        }
    }
}

private enum class RepeatEndOption {
    NEVER,
    ON_DATE,
    AFTER_MONTHS,
}

@Composable
private fun RepeatTransactionScreen(
    initialRepeatSchedule: ExpenseRepeatUiState?,
    initialOccurredAt: Long,
    onBackClick: () -> Unit,
    onDoneClick: (Boolean, RepeatFrequency?, Long?, Long) -> Unit,
) {
    val context = LocalContext.current
    var selectedFrequency by remember(initialRepeatSchedule) {
        mutableStateOf(initialRepeatSchedule?.frequency ?: RepeatFrequency.MONTHLY)
    }
    var startAt by remember(initialOccurredAt) { mutableLongStateOf(initialOccurredAt) }
    val initialRepeatEndState = remember(initialRepeatSchedule, initialOccurredAt) {
        deriveInitialRepeatEndState(
            initialRepeatSchedule = initialRepeatSchedule,
            initialOccurredAt = initialOccurredAt,
        )
    }
    var selectedEndOption by remember(initialRepeatSchedule, initialOccurredAt) {
        mutableStateOf(initialRepeatEndState.option)
    }
    var onDateEndAt by remember(initialRepeatSchedule, initialOccurredAt) {
        mutableLongStateOf(initialRepeatEndState.endAtEpochMillis)
    }
    var afterMonths by remember(initialRepeatSchedule, initialOccurredAt) {
        mutableIntStateOf(initialRepeatEndState.afterMonths)
    }

    val onDone = {
        val resolvedEndAt = when {
            selectedEndOption == RepeatEndOption.NEVER -> Long.MAX_VALUE
            selectedEndOption == RepeatEndOption.ON_DATE -> onDateEndAt
            else -> Calendar.getInstance().apply {
                timeInMillis = startAt
                add(Calendar.MONTH, afterMonths.coerceAtLeast(MIN_REPEAT_MONTHS))
            }.timeInMillis
        }
        onDoneClick(true, selectedFrequency, resolvedEndAt, startAt)
    }
    val afterEndAt = remember(startAt, afterMonths) {
        Calendar.getInstance().apply {
            timeInMillis = startAt
            add(Calendar.MONTH, afterMonths.coerceAtLeast(MIN_REPEAT_MONTHS))
        }.timeInMillis
    }

    MoneyTrackScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = Dimens.spacing16),
        ) {
            Spacer(modifier = Modifier.height(Dimens.spacing16))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier
                        .size(Dimens.iconButtonSize)
                        .clickable(onClick = onBackClick),
                    shape = CircleShape,
                    color = ExpensePillBg,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = DsR.drawable.arrow_left),
                        contentDescription = null,
                        tint = ExpensePrimaryText,
                        modifier = Modifier
                            .padding(Dimens.spacing12)
                            .size(Dimens.icon20),
                    )
                }
                Text(
                    text = "Repeat transaction",
                    modifier = Modifier.weight(1f),
                    style = AppTheme.typography.titleMedium,
                    color = ExpensePrimaryText,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.width(Dimens.spacing48))
            }
            Spacer(modifier = Modifier.height(Dimens.spacing20))
            Spacer(modifier = Modifier.height(Dimens.spacing16))
            GlassSectionCard {
                RepeatFrequency.entries.forEachIndexed { index, frequency ->
                    RepeatOptionRow(
                        title = frequency.displayName(),
                        selected = selectedFrequency == frequency,
                        onClick = { selectedFrequency = frequency },
                    )
                    if (index < RepeatFrequency.entries.lastIndex) GlassDivider()
                }
            }
            Spacer(modifier = Modifier.height(Dimens.spacing12))
            Spacer(modifier = Modifier.height(Dimens.spacing8))
            GlassSectionCard {
                ExpenseRow(
                    iconRes = DsR.drawable.transaction,
                    title = formatDate(startAt),
                    subtitle = null,
                    trailingLabel = "Today",
                    onTrailingClick = {
                        context.showExpenseDatePicker(startAt) { updated -> startAt = updated }
                    },
                )
            }
            Spacer(modifier = Modifier.height(Dimens.spacing12))
            Spacer(modifier = Modifier.height(Dimens.spacing8))
            GlassSectionCard {
                RepeatOptionRow("Never", selectedEndOption == RepeatEndOption.NEVER) {
                    selectedEndOption = RepeatEndOption.NEVER
                }
                GlassDivider()
                RepeatOptionRow("On date", selectedEndOption == RepeatEndOption.ON_DATE) {
                    selectedEndOption = RepeatEndOption.ON_DATE
                }
                if (selectedEndOption == RepeatEndOption.ON_DATE) {
                    GlassDivider()
                    ExpenseRow(
                        iconRes = DsR.drawable.transaction,
                        title = formatDate(onDateEndAt),
                        subtitle = null,
                        trailingLabel = "Pick",
                        onTrailingClick = {
                            context.showExpenseDatePicker(onDateEndAt) { updated -> onDateEndAt = updated }
                        },
                    )
                }
                GlassDivider()
                RepeatOptionRow("After", selectedEndOption == RepeatEndOption.AFTER_MONTHS) {
                    selectedEndOption = RepeatEndOption.AFTER_MONTHS
                }
                if (selectedEndOption == RepeatEndOption.AFTER_MONTHS) {
                    GlassDivider()
                    ExpenseRow(
                        iconRes = DsR.drawable.transaction,
                        title = formatDate(afterEndAt),
                        subtitle = null,
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            LargeButton(
                text = stringResource(id = R.string.expense_continue),
                onClick = onDone,
                enabled = true,
            )
            Spacer(modifier = Modifier.height(Dimens.spacing8))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun RepeatOptionRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Dimens.spacing8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = AppTheme.typography.titleSmall,
            color = ExpensePrimaryText,
            modifier = Modifier.weight(1f),
        )
        Surface(
            modifier = Modifier.size(Dimens.icon24),
            shape = CircleShape,
            color = Color.Transparent,
            border = androidx.compose.foundation.BorderStroke(
                Dimens.borderNormal,
                if (selected) ExpenseAccent else ExpenseSecondaryText,
            ),
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .padding(Dimens.spacing4)
                        .background(ExpenseAccent, CircleShape),
                )
            }
        }
    }
}

@Composable
private fun RepeatSummary(
    repeatSchedule: ExpenseRepeatUiState,
    onEditClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(id = R.string.expense_repeat_frequency_label),
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.onBackground,
            )
            Text(
                text = repeatSchedule.frequency.displayName(),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(id = R.string.expense_repeat_end_after_label),
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.onBackground,
            )
            Text(
                text = formatDate(repeatSchedule.endAtEpochMillis),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant,
            )
        }
        Surface(
            modifier = Modifier.clickable(onClick = onEditClick),
            shape = RoundedCornerShape(Dimens.radius20),
            color = AppTheme.colors.primary.copy(alpha = SELECTED_ITEM_ALPHA),
        ) {
            Text(
                text = stringResource(id = R.string.expense_repeat_edit),
                modifier = Modifier.padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing8),
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.primary,
            )
        }
    }
}

@Composable
private fun RepeatConfigurationBottomSheet(
    initialRepeatSchedule: ExpenseRepeatUiState?,
    onDismiss: () -> Unit,
    onSave: (RepeatFrequency, Long) -> Unit,
) {
    val context = LocalContext.current
    var isFrequencyMenuExpanded by remember { mutableStateOf(false) }
    var selectedFrequency by remember(initialRepeatSchedule) {
        mutableStateOf(initialRepeatSchedule?.frequency)
    }
    var selectedEndDate by remember(initialRepeatSchedule) {
        mutableLongStateOf(initialRepeatSchedule?.endAtEpochMillis ?: 0L)
    }
    val hasEndDate = selectedEndDate != 0L

    MoneyTrackBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacing16)
                .padding(bottom = Dimens.spacing24),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(Dimens.spacing32)
                    .height(Dimens.spacing4)
                    .background(
                        color = AppTheme.colors.primary.copy(alpha = SELECTED_ITEM_ALPHA),
                        shape = RoundedCornerShape(Dimens.radius24),
                    ),
            )
            Spacer(modifier = Modifier.height(Dimens.spacing24))

            Box {
                RepeatSelectionField(
                    label = selectedFrequency?.displayName()
                        ?: stringResource(id = R.string.expense_repeat_frequency_label),
                    onClick = { isFrequencyMenuExpanded = true },
                )
                DropdownMenu(
                    expanded = isFrequencyMenuExpanded,
                    onDismissRequest = { isFrequencyMenuExpanded = false },
                ) {
                    RepeatFrequency.entries.forEach { frequency ->
                        DropdownMenuItem(
                            text = { Text(text = frequency.displayName()) },
                            onClick = {
                                selectedFrequency = frequency
                                isFrequencyMenuExpanded = false
                            },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.spacing16))

            RepeatSelectionField(
                label = if (hasEndDate) {
                    formatDate(selectedEndDate)
                } else {
                    stringResource(id = R.string.expense_repeat_end_after_label)
                },
                onClick = {
                    context.showRepeatEndDatePicker(initialDateMillis = selectedEndDate) { endAt ->
                        selectedEndDate = endAt
                    }
                },
            )

            Spacer(modifier = Modifier.height(Dimens.spacing24))
            LargeButton(
                text = stringResource(id = R.string.expense_repeat_next),
                onClick = {
                    val frequency = selectedFrequency ?: return@LargeButton
                    if (!hasEndDate) return@LargeButton
                    onSave(frequency, selectedEndDate)
                },
                enabled = selectedFrequency != null && hasEndDate,
            )
        }
    }
}

@Composable
private fun RepeatSelectionField(
    label: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.inputHeight)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Dimens.radius16),
        color = AppTheme.colors.background,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = Dimens.borderNormal,
                    color = AppTheme.colors.outline,
                    shape = RoundedCornerShape(Dimens.radius16),
                )
                .padding(horizontal = Dimens.spacing16),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = ImageVector.vectorResource(id = DsR.drawable.arrow_down_2),
                contentDescription = null,
                tint = AppTheme.colors.onSurfaceVariant,
            )
        }
    }
}

private fun categoryColor(colorHex: String): Color = runCatching {
    Color(colorHex.toColorInt())
}.getOrDefault(fallbackCategoryColor)

private fun Context.createCameraAttachmentUri(): Uri {
    val attachmentDirectory = File(cacheDir, ATTACHMENT_CACHE_DIR).apply {
        mkdirs()
    }
    val file = File.createTempFile(
        ATTACHMENT_FILE_PREFIX,
        ATTACHMENT_IMAGE_EXTENSION,
        attachmentDirectory,
    )
    return FileProvider.getUriForFile(
        this,
        "$packageName.fileprovider",
        file,
    )
}

private fun Context.resolveDisplayName(uri: Uri): String? {
    val cursor = contentResolver.query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME),
        null,
        null,
        null,
    ) ?: return null
    cursor.use {
        val hasRow = it.moveToFirst()
        val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        return if (hasRow && columnIndex != -1) {
            it.getString(columnIndex)
        } else {
            null
        }
    }
}

private fun Context.takePersistableReadPermission(uri: Uri) {
    runCatching {
        contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION,
        )
    }
}

private fun RepeatFrequency.displayName(): String = when (this) {
    RepeatFrequency.DAILY -> "Daily"
    RepeatFrequency.WEEKLY -> "Weekly"
    RepeatFrequency.MONTHLY -> "Monthly"
    RepeatFrequency.YEARLY -> "Yearly"
}

private data class InitialRepeatEndState(
    val option: RepeatEndOption,
    val endAtEpochMillis: Long,
    val afterMonths: Int,
)

private fun deriveInitialRepeatEndState(
    initialRepeatSchedule: ExpenseRepeatUiState?,
    initialOccurredAt: Long,
): InitialRepeatEndState {
    val repeatSchedule = initialRepeatSchedule
    val defaultState = InitialRepeatEndState(
        option = RepeatEndOption.NEVER,
        endAtEpochMillis = initialOccurredAt,
        afterMonths = DEFAULT_REPEAT_AFTER_MONTHS,
    )
    val endAtEpochMillis = repeatSchedule?.endAtEpochMillis ?: Long.MAX_VALUE
    val inferredAfterMonths = if (endAtEpochMillis == Long.MAX_VALUE) {
        null
    } else {
        inferAfterMonths(
            startAtEpochMillis = initialOccurredAt,
            endAtEpochMillis = endAtEpochMillis,
        )
    }
    return when {
        repeatSchedule == null || endAtEpochMillis == Long.MAX_VALUE -> defaultState
        inferredAfterMonths != null -> InitialRepeatEndState(
            option = RepeatEndOption.AFTER_MONTHS,
            endAtEpochMillis = endAtEpochMillis,
            afterMonths = inferredAfterMonths,
        )
        else -> InitialRepeatEndState(
            option = RepeatEndOption.ON_DATE,
            endAtEpochMillis = endAtEpochMillis,
            afterMonths = DEFAULT_REPEAT_AFTER_MONTHS,
        )
    }
}

private fun inferAfterMonths(
    startAtEpochMillis: Long,
    endAtEpochMillis: Long,
): Int? {
    val startCalendar = Calendar.getInstance().apply { timeInMillis = startAtEpochMillis }
    val endCalendar = Calendar.getInstance().apply { timeInMillis = endAtEpochMillis }
    val rawMonths = (endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)) * 12 +
        (endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH))
    if (rawMonths < MIN_REPEAT_MONTHS || rawMonths > MAX_REPEAT_MONTHS) return null

    val recalculatedCalendar = Calendar.getInstance().apply {
        timeInMillis = startAtEpochMillis
        add(Calendar.MONTH, rawMonths)
    }
    val dayDelta = kotlin.math.abs(recalculatedCalendar.timeInMillis - endAtEpochMillis) / MILLIS_PER_DAY
    return if (dayDelta <= REPEAT_END_MATCH_TOLERANCE_DAYS) rawMonths else null
}

private fun formatDate(epochMillis: Long): String =
    SimpleDateFormat(DATE_OUTPUT_PATTERN, Locale.getDefault()).format(epochMillis)

private fun Context.showRepeatEndDatePicker(
    initialDateMillis: Long,
    onDateSelected: (Long) -> Unit,
) {
    val initialCalendar = Calendar.getInstance().apply {
        timeInMillis = if (initialDateMillis > 0L) {
            initialDateMillis
        } else {
            System.currentTimeMillis()
        }
    }
    DatePickerDialog(
        this,
        { _, year, month, dayOfMonth ->
            val endCalendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                set(Calendar.HOUR_OF_DAY, END_OF_DAY_HOUR)
                set(Calendar.MINUTE, END_OF_DAY_MINUTE)
                set(Calendar.SECOND, END_OF_DAY_SECOND)
                set(Calendar.MILLISECOND, END_OF_DAY_MILLISECOND)
            }
            onDateSelected(endCalendar.timeInMillis)
        },
        initialCalendar.get(Calendar.YEAR),
        initialCalendar.get(Calendar.MONTH),
        initialCalendar.get(Calendar.DAY_OF_MONTH),
    ).show()
}

private fun Context.showExpenseDatePicker(
    currentEpochMillis: Long,
    onDateSelected: (Long) -> Unit,
) {
    val initialCalendar = Calendar.getInstance().apply {
        timeInMillis = currentEpochMillis
    }
    DatePickerDialog(
        this,
        { _, year, month, dayOfMonth ->
            val updated = Calendar.getInstance().apply {
                timeInMillis = currentEpochMillis
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
            onDateSelected(updated.timeInMillis)
        },
        initialCalendar.get(Calendar.YEAR),
        initialCalendar.get(Calendar.MONTH),
        initialCalendar.get(Calendar.DAY_OF_MONTH),
    ).show()
}

private fun Context.showExpenseTimePicker(
    currentEpochMillis: Long,
    onTimeSelected: (Long) -> Unit,
) {
    val initialCalendar = Calendar.getInstance().apply {
        timeInMillis = currentEpochMillis
    }
    TimePickerDialog(
        this,
        { _, hourOfDay, minute ->
            val updated = Calendar.getInstance().apply {
                timeInMillis = currentEpochMillis
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
            }
            onTimeSelected(updated.timeInMillis)
        },
        initialCalendar.get(Calendar.HOUR_OF_DAY),
        initialCalendar.get(Calendar.MINUTE),
        false,
    ).show()
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ExpenseScreenPreview() {
    MoneyTrackTheme(darkTheme = false) {
        ExpenseScreen(
            uiState = ExpenseUiState(
                amountInput = "",
                amountText = "$0",
                attachment = ExpenseAttachmentUiState(
                    uriString = "",
                    name = "Receipt.jpg",
                    type = ExpenseAttachmentType.DOCUMENT,
                ),
                repeatSchedule = ExpenseRepeatUiState(
                    frequency = RepeatFrequency.MONTHLY,
                    endAtEpochMillis = System.currentTimeMillis(),
                ),
                categories = listOf(
                    ExpenseCategory(
                        id = 1L,
                        name = "Subscription",
                        colorHex = "#2AB784",
                        sortOrder = 0,
                        isDefault = true,
                    ),
                    ExpenseCategory(
                        id = 2L,
                        name = "Food",
                        colorHex = "#FD3C4A",
                        sortOrder = 1,
                        isDefault = true,
                    ),
                ),
                selectedCategoryId = 1L,
                selectedCategory = ExpenseCategory(
                    id = 1L,
                    name = "Subscription",
                    colorHex = "#2AB784",
                    sortOrder = 0,
                    isDefault = true,
                ),
            ),
            onBackClick = {},
            onContinueClick = {},
            onAmountChanged = {},
            onDescriptionChanged = {},
            onAttachmentSelected = { _, _, _ -> },
            onAttachmentRemoved = {},
            onRepeatConfigured = { _, _ -> },
            onRepeatRemoved = {},
            onCategorySelected = {},
            onOccurredAtChanged = {},
        )
    }
}
