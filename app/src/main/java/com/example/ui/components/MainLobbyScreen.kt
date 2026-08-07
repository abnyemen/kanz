package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.ui.theme.OasisTeal

@Composable
fun MainLobbyScreen(
    language: AppLanguage,
    goldCoins: Int,
    diamonds: Int,
    keysCount: Int,
    selectedHeroSkin: String,
    currentMount: String,
    onStartJourney: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenCustomization: () -> Unit,
    onOpenWorldMap: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenGuide: () -> Unit,
    onToggleLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC

    // Subtle pulsing animation for main start button
    val infiniteTransition = rememberInfiniteTransition(label = "lobby_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val heroEmoji = remember(selectedHeroSkin) {
        when (selectedHeroSkin) {
            "pharaoh" -> "🤴"
            "guardian" -> "🧙‍♂️"
            else -> "🧕"
        }
    }

    val heroName = remember(selectedHeroSkin, isAr) {
        when (selectedHeroSkin) {
            "pharaoh" -> if (isAr) "الفارس الفرعوني الذهبي" else "Golden Pharaoh"
            "guardian" -> if (isAr) "حارس الواحة الأسطوري" else "Oasis Guardian"
            else -> if (isAr) "رحالة البدو المغامر" else "Bedouin Explorer"
        }
    }

    val mountEmoji = remember(currentMount) {
        when (currentMount) {
            "camel" -> "🐪"
            "horse" -> "🐎"
            else -> "🚶"
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B110B),
                        DesertObsidian,
                        Color(0xFF0D0805)
                    )
                )
            )
            .padding(16.dp)
            .testTag("main_lobby_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // --- TOP HEADER BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gold, Diamonds & Keys Badge Counters
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, DesertGold)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💰", fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$goldCoins",
                                color = DesertGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, OasisTeal)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💎", fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$diamonds",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, DesertGold.copy(alpha = 0.7f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🔑", fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$keysCount/4",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Top Right Action Buttons: Language Switch & Settings
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onToggleLanguage,
                        border = BorderStroke(1.dp, DesertGold.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DesertGold),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(if (isAr) "English" else "العربية", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .border(1.dp, DesertGold.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = DesertGold, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // --- CENTER LOBBY HERO & GAME TITLE AREA ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                // Game Logo Icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(DesertGold.copy(alpha = 0.15f))
                        .border(2.dp, DesertGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏺", fontSize = 38.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isAr) "ساحة الانتظار - كنز الصحراء 3D" else "Desert Treasure 3D - Main Lobby",
                    color = DesertGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isAr) "عالم مفتوح • استكشاف أثري • مركوبات بدوية" else "Open World • Egyptian Exploration • Mounts",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Hero Preview Display Card
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, OasisTeal.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .widthIn(max = 380.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(OasisTeal.copy(alpha = 0.2f))
                                    .border(1.dp, OasisTeal, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("$heroEmoji $mountEmoji", fontSize = 20.sp)
                            }

                            Column {
                                Text(
                                    text = heroName,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (isAr) "الركوبة المجهزة: $mountEmoji" else "Equipped Mount: $mountEmoji",
                                    color = OasisTeal,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onOpenCustomization,
                            border = BorderStroke(1.dp, OasisTeal),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = OasisTeal),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(if (isAr) "تغيير 👤" else "Custom 👤", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // --- BOTTOM SECTION: START BUTTON & LOBBY MENU GRID ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                // GIANT START GAME / ENTER WORLD BUTTON
                Button(
                    onClick = onStartJourney,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DesertGold,
                        contentColor = DesertObsidian
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .padding(horizontal = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = if (isAr) "بدء المغامرة ودخول العالم 🐪⚔️" else "Start Journey & Enter World 🐪⚔️",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp
                        )
                    }
                }

                // LOBBY QUICK ACTION GRID
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Bedouin Bazaar / Shop 🛒
                    Button(
                        onClick = onOpenShop,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black.copy(alpha = 0.6f),
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, DesertGold),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🛒", fontSize = 16.sp)
                            Text(if (isAr) "المتجر" else "Shop", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Achievements & Diamond Rewards 🏆
                    Button(
                        onClick = onOpenAchievements,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black.copy(alpha = 0.6f),
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, OasisTeal),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🏆", fontSize = 16.sp)
                            Text(if (isAr) "الإنجازات" else "Awards", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // World Map Preview 🗺️
                    Button(
                        onClick = onOpenWorldMap,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black.copy(alpha = 0.6f),
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🗺️", fontSize = 16.sp)
                            Text(if (isAr) "الخريطة" else "Map", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Explorer's Handbook / Guide 📖
                    Button(
                        onClick = onOpenGuide,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black.copy(alpha = 0.6f),
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, DesertGold.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = DesertGold, modifier = Modifier.size(16.dp))
                            Text(if (isAr) "الدليل" else "Guide", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
