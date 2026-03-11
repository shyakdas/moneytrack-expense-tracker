// Copyright (c) 2026 shyakdas

@file:Suppress("LongMethod", "UnusedPrivateMember", "LongParameterList", "TooManyFunctions")

package com.moneytrack.expense.presentation

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.FileProvider
import com.moneytrack.R
import com.moneytrack.designsystem.R as DsR
import com.moneytrack.expense.domain.model.ExpenseCategory
import java.io.File
import ui.components.card.bottomsheet.SheetBlurHost
import ui.components.form.control.PrimarySwitch
import ui.components.form.input.InputField
import ui.components.navigation.button.LargeButton
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

private const val CATEGORY_SHEET_VISIBLE_ROWS = 5
private const val FALLBACK_CATEGORY_COLOR_HEX = "#7F3DFF"
private const val SELECTED_ITEM_ALPHA = 0.12f
private const val ATTACHMENT_IMAGE_EXTENSION = ".jpg"
private const val ATTACHMENT_CACHE_DIR = "expense_attachments"
private const val ATTACHMENT_FILE_PREFIX = "receipt_"
private val fallbackCategoryColor = categoryColor(FALLBACK_CATEGORY_COLOR_HEX)

@Composable
fun ExpenseScreen(
    uiState: ExpenseUiState,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    onAmountChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onAttachmentSelected: (String, String, ExpenseAttachmentType) -> Unit,
    onAttachmentRemoved: () -> Unit,
    onCategorySelected: (Long) -> Unit,
) {
    val context = LocalContext.current
    var isRepeatEnabled by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
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

    SheetBlurHost(isSheetVisible = showCategorySheet || showAttachmentSheet) {
        ExpenseContent(
            amountInput = uiState.amountInput,
            amountText = uiState.amountText,
            description = uiState.description,
            attachment = uiState.attachment,
            selectedCategory = uiState.selectedCategory,
            isRepeatEnabled = isRepeatEnabled,
            onRepeatChanged = { isRepeatEnabled = it },
            onBackClick = onBackClick,
            onContinueClick = onContinueClick,
            onAmountChanged = onAmountChanged,
            onDescriptionChanged = onDescriptionChanged,
            onAttachmentClick = { showAttachmentSheet = true },
            onAttachmentRemoved = onAttachmentRemoved,
            onCategoryFieldClick = { showCategorySheet = true },
        )
    }

    if (showCategorySheet) {
        CategoryPickerBottomSheet(
            uiState = uiState,
            onDismiss = { showCategorySheet = false },
            onCategorySelected = { categoryId ->
                onCategorySelected(categoryId)
                showCategorySheet = false
            },
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
}

@Composable
private fun ExpenseContent(
    amountInput: String,
    amountText: String,
    description: String,
    attachment: ExpenseAttachmentUiState?,
    selectedCategory: ExpenseCategory?,
    isRepeatEnabled: Boolean,
    onRepeatChanged: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    onAmountChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onAttachmentClick: () -> Unit,
    onAttachmentRemoved: () -> Unit,
    onCategoryFieldClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.error),
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = Dimens.spacing24),
        ) {
            Spacer(modifier = Modifier.height(Dimens.spacing12))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = DsR.drawable.arrow_left),
                        contentDescription = stringResource(id = R.string.expense_back_content_desc),
                        tint = AppTheme.colors.onPrimary,
                    )
                }

                Text(
                    text = stringResource(id = R.string.expense_title),
                    modifier = Modifier.weight(1f),
                    style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = AppTheme.colors.onPrimary,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.size(Dimens.iconButtonSize))
            }

            Spacer(modifier = Modifier.height(Dimens.spacing24))
            Text(
                text = stringResource(id = R.string.expense_amount_prompt),
                style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.onPrimary.copy(alpha = 0.85f),
            )
            Spacer(modifier = Modifier.height(Dimens.spacing8))
            AmountInputField(
                amountInput = amountInput,
                amountText = amountText,
                onAmountChanged = onAmountChanged,
            )
            Spacer(modifier = Modifier.height(Dimens.spacing24))
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = AppTheme.colors.background,
            shape = RoundedCornerShape(topStart = Dimens.radius40, topEnd = Dimens.radius40),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing24),
                ) {
                    CategorySelectorField(
                        selectedCategory = selectedCategory,
                        onClick = onCategoryFieldClick,
                    )
                    Spacer(modifier = Modifier.height(Dimens.spacing16))

                    InputField(
                        value = description,
                        onValueChange = onDescriptionChanged,
                        placeholder = stringResource(id = R.string.expense_description_placeholder),
                    )
                    Spacer(modifier = Modifier.height(Dimens.spacing16))

                    if (attachment == null) {
                        AttachmentInput(onClick = onAttachmentClick)
                    } else {
                        AttachmentPreview(
                            attachment = attachment,
                            onRemoveClick = onAttachmentRemoved,
                        )
                    }
                    Spacer(modifier = Modifier.height(Dimens.spacing24))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(id = R.string.expense_repeat_title),
                                style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = AppTheme.colors.onBackground,
                            )
                            Text(
                                text = stringResource(id = R.string.expense_repeat_subtitle),
                                style = AppTheme.typography.bodySmall,
                                color = AppTheme.colors.onSurfaceVariant,
                            )
                        }

                        PrimarySwitch(
                            checked = isRepeatEnabled,
                            onCheckedChange = onRepeatChanged,
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.spacing24))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.spacing16)
                        .padding(bottom = Dimens.spacing8),
                ) {
                    LargeButton(
                        text = stringResource(id = R.string.expense_continue),
                        onClick = onContinueClick,
                    )
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
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
                    text = amountText,
                    style = AppTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                    color = AppTheme.colors.onPrimary,
                )
            },
        )
    }
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
                .padding(horizontal = Dimens.spacing12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (selectedCategory == null) {
                Text(
                    text = stringResource(id = R.string.expense_category_placeholder),
                    style = AppTheme.typography.bodyLarge,
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
        color = AppTheme.colors.surface,
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
                style = AppTheme.typography.bodyLarge,
                color = AppTheme.colors.onSurface,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryPickerBottomSheet(
    uiState: ExpenseUiState,
    onDismiss: () -> Unit,
    onCategorySelected: (Long) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AppTheme.colors.surface,
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
            context.contentResolver.openInputStream(Uri.parse(uriString))?.use { inputStream ->
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttachmentPickerBottomSheet(
    onDismiss: () -> Unit,
    onCameraClick: () -> Unit,
    onImageClick: () -> Unit,
    onDocumentClick: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AppTheme.colors.surface,
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

private fun categoryColor(colorHex: String): Color = runCatching {
    Color(android.graphics.Color.parseColor(colorHex))
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
            onCategorySelected = {},
        )
    }
}
