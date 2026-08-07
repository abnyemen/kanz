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
import androidx.compose.runtime.*
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
import com.example.game.AppLanguage
import com.example.game.GraphicsQuality
import com.example.ui.theme.DesertGold
import com.example.ui.theme.DesertObsidian
import com.example.ui.theme.DesertSand
import com.example.ui.theme.OasisTeal
import com.example.ui.theme.DesertCrimson

@Composable
fun SettingsDialog(
    language: AppLanguage,
    graphicsQuality: GraphicsQuality = GraphicsQuality.HIGH,
    onSelectGraphicsQuality: (GraphicsQuality) -> Unit = {},
    sfxEnabled: Boolean = true,
    onToggleSfx: (Boolean) -> Unit = {},
    sfxVolume: Float = 0.8f,
    onChangeSfxVolume: (Float) -> Unit = {},
    musicVolume: Float = 0.7f,
    onChangeMusicVolume: (Float) -> Unit = {},
    controlsScale: Float = 1.0f,
    onChangeControlsScale: (Float) -> Unit = {},
    joystickOnRight: Boolean = false,
    onToggleJoystickOnRight: (Boolean) -> Unit = {},
    joystickSizeDp: Float = 65f,
    onChangeJoystickSize: (Float) -> Unit = {},
    actionButtonsScale: Float = 1.0f,
    onChangeActionButtonsScale: (Float) -> Unit = {},
    controlsBottomPaddingDp: Float = 12f,
    onChangeControlsBottomPadding: (Float) -> Unit = {},
    controlsSidePaddingDp: Float = 12f,
    onChangeControlsSidePadding: (Float) -> Unit = {},
    onResetControls: () -> Unit = {},
    onToggleLanguage: () -> Unit = {},
    onResetGame: () -> Unit = {},
    onClose: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onClose) {
        Surface(
            color = DesertObsidian,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, DesertGold),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(6.dp)
                .testTag("settings_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
                    .verticalScroll(scrollState)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = DesertGold, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "إعدادات اللعبة ⚙️" else "Game Settings ⚙️",
                            color = DesertGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("settings_close_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DesertGold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ==========================================
                // --- SECTION 1: CUSTOM CONTROLS (تخصيص أزرار التحكم) ---
                // ==========================================
                Surface(
                    color = Color(0xFF1B1109),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, DesertGold.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SportsEsports, contentDescription = null, tint = DesertGold, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "تخصيص أزرار التحكم 🎮" else "Customize Controls 🎮",
                                    color = DesertGold,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(
                                onClick = onResetControls,
                                modifier = Modifier.size(32.dp).testTag("reset_controls_button")
                            ) {
                                Icon(Icons.Default.RestartAlt, contentDescription = "Reset Controls", tint = DesertSand)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 1A. Swap Joystick Handedness (موقع مقبض الحركة)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isAr) "موقع عصا الحركة 🕹️" else "Joystick Position 🕹️",
                                color = Color.White,
                                fontSize = 13.sp
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = { onToggleJoystickOnRight(false) },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (!joystickOnRight) DesertGold else Color.Transparent,
                                        contentColor = if (!joystickOnRight) DesertObsidian else Color.White
                                    ),
                                    border = BorderStroke(1.dp, DesertGold),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text(if (isAr) "يسار ⬅️" else "Left ⬅️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { onToggleJoystickOnRight(true) },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (joystickOnRight) DesertGold else Color.Transparent,
                                        contentColor = if (joystickOnRight) DesertObsidian else Color.White
                                    ),
                                    border = BorderStroke(1.dp, DesertGold),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text(if (isAr) "يمين ➡️" else "Right ➡️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 1B. Overall Controls Scale (حجم الأزرار العام)
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isAr) "الحجم الكلي لعناصر التحكم 📐" else "Overall Control Scale 📐",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${(controlsScale * 100).toInt()}%",
                                    color = DesertGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Slider(
                                value = controlsScale,
                                onValueChange = onChangeControlsScale,
                                valueRange = 0.8f..1.4f,
                                colors = SliderDefaults.colors(
                                    thumbColor = DesertGold,
                                    activeTrackColor = DesertGold,
                                    inactiveTrackColor = Color.DarkGray
                                ),
                                modifier = Modifier.testTag("controls_scale_slider")
                            )
                        }

                        // 1C. Joystick Radius Size (حجم العصا الافتراضية)
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isAr) "حجم العصا الافتراضية ⭕" else "Joystick Size ⭕",
                                    color = Color.LightGray,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${joystickSizeDp.toInt()} dp",
                                    color = DesertGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Slider(
                                value = joystickSizeDp,
                                onValueChange = onChangeJoystickSize,
                                valueRange = 50f..90f,
                                colors = SliderDefaults.colors(
                                    thumbColor = DesertGold,
                                    activeTrackColor = DesertGold,
                                    inactiveTrackColor = Color.DarkGray
                                ),
                                modifier = Modifier.testTag("joystick_size_slider")
                            )
                        }

                        // 1D. Action Buttons Size (حجم أزرار الهجوم والحركة)
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isAr) "حجم أزرار الهجوم والحركة ⚔️" else "Action Buttons Size ⚔️",
                                    color = Color.LightGray,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${(actionButtonsScale * 100).toInt()}%",
                                    color = DesertGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Slider(
                                value = actionButtonsScale,
                                onValueChange = onChangeActionButtonsScale,
                                valueRange = 0.8f..1.4f,
                                colors = SliderDefaults.colors(
                                    thumbColor = DesertGold,
                                    activeTrackColor = DesertGold,
                                    inactiveTrackColor = Color.DarkGray
                                ),
                                modifier = Modifier.testTag("action_buttons_scale_slider")
                            )
                        }

                        // 1E. Edge Margins / Padding (الهوامش عن الحواف)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isAr) "هامش الشاشة السفلية ⬇️" else "Bottom Margin ⬇️",
                                    color = Color.LightGray,
                                    fontSize = 11.sp
                                )
                                Slider(
                                    value = controlsBottomPaddingDp,
                                    onValueChange = onChangeControlsBottomPadding,
                                    valueRange = 4f..32f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = DesertGold,
                                        activeTrackColor = DesertGold,
                                        inactiveTrackColor = Color.DarkGray
                                    ),
                                    modifier = Modifier.testTag("bottom_padding_slider")
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isAr) "هامش الشاشة الجانبي ⬅️➡️" else "Side Margin ⬅️➡️",
                                    color = Color.LightGray,
                                    fontSize = 11.sp
                                )
                                Slider(
                                    value = controlsSidePaddingDp,
                                    onValueChange = onChangeControlsSidePadding,
                                    valueRange = 4f..32f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = DesertGold,
                                        activeTrackColor = DesertGold,
                                        inactiveTrackColor = Color.DarkGray
                                    ),
                                    modifier = Modifier.testTag("side_padding_slider")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // --- LIVE MINI CONTROLS PREVIEW ---
                        Text(
                            text = if (isAr) "معاينة مباشرة للتحكم 📱" else "Live Controls Layout Preview 📱",
                            color = DesertGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(95.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .border(1.dp, DesertGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(
                                    start = (controlsSidePaddingDp * 0.5f).dp,
                                    end = (controlsSidePaddingDp * 0.5f).dp,
                                    bottom = (controlsBottomPaddingDp * 0.4f).dp
                                )
                        ) {
                            val miniJoyAlign = if (joystickOnRight) Alignment.BottomEnd else Alignment.BottomStart
                            val miniActAlign = if (joystickOnRight) Alignment.BottomStart else Alignment.BottomEnd

                            // Mini Joystick indicator
                            Box(
                                modifier = Modifier
                                    .align(miniJoyAlign)
                                    .size(((joystickSizeDp * 0.45f) * controlsScale).dp)
                                    .clip(CircleShape)
                                    .background(DesertGold.copy(alpha = 0.3f))
                                    .border(2.dp, DesertGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }

                            // Mini Action buttons indicator
                            Row(
                                modifier = Modifier.align(miniActAlign),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                val miniBtnSize = (20 * actionButtonsScale * controlsScale).dp
                                val miniAtkSize = (26 * actionButtonsScale * controlsScale).dp

                                Box(
                                    modifier = Modifier
                                        .size(miniBtnSize)
                                        .clip(CircleShape)
                                        .background(OasisTeal)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(miniBtnSize)
                                        .clip(CircleShape)
                                        .background(DesertGold)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(miniAtkSize)
                                        .clip(CircleShape)
                                        .background(DesertCrimson)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = DesertGold.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(16.dp))

                // ==========================================
                // --- SECTION 2: GRAPHICS QUALITY ---
                // ==========================================
                Text(
                    text = if (isAr) "جودة الجرافيك 🎮" else "Graphics Quality 🎮",
                    color = DesertGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GraphicsQuality.entries.forEach { quality ->
                        val isSelected = quality == graphicsQuality
                        OutlinedButton(
                            onClick = { onSelectGraphicsQuality(quality) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("graphics_quality_${quality.name.lowercase()}"),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) DesertGold else Color.Transparent,
                                contentColor = if (isSelected) DesertObsidian else Color.White
                            ),
                            border = BorderStroke(1.dp, if (isSelected) DesertGold else Color.Gray),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = when (quality) {
                                    GraphicsQuality.LOW -> if (isAr) "منخفضة" else "Low"
                                    GraphicsQuality.MEDIUM -> if (isAr) "متوسطة" else "Medium"
                                    GraphicsQuality.HIGH -> if (isAr) "عالية" else "High"
                                },
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = DesertGold.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(16.dp))

                // ==========================================
                // --- SECTION 3: AUDIO & SFX ---
                // ==========================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = DesertGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "المؤثرات الصوتية والاهتزاز 🔊" else "Sound & Haptics 🔊",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                    Switch(
                        checked = sfxEnabled,
                        onCheckedChange = onToggleSfx,
                        colors = SwitchDefaults.colors(checkedThumbColor = DesertGold, checkedTrackColor = DesertObsidian)
                    )
                }

                if (sfxEnabled) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isAr) "شدة صوت المؤثرات" else "SFX Volume",
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${(sfxVolume * 100).toInt()}%",
                                color = DesertGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = sfxVolume,
                            onValueChange = onChangeSfxVolume,
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = DesertGold,
                                activeTrackColor = DesertGold,
                                inactiveTrackColor = Color.DarkGray
                            ),
                            modifier = Modifier.testTag("sfx_volume_slider")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Music Volume Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = DesertGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "مستوى الموسيقى 🎵" else "Music Volume 🎵",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "${(musicVolume * 100).toInt()}%",
                            color = DesertGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = musicVolume,
                        onValueChange = onChangeMusicVolume,
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = DesertGold,
                            activeTrackColor = DesertGold,
                            inactiveTrackColor = Color.DarkGray
                        ),
                        modifier = Modifier.testTag("music_volume_slider")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = DesertGold.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(16.dp))

                // ==========================================
                // --- SECTION 4: LANGUAGE & DATA RESET ---
                // ==========================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "لغة الواجهة 🌐" else "Interface Language 🌐",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Button(
                        onClick = onToggleLanguage,
                        colors = ButtonDefaults.buttonColors(containerColor = DesertGold, contentColor = DesertObsidian)
                    ) {
                        Text(if (isAr) "العربية 🇸🇦" else "English 🇬🇧", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Reset Game Data Button
                OutlinedButton(
                    onClick = {
                        onResetGame()
                        onClose()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = BorderStroke(1.dp, Color.Red),
                    modifier = Modifier.fillMaxWidth().testTag("reset_game_button")
                ) {
                    Text(if (isAr) "إعادة تشغيل اللعبة من البداية 🔄" else "Reset Game Progress 🔄", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
