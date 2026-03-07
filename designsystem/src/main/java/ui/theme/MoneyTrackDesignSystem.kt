// Copyright (c) 2026 shyakdas

package ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

object MoneyTrackTheme {

    val colors
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    val typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    val shapes
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes
}
