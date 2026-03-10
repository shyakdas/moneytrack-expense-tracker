// Copyright (c) 2026 shyakdas

package ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

object AppTheme {

    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current

    val typography
        @Composable
        @ReadOnlyComposable
        get() = Typography

    val dimens
        @Composable
        @ReadOnlyComposable
        get() = Dimens
}
