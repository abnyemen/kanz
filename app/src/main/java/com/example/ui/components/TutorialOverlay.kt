package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.AppLanguage
import com.example.ui.theme.DesertGold
import com.example.ui.theme.DesertObsidian
import com.example.ui.theme.DesertSand
import com.example.ui.theme.OasisTeal
import kotlinx.coroutines.delay

@Composable
fun TutorialOverlay(
    language: AppLanguage,
    joystickOnRight: Boolean = false,
    onDismiss: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC
    var autoTimerProgress by remember { mutableStateOf(1f) }

    // Auto dismiss countdown timer (9 seconds)
    LaunchedEffect(Unit) {
        val totalMs = 9000L
        val intervalMs = 100L
        var elapsed = 0L
        while (elapsed < totalMs) {
            delay(intervalMs)
            elapsed += intervalMs
            autoTimerProgress = (1f - (elapsed.toFloat() / totalMs)).coerceIn(0f, 1f)
        }
        onDismiss()
    }

    // Infinite repeat transition for gesture animations
    val infiniteTransition = rememberInfiniteTransition(label = "tutorial_gestures")

    // Joystick sweep animation
    val joystickAnimOffset by infiniteTransition.animateFloat(
        initialValue = -25f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "joystick_anim"
    )

    // Camera swipe animation
    val cameraSwipeX by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "camera_anim"
    )

    // Pulse animation
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_anim"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable { onDismiss() } // Tap anywhere to dismiss
            .testTag("tutorial_overlay")
    ) {

        // --- 1. TOP HEADER DISMISS BAR & TIMER ---
        Surface(
            color = DesertObsidian.copy(alpha = 0.92f),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.5.dp, DesertGold),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .testTag("tutorial_top_banner")
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "✨", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "دليل التحكم باللمس والتحرك 🎮" else "Touch Controls Guide 🎮",
                            color = DesertGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DesertGold,
                            contentColor = DesertObsidian
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("tutorial_got_it_button")
                    ) {
                        Text(
                            text = if (isAr) "فهمت! ✓" else "Got It! ✓",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Progress Bar showing auto-dismiss countdown
                LinearProgressIndicator(
                    progress = { autoTimerProgress },
                    color = OasisTeal,
                    trackColor = Color.DarkGray,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                )
            }
        }

        // --- 2. MOVEMENT JOYSTICK TUTORIAL CARD (LEFT OR RIGHT) ---
        val joystickAlignment = if (joystickOnRight) Alignment.BottomEnd else Alignment.BottomStart
        Box(
            modifier = Modifier
                .align(joystickAlignment)
                .padding(
                    start = if (joystickOnRight) 16.dp else 24.dp,
                    end = if (joystickOnRight) 24.dp else 16.dp,
                    bottom = 120.dp
                )
        ) {
            Surface(
                color = DesertObsidian.copy(alpha = 0.88f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, OasisTeal),
                modifier = Modifier.width(200.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated Joystick Sweep Circle Visualizer
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(OasisTeal.copy(alpha = 0.2f))
                            .border(1.dp, OasisTeal, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Sliding thumb knob indicator
                        Box(
                            modifier = Modifier
                                .offset(x = joystickAnimOffset.dp, y = (joystickAnimOffset * 0.5f).dp)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(DesertGold)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isAr) "عصا الحركة 🕹️" else "Move Joystick 🕹️",
                        color = DesertGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    Text(
                        text = if (isAr) "اسحب للتنقل والركض في أرجاء الصحراء"
                        else "Drag in any direction to walk or run",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // --- 3. 3D CAMERA ORBIT SWIPE TUTORIAL CARD (TOP RIGHT OR CENTER RIGHT) ---
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 24.dp)
        ) {
            Surface(
                color = DesertObsidian.copy(alpha = 0.88f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, DesertGold),
                modifier = Modifier.width(210.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated Swipe Gesture Visualizer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val midY = size.height / 2
                            drawLine(
                                color = DesertGold.copy(alpha = 0.5f),
                                start = Offset(20f, midY),
                                end = Offset(size.width - 20f, midY),
                                strokeWidth = 3f
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.offset(x = cameraSwipeX.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = DesertGold,
                                modifier = Modifier.size(28.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = OasisTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isAr) "تدوير الكاميرا 🔄" else "3D Camera Orbit 🔄",
                        color = DesertGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    Text(
                        text = if (isAr) "اسحب أصبعك في أي مكان على الشاشة لتدوير الكاميرا والتكبير"
                        else "Drag anywhere on right screen to rotate & pinch zoom camera",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // --- 4. ACTION BUTTONS TAP TUTORIAL BADGE (BOTTOM CENTER/RIGHT) ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp)
        ) {
            Surface(
                color = DesertObsidian.copy(alpha = 0.92f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.5.dp, OasisTeal)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(OasisTeal.copy(alpha = 0.3f))
                            .border(1.dp, OasisTeal, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⚔️", fontSize = 16.sp)
                    }

                    Column {
                        Text(
                            text = if (isAr) "الأزرار التفاعلية ⚡" else "Action & Interaction ⚡",
                            color = OasisTeal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = if (isAr) "انقر الأزرار للهجوم ⚔️، القفز ⬆️، المراوغة 🌀 والتفاعل ✋"
                            else "Tap buttons to Attack ⚔️, Jump ⬆️, Roll 🌀 & Interact ✋",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
