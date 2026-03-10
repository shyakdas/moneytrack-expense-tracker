// Copyright (c) 2026 shyakdas

package ui.components.form.control

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import ui.theme.AppTheme
import ui.theme.Dimens

@Composable
fun PrimarySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderWidthPx by remember { mutableStateOf(0) }
    val density = LocalDensity.current

    Column(modifier = modifier.fillMaxWidth()) {

        Box {
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = AppTheme.colors.primary,
                    activeTrackColor = AppTheme.colors.primary,
                    inactiveTrackColor = AppTheme.colors.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        sliderWidthPx = it.size.width
                    }
            )

            if (sliderWidthPx > 0) {
                val offsetDp = with(density) {
                    ((value / 100f) * sliderWidthPx).toDp()
                }

                Box(
                    modifier = Modifier
                        .offset(x = offsetDp - Dimens.sliderBubbleOffset)
                        .background(
                            AppTheme.colors.primary,
                            RoundedCornerShape(Dimens.radius16)
                        )
                        .padding(
                            horizontal = Dimens.spacing12,
                            vertical = Dimens.spacing6
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${value.toInt()}%",
                        color = AppTheme.colors.onPrimary,
                        style = AppTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview(name = "Primary Slider – Light & Dark")
@Composable
private fun PrimarySliderPreview() {

    Column {

        ui.theme.MoneyTrackTheme(darkTheme = false) {
            SliderPreviewContent()
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        ui.theme.MoneyTrackTheme(darkTheme = true) {
            SliderPreviewContent()
        }
    }
}

@Composable
private fun SliderPreviewContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.background)
            .padding(Dimens.spacing16),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing24)
    ) {

        PrimarySlider(value = 0f, onValueChange = {})
        PrimarySlider(value = 50f, onValueChange = {})
        PrimarySlider(value = 90f, onValueChange = {})
    }
}
