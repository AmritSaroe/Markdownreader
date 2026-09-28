package com.markdownreader.app

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MarkoIcon: ImageVector
    get() = ImageVector.Builder(
        name = "MarkoIcon",
        defaultWidth = 312.dp,
        defaultHeight = 272.dp,
        viewportWidth = 512f,
        viewportHeight = 512f
    ).apply {
        // Rect 1
        path(fill = SolidColor(Color.White)) {
            moveTo(142f, 150f)
            lineTo(174f, 150f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 184f, 160f)
            lineTo(184f, 352f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 174f, 362f)
            lineTo(142f, 362f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 132f, 352f)
            lineTo(132f, 160f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 142f, 150f)
            close()
        }
        // Rect 2
        path(fill = SolidColor(Color.White)) {
            moveTo(338f, 150f)
            lineTo(370f, 150f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 380f, 160f)
            lineTo(380f, 352f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 370f, 362f)
            lineTo(338f, 362f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 328f, 352f)
            lineTo(328f, 160f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 338f, 150f)
            close()
        }
        // Chevron Path
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = 52f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(184f, 190f)
            lineTo(256f, 288f)
            lineTo(328f, 190f)
        }
    }.build()
