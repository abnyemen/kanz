package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.Vector3
import com.example.engine.WorldEntity
import com.example.ui.theme.DesertGold
import com.example.ui.theme.DesertObsidian
import com.example.ui.theme.OasisTeal
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * MiniMap UI Component
 * Renders a top-down radar view of the player's immediate surroundings in the desert world.
 * Displays points of interest including Temples, Oases, Chests, Mounts, and Enemies using Canvas.
 */
@Composable
fun MiniMap(
    playerPos: Vector3,
    playerYaw: Float,
    entities: List<WorldEntity>,
    onClickMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "poi_pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "poi_pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "poi_pulse_alpha"
    )

    Box(
        modifier = modifier
            .size(110.dp)
            .clip(CircleShape)
            .background(DesertObsidian.copy(alpha = 0.85f))
            .border(2.dp, DesertGold, CircleShape)
            .clickable { onClickMap() }
            .testTag("minimap_widget")
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radarRadius = size.width / 2f - 4f
            val scale = radarRadius / 100f // 100m detection range

            // Background radar & Grid
            drawCircle(color = Color(0xFF1E1107), radius = radarRadius)
            drawCircle(color = DesertGold.copy(alpha = 0.2f), radius = radarRadius * 0.5f, style = Stroke(1f))
            drawCircle(color = DesertGold.copy(alpha = 0.4f), radius = radarRadius, style = Stroke(2f))

            // Crosshair lines
            drawLine(
                color = DesertGold.copy(alpha = 0.15f),
                start = Offset(center.x, center.y - radarRadius),
                end = Offset(center.x, center.y + radarRadius),
                strokeWidth = 1f
            )
            drawLine(
                color = DesertGold.copy(alpha = 0.15f),
                start = Offset(center.x - radarRadius, center.y),
                end = Offset(center.x + radarRadius, center.y),
                strokeWidth = 1f
            )

            // Render Points of Interest (Temples, Oases, Enemies, Chests, Mounts)
            for (entity in entities) {
                val relX = entity.position.x - playerPos.x
                val relZ = entity.position.z - playerPos.z

                val mapX = center.x + relX * scale
                val mapY = center.y + relZ * scale

                val distFromCenter = Offset(mapX, mapY).distanceTo(center)
                if (distFromCenter <= radarRadius - 6f) {
                    val color = when (entity.type) {
                        "key_altar", "temple_door" -> DesertGold // Temples & Sacred Gates
                        "water_well" -> OasisTeal // Oases & Water Wells
                        "chest" -> Color(0xFFFFD700) // Treasure Chests
                        "camel", "horse" -> Color.White // Mounts
                        "mummy", "bandit", "boss_anubis" -> Color(0xFFFF4444) // Enemies & Bosses
                        else -> Color.Yellow
                    }
                    val baseRadius = if (entity.type == "key_altar" || entity.type == "temple_door") 5.5f else 3.5f

                    // Pulsing outer aura ring
                    drawCircle(
                        color = color.copy(alpha = pulseAlpha),
                        radius = baseRadius * pulseScale * 1.6f,
                        center = Offset(mapX, mapY)
                    )
                    // Solid core marker
                    drawCircle(
                        color = color,
                        radius = baseRadius * pulseScale,
                        center = Offset(mapX, mapY)
                    )
                }
            }

            // Player Direction Triangle Marker
            val radYaw = Math.toRadians(playerYaw.toDouble()).toFloat()
            val arrowLength = 12f
            val arrowWidth = 7f

            val tipX = center.x - sin(radYaw) * arrowLength
            val tipY = center.y - cos(radYaw) * arrowLength

            val leftX = center.x + cos(radYaw) * arrowWidth + sin(radYaw) * (arrowLength * 0.4f)
            val leftY = center.y - sin(radYaw) * arrowWidth + cos(radYaw) * (arrowLength * 0.4f)

            val rightX = center.x - cos(radYaw) * arrowWidth + sin(radYaw) * (arrowLength * 0.4f)
            val rightY = center.y + sin(radYaw) * arrowWidth + cos(radYaw) * (arrowLength * 0.4f)

            val path = Path().apply {
                moveTo(tipX, tipY)
                lineTo(leftX, leftY)
                lineTo(rightX, rightY)
                close()
            }

            drawPath(path = path, color = Color.Cyan)
            drawCircle(color = Color.White, radius = 2.5f, center = center)
        }

        // Compass North Cardinal Label
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 2.dp)
        ) {
            Text(
                text = "N",
                color = DesertGold,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Mini Map Label
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 2.dp)
        ) {
            Text(
                text = "MAP",
                color = DesertGold.copy(alpha = 0.8f),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Alias for backwards compatibility with existing views
@Composable
fun MinimapView(
    playerPos: Vector3,
    playerYaw: Float,
    entities: List<WorldEntity>,
    onClickMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    MiniMap(
        playerPos = playerPos,
        playerYaw = playerYaw,
        entities = entities,
        onClickMap = onClickMap,
        modifier = modifier
    )
}

private fun Offset.distanceTo(other: Offset): Float {
    val dx = x - other.x
    val dy = y - other.y
    return sqrt(dx * dx + dy * dy)
}
