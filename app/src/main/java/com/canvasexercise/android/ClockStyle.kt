package com.canvasexercise.android

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ClockStyle(
    val background: Color = Color.White,
    val radius: Dp = 100.dp,
    val minuteIndicatorLength: Dp = 8.dp,
    val minuteIndicatorWidth: Dp = 2.dp,
    val minuteIndicatorColor: Color = Color.Gray.copy(alpha = 0.3f),
    val hourIndicatorLength: Dp = 12.dp,
    val hourIndicatorWidth: Dp = 3.dp,
    val hourIndicatorColor: Color = Color.Gray.copy(alpha = 0.85f),
    val secondIndicatorColor: Color = Color.Red.copy(alpha = 0.7f)
) {
}
