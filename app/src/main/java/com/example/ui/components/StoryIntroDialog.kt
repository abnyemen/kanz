package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.game.AppLanguage
import com.example.ui.theme.DesertGold
import com.example.ui.theme.DesertObsidian
import com.example.ui.theme.OasisTeal

@Composable
fun StoryIntroDialog(
    language: AppLanguage,
    goldCoins: Int = 50,
    keysCount: Int = 0,
    onStartJourney: () -> Unit,
    onOpenShop: () -> Unit = {},
    onOpenCustomization: () -> Unit = {},
    onOpenWorldMap: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onOpenGuide: () -> Unit = {},
    onToggleLanguage: () -> Unit = {}
) {
    val isAr = language == AppLanguage.ARABIC

    Dialog(onDismissRequest = onStartJourney) {
        Surface(
            color = DesertObsidian,
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(2.dp, DesertGold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .testTag("story_intro_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Bar (Gold, Keys, Language)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, DesertGold.copy(alpha = 0.8f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("💰", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("$goldCoins", color = DesertGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, DesertGold.copy(alpha = 0.8f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🔑", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("$keysCount/4", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    // Language Switch Button
                    TextButton(
                        onClick = onToggleLanguage,
                        colors = ButtonDefaults.textButtonColors(contentColor = DesertGold)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(if (isAr) "English" else "العربية", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Emblem & Title
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(DesertGold.copy(alpha = 0.2f))
                        .border(2.dp, DesertGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏺", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isAr) "كنز الصحراء الأسطوري" else "Desert Treasure 3D",
                    color = DesertGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Text(
                    text = if (isAr) "مغامرة الفراعنة والاستكشاف الحر" else "Ancient Pharaohs Open World RPG",
                    color = Color.LightGray,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Primary Start Button
                Button(
                    onClick = onStartJourney,
                    colors = ButtonDefaults.buttonColors(containerColor = DesertGold, contentColor = DesertObsidian),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Text(
                            text = if (isAr) "بدء / استئناف المغامرة 🐪" else "Start / Continue Journey 🐪",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Main Menu Grid Options (Shop, Customization, Map, Settings, Guide)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Shop Button 🛒
                        Button(
                            onClick = onOpenShop,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.6f), contentColor = Color.White),
                            border = BorderStroke(1.dp, DesertGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("🛒", fontSize = 16.sp)
                                Text(if (isAr) "المتجر" else "Shop", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Hero & Mount Customization 👤
                        Button(
                            onClick = onOpenCustomization,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.6f), contentColor = Color.White),
                            border = BorderStroke(1.dp, OasisTeal),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("👤", fontSize = 16.sp)
                                Text(if (isAr) "الشخصية" else "Hero", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // World Map 🗺️
                        Button(
                            onClick = onOpenWorldMap,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.6f), contentColor = Color.White),
                            border = BorderStroke(1.dp, Color.Gray),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("🗺️", fontSize = 16.sp)
                                Text(if (isAr) "الخريطة" else "Map", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Settings ⚙️
                        Button(
                            onClick = onOpenSettings,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.6f), contentColor = Color.White),
                            border = BorderStroke(1.dp, Color.Gray),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text(if (isAr) "الإعدادات" else "Settings", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Explorer's Guide Button 📖
                    OutlinedButton(
                        onClick = onOpenGuide,
                        border = BorderStroke(1.dp, DesertGold.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DesertGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(
                                if (isAr) "دليل المستكشف وطريقة اللعب 📖" else "Explorer's Handbook & Controls 📖",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VictoryDialog(
    language: AppLanguage,
    onContinueFreeRoam: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC

    Dialog(onDismissRequest = onContinueFreeRoam) {
        Surface(
            color = DesertObsidian,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(3.dp, DesertGold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("victory_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isAr) "🏆 النصر والكنز الأسطوري! 🏆" else "🏆 Legendary Victory! 🏆",
                    color = DesertGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isAr)
                        "تهانينا! لقد فتحت بوابة الهرم الأكبر وحصلت على كنز الصحراء الأسطوري المفقود منذ 3000 عام!\n\nيمكنك الآن مواصلة استكشاف العالم المفتوح بحرية كاملة، وركوب الجمال والخيل، وإكمال المهام الإضافية."
                    else
                        "Congratulations! You unlocked the Great Pyramid gate and claimed the Legendary Desert Treasure lost for 3000 years!\n\nYou can now continue exploring the open world freely, ride camels and horses, and complete bonus quests.",
                    color = Color.White,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onContinueFreeRoam,
                    colors = ButtonDefaults.buttonColors(containerColor = DesertGold, contentColor = DesertObsidian),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isAr) "استمرار الاستكشاف الحر 🐪" else "Continue Free Exploration 🐪",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
