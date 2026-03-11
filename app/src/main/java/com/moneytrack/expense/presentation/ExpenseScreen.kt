// Copyright (c) 2026 shyakdas

@file:Suppress("LongMethod", "UnusedPrivateMember")

package com.moneytrack.expense.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.moneytrack.R
import com.moneytrack.designsystem.R as DsR
import ui.components.form.control.PrimarySwitch
import ui.components.form.input.InputField
import ui.components.navigation.button.LargeButton
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
fun ExpenseScreen(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
) {
    var isRepeatEnabled by remember { mutableStateOf(false) }

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
            Text(
                text = stringResource(id = R.string.expense_amount_default),
                style = AppTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.onPrimary,
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
                    .padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing24),
            ) {
                InputField(
                    value = "",
                    onValueChange = {},
                    placeholder = stringResource(id = R.string.expense_category_placeholder),
                    trailingIcon = ImageVector.vectorResource(id = DsR.drawable.arrow_down_2),
                )
                Spacer(modifier = Modifier.height(Dimens.spacing16))

                InputField(
                    value = "",
                    onValueChange = {},
                    placeholder = stringResource(id = R.string.expense_description_placeholder),
                )
                Spacer(modifier = Modifier.height(Dimens.spacing16))

                InputField(
                    value = "",
                    onValueChange = {},
                    placeholder = stringResource(id = R.string.expense_wallet_placeholder),
                    trailingIcon = ImageVector.vectorResource(id = DsR.drawable.arrow_down_2),
                )
                Spacer(modifier = Modifier.height(Dimens.spacing16))

                AttachmentInput()
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
                        onCheckedChange = { isRepeatEnabled = it },
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                LargeButton(
                    text = stringResource(id = R.string.expense_continue),
                    onClick = onContinueClick,
                )
                Spacer(modifier = Modifier.height(Dimens.spacing8).navigationBarsPadding())
            }
        }
    }
}

@Composable
private fun AttachmentInput() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.inputHeight)
            .border(
                width = Dimens.borderNormal,
                color = AppTheme.colors.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(Dimens.radius16),
            )
            .padding(horizontal = Dimens.spacing16),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = DsR.drawable.attachment),
            contentDescription = null,
            tint = AppTheme.colors.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.size(Dimens.spacing12))
        Text(
            text = stringResource(id = R.string.expense_add_attachment),
            style = AppTheme.typography.bodyLarge,
            color = AppTheme.colors.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ExpenseScreenPreview() {
    MoneyTrackTheme(darkTheme = false) {
        ExpenseScreen(
            onBackClick = {},
            onContinueClick = {},
        )
    }
}
