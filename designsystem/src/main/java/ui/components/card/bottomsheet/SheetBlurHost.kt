// Copyright (c) 2026 shyakdas

package ui.components.card.bottomsheet

import androidx.compose.animation.core.animateFloatAsState
import ui.theme.MotionTokens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SheetBlurHost(
    isSheetVisible: Boolean,
    modifier: Modifier = Modifier,
    blurRadius: Dp = 12.dp,
    scrimColor: Color = Color.Black.copy(alpha = 0.22f),
    content: @Composable () -> Unit,
) {
    val blurProgress = animateFloatAsState(
        targetValue = if (isSheetVisible) 1f else 0f,
        animationSpec = MotionTokens.emphasizedTween(),
        label = "SheetBlurProgress",
    )

    Box(modifier = modifier) {
        val activeBlurRadius = blurRadius * blurProgress.value
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(activeBlurRadius),
        ) {
            content()
        }

        if (blurProgress.value > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(scrimColor.copy(alpha = scrimColor.alpha * blurProgress.value)),
            )
        }
    }
}
