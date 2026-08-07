package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.game.AppLanguage
import com.example.game.GameUiState
import com.example.ui.theme.*

@Composable
fun PauseMenuDialog(
    uiState: GameUiState,
    timeOfDayHours: Float,
    onResume: () -> Unit,
    onQuickSave: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenStoryIntro: () -> Unit,
    onOpenLoadingScreen: () -> Unit = {},
    onShowTutorial: () -> Unit = {},
    onBackToMainMenu: () -> Unit,
    onToggleLanguage: () -> Unit
) {
    val isAr = uiState.language == AppLanguage.ARABIC
    val scrollState = rememberScrollState()

    // Convert time of day to formatted string
    val totalMinutes = (timeOfDayHours * 60).toInt()
    val hours12 = (totalMinutes / 60) % 12.let { if (it == 0) 12 else it }
    val minutes = totalMinutes % 60
    val isPm = timeOfDayHours >= 12.0f
    val timeFormatted = String.format("%02d:%02d %s", hours12, minutes, if (isPm) "PM" else "AM")
    val sunMoonIcon = if (timeOfDayHours in 6.0f..18.0f) "☀️" else "🌙"

    Dialog(
        onDismissRequest = onResume,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            color = DesertObsidian.copy(alpha = 0.96f),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(2.dp, DesertGold),
            modifier = Modifier
                .fillMaxSize(0.92f)
                .padding(8.dp)
                .testTag("pause_menu_dialog")
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Title
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(DesertGold.copy(alpha = 0.2f))
                                    .border(1.dp, DesertGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Pause,
                                    contentDescription = null,
                                    tint = DesertGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isAr) "اللعبة متوقفة مؤقتاً ⏸️" else "GAME PAUSED ⏸️",
                                color = DesertGold,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            )
                        }

                        // Language Toggle Quick Action
                        OutlinedButton(
                            onClick = onToggleLanguage,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = DesertGold
                            ),
                            border = BorderStroke(1.dp, DesertGold),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(34.dp).testTag("pause_language_toggle")
                        ) {
                            Text(
                                text = if (isAr) "EN 🇬🇧" else "عربي 🇸🇦",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- PLAYER STATUS SNAPSHOT CARD ---
                    Surface(
                        color = Color(0xFF1C130B),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, DesertGold.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isAr) "ملخص حالة المستكشف 📜" else "Explorer Status Summary 📜",
                                color = DesertGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Time & Weather
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "$sunMoonIcon $timeFormatted",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Gold & Keys
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = DesertGold, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${uiState.goldCoins}", color = DesertGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Key, contentDescription = null, tint = DesertGold, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${uiState.keysCollectedCount}/4", color = DesertGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // HP & Hydration indicators
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(if (isAr) "الصحة ❤️" else "Health ❤️", color = Color.LightGray, fontSize = 11.sp)
                                        Text("${uiState.health}%", color = DesertCrimson, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    LinearProgressIndicator(
                                        progress = { uiState.health / 100f },
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                        color = DesertCrimson,
                                        trackColor = Color.DarkGray
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(if (isAr) "الارتواء 💧" else "Water 💧", color = Color.LightGray, fontSize = 11.sp)
                                        Text("${uiState.hydration}%", color = OasisTeal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    LinearProgressIndicator(
                                        progress = { uiState.hydration / 100f },
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                        color = OasisTeal,
                                        trackColor = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // --- PAUSE MENU MAIN ACTION BUTTONS ---
                    Column(
                        modifier = Modifier.fillMaxWidth(0.88f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // 1. RESUME BUTTON
                        Button(
                            onClick = onResume,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DesertGold,
                                contentColor = DesertObsidian
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("pause_resume_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Resume", modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "استئناف اللعبة ◀️" else "Resume Game ◀️",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        // 2. QUICK SAVE BUTTON
                        Button(
                            onClick = onQuickSave,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OasisTeal,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("pause_quicksave_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Save, contentDescription = "Quick Save", modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "حفظ سريع للتقدم 💾" else "Quick Save Progress 💾",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // 3. SETTINGS BUTTON
                        OutlinedButton(
                            onClick = onOpenSettings,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = DesertGold
                            ),
                            border = BorderStroke(1.5.dp, DesertGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("pause_settings_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings", modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "إعدادات اللعبة والتحكم ⚙️" else "Game Settings & Controls ⚙️",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // 4. STORY & QUESTS BUTTON
                        OutlinedButton(
                            onClick = onOpenStoryIntro,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = DesertSand
                            ),
                            border = BorderStroke(1.dp, DesertSand),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("pause_story_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MenuBook, contentDescription = "Story", modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "قصة الصحراء والمهام 📖" else "Story & Quest Log 📖",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // 4b. CONTROLS TUTORIAL OVERLAY BUTTON
                        OutlinedButton(
                            onClick = {
                                onResume()
                                onShowTutorial()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = OasisTeal
                            ),
                            border = BorderStroke(1.dp, OasisTeal),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("pause_tutorial_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TouchApp, contentDescription = "Tutorial", modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "دليل التحكم والتحرك 🎮" else "Touch Controls Tutorial 🎮",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // 4c. LOADING SCREEN DISPLAY BUTTON
                        OutlinedButton(
                            onClick = onOpenLoadingScreen,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = DesertGold
                            ),
                            border = BorderStroke(1.dp, DesertGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("pause_loading_screen_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.HourglassTop, contentDescription = "Loading Screen", modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "عرض شاشة التحميل ⏳" else "Show Loading Screen ⏳",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // 5. MAIN MENU / RESTART BUTTON
                        OutlinedButton(
                            onClick = onBackToMainMenu,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = DesertCrimson
                            ),
                            border = BorderStroke(1.5.dp, DesertCrimson),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("pause_main_menu_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Home, contentDescription = "Main Menu", modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "العودة للقائمة الرئيسية 🏠" else "Main Menu / Restart 🏠",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isAr) "العالم متوقف مؤقتاً. اضغط استئناف للعودة للعب." else "Game world paused. Tap Resume to continue.",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
