// Copyright (c) 2026 shyakdas

package com.moneytrack.home.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import ui.theme.AppTheme

@Composable
internal fun SpendFrequencyChart(
    points: List<Float>,
    modifier: Modifier = Modifier,
) {
    val chartLineColor = AppTheme.colors.primary
    val chartFillColor = AppTheme.colors.primary.copy(alpha = 0.15f)
    val chartGridColor = AppTheme.colors.outline.copy(alpha = 0.15f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(170.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (points.isEmpty()) {
                drawChartGrid(chartGridColor)
                return@Canvas
            }

            val normalizedPoints = points.normalizeForChart()
            drawChart(
                points = normalizedPoints.toChartPoints(this),
                lineColor = chartLineColor,
                fillColor = chartFillColor,
                gridColor = chartGridColor,
            )
        }
    }
}

private fun DrawScope.drawChart(
    points: List<Offset>,
    lineColor: androidx.compose.ui.graphics.Color,
    fillColor: androidx.compose.ui.graphics.Color,
    gridColor: androidx.compose.ui.graphics.Color,
) {
    val linePath = points.toLinePath()
    val fillPath = Path().apply {
        addPath(linePath)
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()
    }

    drawPath(path = fillPath, color = fillColor)
    drawPath(
        path = linePath,
        color = lineColor,
        style = Stroke(
            width = CHART_LINE_WIDTH.dp.toPx(),
            cap = StrokeCap.Round,
            pathEffect = PathEffect.cornerPathEffect(CHART_CORNER_RADIUS),
        ),
    )
    drawChartGrid(gridColor)
}

private fun DrawScope.drawChartGrid(color: androidx.compose.ui.graphics.Color) {
    drawRect(
        color = color,
        size = Size(width = size.width, height = 1.dp.toPx()),
    )
}

private fun List<Float>.toChartPoints(drawScope: DrawScope): List<Offset> = mapIndexed { index, value ->
    val x = if (size == 1) {
        drawScope.size.width / 2f
    } else {
        drawScope.size.width * index / (lastIndex.coerceAtLeast(1))
    }
    Offset(x, drawScope.size.height * value)
}.let { generated ->
    if (generated.size == 1) {
        listOf(
            Offset(0f, generated.first().y),
            Offset(drawScope.size.width, generated.first().y),
        )
    } else {
        generated
    }
}

private fun List<Offset>.toLinePath(): Path = Path().apply {
    moveTo(first().x, first().y)
    for (index in 1 until size) {
        val prev = get(index - 1)
        val current = get(index)
        val cx = (prev.x + current.x) / 2
        cubicTo(cx, prev.y, cx, current.y, current.x, current.y)
    }
}

private fun List<Float>.normalizeForChart(): List<Float> {
    val maxValue = maxOrNull()?.takeIf { value -> value > 0f } ?: return map { CHART_BASELINE_RATIO }
    return map { value ->
        val normalized = 1f - (value / maxValue) * CHART_HEIGHT_RATIO
        normalized.coerceIn(CHART_TOP_PADDING_RATIO, CHART_BASELINE_RATIO)
    }
}

private const val CHART_TOP_PADDING_RATIO = 0.12f
private const val CHART_BASELINE_RATIO = 0.88f
private const val CHART_HEIGHT_RATIO = 0.76f
private const val CHART_LINE_WIDTH = 5
private const val CHART_CORNER_RADIUS = 30f
