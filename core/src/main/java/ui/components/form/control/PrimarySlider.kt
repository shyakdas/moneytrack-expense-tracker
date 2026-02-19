package ui.components.form.control

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.theme.Violet100

@Composable
fun PrimarySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        Box {
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = Violet100,
                    activeTrackColor = Violet100,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            Box(
                modifier = Modifier
                    .offset(x = (value / 100f * 260).dp) // visually tuned
                    .background(Violet100, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${value.toInt()}%",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(
    name = "Primary Slider",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun PrimarySliderPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            PrimarySlider(
                value = 0f,
                onValueChange = {}
            )

            PrimarySlider(
                value = 50f,
                onValueChange = {}
            )

            PrimarySlider(
                value = 90f,
                onValueChange = {}
            )
        }
    }
}
