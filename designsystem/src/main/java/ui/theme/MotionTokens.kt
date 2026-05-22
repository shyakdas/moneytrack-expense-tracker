// Copyright (c) 2026 shyakdas

package ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

object MotionTokens {
    const val DurationShort = 150
    const val DurationMedium = 220
    const val DurationLong = 300

    const val PressedScale = 0.97f
    const val SelectedScale = 1.08f

    val EmphasizedEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val StandardEasing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)

    fun <T> standardTween(durationMillis: Int = DurationMedium) =
        tween<T>(
            durationMillis = durationMillis,
            easing = StandardEasing,
        )

    fun <T> emphasizedTween(durationMillis: Int = DurationLong) =
        tween<T>(
            durationMillis = durationMillis,
            easing = EmphasizedEasing,
        )

    fun <T> pressSpring() =
        spring<T>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        )
}
