// Copyright (c) 2026 shyakdas

package ui.components.surface

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.theme.AppTheme
import ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoneyTrackBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(
            topStart = Dimens.radius24,
            topEnd = Dimens.radius24,
        ),
        containerColor = AppTheme.colors.surface,
        contentColor = AppTheme.colors.onSurface,
        tonalElevation = Dimens.elevation8,
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(5.dp)
                        .background(
                            color = AppTheme.colors.outline.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(Dimens.radius40),
                        ),
                )
            }
        },
        modifier = modifier,
    ) {
        content()
    }
}
