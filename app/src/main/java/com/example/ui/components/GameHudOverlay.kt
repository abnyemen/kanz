package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UnlockedTemple
import com.example.game.ActiveDialogType
import com.example.game.AppLanguage
import com.example.game.GameUiState
import com.example.ui.theme.*

@Composable
fun GameHudOverlay(
    uiState: GameUiState,
    unlockedTemples: List<UnlockedTemple> = emptyList(),
    onMoveInput: (dx: Float, dy: Float) -> Unit,
    onAttack: () -> Unit,
    onJump: () -> Unit,
    onRoll: () -> Unit,
    onToggleTorch: () -> Unit,
    onDrinkWater: () -> Unit,
    onInteract: () -> Unit,
    onOpenDialog: (ActiveDialogType) -> Unit,
    onToggleLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = uiState.language == AppLanguage.ARABIC

    Box(modifier = modifier.fillMaxSize()) {

        // --- SCREEN EDGE VIGNETTE WARNING (PULSING FOR LOW HEALTH / LOW STAMINA) ---
        CriticalWarningVignette(
            health = uiState.health,
            stamina = uiState.stamina
        )

        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {

            // --- TOP BAR: STATUS BARS & MENU BUTTONS ---
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Player Vital Bars
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Health Bar
                        StatProgressBar(
                            icon = Icons.Default.Favorite,
                            label = if (isAr) "الصحة" else "HP",
                            value = uiState.health,
                            maxValue = 100,
                            color = DesertCrimson,
                            isCritical = uiState.health <= 30
                        )
                        // Hydration Bar
                        StatProgressBar(
                            icon = Icons.Default.WaterDrop,
                            label = if (isAr) "الارتواء" else "Water",
                            value = uiState.hydration,
                            maxValue = 100,
                            color = OasisTeal,
                            isCritical = uiState.hydration <= 25
                        )
                        // Stamina Bar
                        StatProgressBar(
                            icon = Icons.Default.Bolt,
                            label = if (isAr) "التحمل" else "Stamina",
                            value = uiState.stamina,
                            maxValue = 100,
                            color = DesertGold,
                            isCritical = uiState.stamina <= 20
                        )

                        // Biome Location Chip
                        Surface(
                            color = DesertObsidian.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, DesertGold.copy(alpha = 0.6f)),
                            modifier = Modifier.padding(top = 2.dp).testTag("hud_biome_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = DesertGold,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isAr) uiState.currentBiomeAr else uiState.currentBiomeEn,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Floating Objective HUD Widget
                        ObjectiveHudWidget(
                            language = uiState.language,
                            keysCollectedCount = uiState.keysCollectedCount,
                            unlockedTemples = unlockedTemples,
                            currentBiomeEn = uiState.currentBiomeEn,
                            currentBiomeAr = uiState.currentBiomeAr
                        )
                    }

                    // Top Right Quick Controls
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Language Switcher
                        IconButton(
                            onClick = onToggleLanguage,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(DesertObsidian.copy(alpha = 0.8f))
                                .border(1.dp, DesertGold, CircleShape)
                                .testTag("language_toggle_button")
                        ) {
                            Text(
                                text = if (isAr) "EN" else "عربي",
                                color = DesertGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        // Map Button
                        IconButton(
                            onClick = { onOpenDialog(ActiveDialogType.WORLD_MAP) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(DesertObsidian.copy(alpha = 0.8f))
                                .border(1.dp, DesertGold, CircleShape)
                                .testTag("map_button")
                        ) {
                            Icon(Icons.Default.Map, contentDescription = "Map", tint = DesertGold)
                        }

                        // Inventory Button
                        IconButton(
                            onClick = { onOpenDialog(ActiveDialogType.INVENTORY) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(DesertObsidian.copy(alpha = 0.8f))
                                .border(1.dp, DesertGold, CircleShape)
                                .testTag("inventory_button")
                        ) {
                            Icon(Icons.Default.Backpack, contentDescription = "Inventory", tint = DesertGold)
                        }

                        // Settings Button
                        IconButton(
                            onClick = { onOpenDialog(ActiveDialogType.SETTINGS) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(DesertObsidian.copy(alpha = 0.8f))
                                .border(1.dp, DesertGold, CircleShape)
                                .testTag("settings_button")
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = DesertGold)
                        }

                        // Pause Button
                        IconButton(
                            onClick = { onOpenDialog(ActiveDialogType.PAUSE_MENU) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(DesertGold)
                                .border(1.dp, Color.White, CircleShape)
                                .testTag("pause_button")
                        ) {
                            Icon(Icons.Default.Pause, contentDescription = "Pause", tint = DesertObsidian)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Resource Counters (Gold & Keys)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = DesertObsidian.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, DesertGold)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = DesertGold, modifier = Modifier.size(18.dp))
                            Text("${uiState.goldCoins}", color = DesertGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Surface(
                        color = DesertObsidian.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, DesertGold)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Key, contentDescription = null, tint = DesertGold, modifier = Modifier.size(18.dp))
                            Text("${uiState.keysCollectedCount}/4", color = DesertGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quest Banner
                Surface(
                    color = DesertObsidian.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, DesertGold.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Explore, contentDescription = null, tint = DesertGold)
                        Column {
                            Text(
                                text = if (isAr) uiState.currentQuest.titleAr else uiState.currentQuest.titleEn,
                                color = DesertGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = if (isAr) uiState.currentQuest.descriptionAr else uiState.currentQuest.descriptionEn,
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // --- CENTER INTERACTION PROMPT ---
            AnimatedVisibility(
                visible = uiState.nearbyInteractNameEn != null,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Button(
                    onClick = onInteract,
                    colors = ButtonDefaults.buttonColors(containerColor = DesertGold, contentColor = DesertObsidian),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(2.dp, Color.White),
                    modifier = Modifier.testTag("interact_button")
                ) {
                    Icon(Icons.Default.TouchApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "تفاعل: ${uiState.nearbyInteractNameAr}" else "Interact: ${uiState.nearbyInteractNameEn}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            // --- BOTTOM TOUCH CONTROLS ---
            val joystickAlignment = if (uiState.joystickOnRight) Alignment.BottomEnd else Alignment.BottomStart
            val actionsAlignment = if (uiState.joystickOnRight) Alignment.BottomStart else Alignment.BottomEnd

            val effectiveJoystickRadius = (uiState.joystickSizeDp * uiState.controlsScale).coerceIn(40f, 110f)
            val actionScale = uiState.actionButtonsScale * uiState.controlsScale

            val bottomPadding = uiState.controlsBottomPaddingDp.dp
            val sidePadding = uiState.controlsSidePaddingDp.dp

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                // Joystick (Left or Right)
                Box(
                    modifier = Modifier
                        .align(joystickAlignment)
                        .padding(
                            start = if (!uiState.joystickOnRight) sidePadding else 0.dp,
                            end = if (uiState.joystickOnRight) sidePadding else 0.dp,
                            bottom = bottomPadding
                        )
                ) {
                    VirtualJoystick(
                        radiusDp = effectiveJoystickRadius,
                        onMove = onMoveInput
                    )
                }

                // Action Buttons Grid (Right or Left)
                Column(
                    verticalArrangement = Arrangement.spacedBy((10 * actionScale).dp),
                    horizontalAlignment = if (uiState.joystickOnRight) Alignment.Start else Alignment.End,
                    modifier = Modifier
                        .align(actionsAlignment)
                        .padding(
                            start = if (uiState.joystickOnRight) sidePadding else 0.dp,
                            end = if (!uiState.joystickOnRight) sidePadding else 0.dp,
                            bottom = bottomPadding
                        )
                ) {
                    val torchSize = (50 * actionScale).dp
                    val waterSize = (50 * actionScale).dp
                    val rollSize = (50 * actionScale).dp
                    val jumpSize = (56 * actionScale).dp
                    val attackSize = (64 * actionScale).dp

                    Row(horizontalArrangement = Arrangement.spacedBy((10 * actionScale).dp)) {
                        // Torch Toggle
                        IconButton(
                            onClick = onToggleTorch,
                            modifier = Modifier
                                .size(torchSize)
                                .clip(CircleShape)
                                .background(if (uiState.isTorchLit) DesertGold else DesertObsidian.copy(alpha = 0.8f))
                                .border(2.dp, DesertGold, CircleShape)
                                .testTag("torch_button")
                        ) {
                            Icon(
                                Icons.Default.Whatshot,
                                contentDescription = "Torch",
                                tint = if (uiState.isTorchLit) DesertObsidian else DesertGold,
                                modifier = Modifier.size((22 * actionScale).dp)
                            )
                        }

                        // Water Drink
                        IconButton(
                            onClick = onDrinkWater,
                            modifier = Modifier
                                .size(waterSize)
                                .clip(CircleShape)
                                .background(OasisTeal.copy(alpha = 0.9f))
                                .border(2.dp, Color.White, CircleShape)
                                .testTag("water_button")
                        ) {
                            Icon(
                                Icons.Default.WaterDrop,
                                contentDescription = "Water",
                                tint = Color.White,
                                modifier = Modifier.size((22 * actionScale).dp)
                            )
                        }

                        // Roll / Sprint
                        IconButton(
                            onClick = onRoll,
                            modifier = Modifier
                                .size(rollSize)
                                .clip(CircleShape)
                                .background(DesertObsidian.copy(alpha = 0.8f))
                                .border(2.dp, DesertGold, CircleShape)
                                .testTag("roll_button")
                        ) {
                            Icon(
                                Icons.Default.DirectionsRun,
                                contentDescription = "Roll",
                                tint = DesertGold,
                                modifier = Modifier.size((22 * actionScale).dp)
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy((12 * actionScale).dp)) {
                        // Jump
                        IconButton(
                            onClick = onJump,
                            modifier = Modifier
                                .size(jumpSize)
                                .clip(CircleShape)
                                .background(DesertGold.copy(alpha = 0.9f))
                                .border(2.dp, Color.White, CircleShape)
                                .testTag("jump_button")
                        ) {
                            Icon(
                                Icons.Default.ArrowUpward,
                                contentDescription = "Jump",
                                tint = DesertObsidian,
                                modifier = Modifier.size((26 * actionScale).dp)
                            )
                        }

                        // Attack (Sword Swing)
                        IconButton(
                            onClick = onAttack,
                            modifier = Modifier
                                .size(attackSize)
                                .clip(CircleShape)
                                .background(DesertCrimson)
                                .border(3.dp, DesertGold, CircleShape)
                                .testTag("attack_button")
                        ) {
                            Icon(
                                Icons.Default.Gavel,
                                contentDescription = "Attack",
                                tint = Color.White,
                                modifier = Modifier.size((32 * actionScale).dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CriticalWarningVignette(
    health: Int,
    stamina: Int
) {
    val isLowHealth = health <= 30
    val isLowStamina = stamina <= 20

    if (!isLowHealth && !isLowStamina) return

    val warningColor = when {
        isLowHealth -> DesertCrimson
        else -> DesertGold
    }

    val infiniteTransition = rememberInfiniteTransition(label = "vignette_warning_transition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "vignette_warning_alpha"
    )

    Canvas(modifier = Modifier.fillMaxSize().testTag("critical_warning_vignette")) {
        val width = size.width
        val height = size.height
        val edgeDepth = 60f

        // Top edge glow
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(warningColor.copy(alpha = pulseAlpha), Color.Transparent),
                startY = 0f,
                endY = edgeDepth
            )
        )
        // Bottom edge glow
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, warningColor.copy(alpha = pulseAlpha)),
                startY = height - edgeDepth,
                endY = height
            )
        )
        // Left edge glow
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(warningColor.copy(alpha = pulseAlpha), Color.Transparent),
                startX = 0f,
                endX = edgeDepth
            )
        )
        // Right edge glow
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, warningColor.copy(alpha = pulseAlpha)),
                startX = width - edgeDepth,
                endX = width
            )
        )
    }
}

@Composable
private fun StatProgressBar(
    icon: ImageVector,
    label: String,
    value: Int,
    maxValue: Int,
    color: Color,
    isCritical: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "stat_critical_pulse")
    val borderAlpha by if (isCritical) {
        infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 400),
                repeatMode = RepeatMode.Reverse
            ),
            label = "border_alpha"
        )
    } else {
        remember { mutableFloatStateOf(1.0f) }
    }

    val activeColor = if (isCritical) Color.Red else color

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = activeColor,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(38.dp)
        )
        Box(
            modifier = Modifier
                .width(105.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(Color.Black.copy(alpha = 0.7f))
                .border(
                    width = if (isCritical) 2.dp else 1.dp,
                    color = activeColor.copy(alpha = borderAlpha),
                    shape = RoundedCornerShape(7.dp)
                )
        ) {
            val fraction = (value.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(activeColor.copy(alpha = 0.7f), activeColor)
                        )
                    )
            )
            // Numerical readout inside bar
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$value/$maxValue",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

