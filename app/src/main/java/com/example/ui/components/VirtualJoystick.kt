package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DesertGold
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun VirtualJoystick(
    modifier: Modifier = Modifier,
    radiusDp: Float = 70f,
    onMove: (dx: Float, dy: Float) -> Unit
) {
    var knobOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .size((radiusDp * 2).dp)
            .testTag("virtual_joystick")
            .pointerInput(radiusDp) {
                detectDragGestures(
                    onDragEnd = {
                        knobOffset = Offset.Zero
                        onMove(0f, 0f)
                    },
                    onDragCancel = {
                        knobOffset = Offset.Zero
                        onMove(0f, 0f)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val rawNewOffset = knobOffset + dragAmount
                        val maxDist = radiusDp * density
                        val dist = sqrt(rawNewOffset.x * rawNewOffset.x + rawNewOffset.y * rawNewOffset.y)

                        knobOffset = if (dist > maxDist) {
                            val angle = atan2(rawNewOffset.y, rawNewOffset.x)
                            Offset(cos(angle) * maxDist, sin(angle) * maxDist)
                        } else {
                            rawNewOffset
                        }

                        val normalizedX = knobOffset.x / maxDist
                        val normalizedY = -knobOffset.y / maxDist // Invert Y for forward direction
                        onMove(normalizedX, normalizedY)
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.width / 2f
            val knobRadius = outerRadius * 0.35f

            // Outer ring
            drawCircle(
                color = Color.Black.copy(alpha = 0.4f),
                radius = outerRadius,
                center = center
            )
            drawCircle(
                color = DesertGold.copy(alpha = 0.6f),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 4f)
            )

            // Inner Knob
            val currentKnobCenter = center + knobOffset
            drawCircle(
                color = DesertGold.copy(alpha = 0.85f),
                radius = knobRadius,
                center = currentKnobCenter
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                radius = knobRadius * 0.4f,
                center = currentKnobCenter
            )
        }
    }
}
