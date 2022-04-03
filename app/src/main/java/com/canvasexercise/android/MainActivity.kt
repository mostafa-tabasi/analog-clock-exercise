package com.canvasexercise.android

import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.util.*
import kotlin.math.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnalogClock()
        }
    }
}

@Composable
fun AnalogClock(style: ClockStyle = ClockStyle()) {
    var currentTime by remember { mutableStateOf(Calendar.getInstance()) }
    var second by remember { mutableStateOf(0) }
    // update time every one second
    LaunchedEffect(key1 = second) {
        delay(1000)
        currentTime = Calendar.getInstance()
        second++
    }
    // second counter offset animation
    val secondCounterEnd by animateOffsetAsState(
        calculateSecondCounterEndPath(
            currentTime = currentTime, style = style
        ), animationSpec = TweenSpec(durationMillis = 1000, easing = LinearEasing)
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray)
    ) {
        // draw background
        drawContext.canvas.nativeCanvas.drawCircle(
            center.x,
            center.y,
            style.radius.toPx(),
            Paint().apply {
                color = android.graphics.Color.WHITE
                setShadowLayer(50.dp.toPx(), 0f, 0f, android.graphics.Color.DKGRAY)
                isAntiAlias = true
            })
        // draw indicators
        for (i in 1..60) {
            val indicatorType =
                if (i % 5 == 0) ClockIndicatorType.HOUR else ClockIndicatorType.MINUTE
            val convertedIndex = (i * 360) / 60
            val indicatorAngleInRad = (convertedIndex - 90) * (PI / 180f)
            val indicatorLineStart =
                Offset(
                    x = (style.radius.toPx() * cos(indicatorAngleInRad) + center.x).toFloat(),
                    y = (style.radius.toPx() * sin(indicatorAngleInRad) + center.y).toFloat()
                )
            val indicatorLength = when (indicatorType) {
                ClockIndicatorType.MINUTE -> style.minuteIndicatorLength.toPx()
                ClockIndicatorType.HOUR -> style.hourIndicatorLength.toPx()
            }
            val indicatorLineEnd =
                Offset(
                    x = ((style.radius.toPx() - indicatorLength) *
                            cos(indicatorAngleInRad) + center.x).toFloat(),
                    y = ((style.radius.toPx() - indicatorLength) *
                            sin(indicatorAngleInRad) + center.y).toFloat()
                )
            val indicatorColor = when (indicatorType) {
                ClockIndicatorType.MINUTE -> style.minuteIndicatorColor
                ClockIndicatorType.HOUR -> style.hourIndicatorColor
            }
            drawLine(
                color = indicatorColor,
                start = indicatorLineStart,
                end = indicatorLineEnd,
                strokeWidth = style.minuteIndicatorWidth.toPx()
            )
        }
        // draw second counter indicator with animation
        drawLine(
            color = style.secondIndicatorColor,
            start = center,
            end = Offset(secondCounterEnd.x + center.x, secondCounterEnd.y + center.y),
            strokeWidth = 1.dp.toPx()
        )
        // draw minute counter indicator
        drawCounter(
            currentTime,
            `for` = Calendar.MINUTE,
            length = style.radius.minus(style.hourIndicatorLength).minus(15.dp),
            color = style.minuteIndicatorColor,
            strokeWidth = style.minuteIndicatorWidth
        )
        // draw hour counter indicator
        drawCounter(
            currentTime,
            `for` = Calendar.HOUR,
            length = style.radius.minus(style.hourIndicatorLength).minus(30.dp),
            color = style.hourIndicatorColor,
            strokeWidth = style.hourIndicatorWidth
        )

        drawCircle(color = Black, radius = 5.dp.toPx(), center = center)
    }
}

private fun DrawScope.drawCounter(
    calendar: Calendar,
    `for`: Int,
    length: Dp,
    color: Color,
    strokeWidth: Dp,
) {
    // draw time indicator
    // calculate current time
    val time = calendar.get(`for`)
    val criterionForConvert = if (`for` == Calendar.HOUR) 12 else 60
    val hourOffset: Float = if (`for` == Calendar.HOUR) calendar.get(Calendar.MINUTE) / 60F else 0F
    val convertedTime = ((time + hourOffset) * 360) / criterionForConvert
    val timeCounterAngleInRad = (convertedTime - 90) * (PI / 180f)
    val timeCounterEnd = Offset(
        x = (length.toPx() * cos(timeCounterAngleInRad) + center.x).toFloat(),
        y = (length.toPx() * sin(timeCounterAngleInRad) + center.y).toFloat()
    )
    drawLine(
        color = color,
        start = center,
        end = timeCounterEnd,
        strokeWidth = strokeWidth.toPx()
    )
}

@Composable
private fun calculateSecondCounterEndPath(
    currentTime: Calendar,
    style: ClockStyle,
): Offset {
    val time = currentTime.get(Calendar.SECOND)
    val criterionForConvert = 60
    val convertedTime = (time * 360) / criterionForConvert
    val timeCounterAngleInRad = (convertedTime - 90) * (PI / 180f)
    val lengthInPx = with(LocalDensity.current) {
        style.radius.minus(style.hourIndicatorLength).minus(5.dp).toPx()
    }
    return Offset(
        x = (lengthInPx * cos(timeCounterAngleInRad)).toFloat(),
        y = (lengthInPx * sin(timeCounterAngleInRad)).toFloat()
    )
}