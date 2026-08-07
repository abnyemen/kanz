package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UnlockedTemple
import com.example.game.AppLanguage
import com.example.ui.theme.DesertGold
import com.example.ui.theme.DesertObsidian
import com.example.ui.theme.DesertSand
import com.example.ui.theme.OasisTeal

@Composable
fun ObjectiveHudWidget(
    language: AppLanguage,
    keysCollectedCount: Int,
    unlockedTemples: List<UnlockedTemple>,
    currentBiomeEn: String,
    currentBiomeAr: String,
    onOpenQuestLog: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC
    var isExpanded by remember { mutableStateOf(true) }

    // Detect state changes for brief highlight animation
    var pulseTrigger by remember { mutableIntStateOf(0) }
    LaunchedEffect(keysCollectedCount, unlockedTemples.size) {
        pulseTrigger++
    }

    val pulseAlpha by animateFloatAsState(
        targetValue = if (pulseTrigger % 2 == 1) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 600),
        label = "objective_pulse"
    )

    // Compute dynamic temple status
    val horusTemple = unlockedTemples.find { it.templeId.contains("horus") }
    val anubisTemple = unlockedTemples.find { it.templeId.contains("anubis") }

    val isHorusUnlocked = horusTemple?.isUnlocked == true
    val isAnubisUnlocked = anubisTemple?.isUnlocked == true

    // Compute active objective title and details
    val mainTitle = remember(keysCollectedCount, isHorusUnlocked, isAnubisUnlocked, isAr) {
        when {
            keysCollectedCount >= 4 -> {
                if (isAr) "عد لمحراب أبو الهول العظيم 🏛️" else "Return to Great Sphinx Altar 🏛️"
            }
            isHorusUnlocked || isAnubisUnlocked -> {
                if (isAr) "استكشف المقابر والمعابد المفتوحة 🔑" else "Explore Unlocked Temple Vaults 🔑"
            }
            else -> {
                if (isAr) "ابحث عن المفاتيح الآثرية الأربعة 🗝️" else "Locate 4 Ancient Relic Keys 🗝️"
            }
        }
    }

    val subNote = remember(keysCollectedCount, isHorusUnlocked, isAnubisUnlocked, isAr) {
        when {
            keysCollectedCount >= 4 -> {
                if (isAr) "جمعت كافة المفاتيح! فعّل بوابة الحضارة القديمة."
                else "All 4 keys collected! Activate the ancient civilization portal."
            }
            isHorusUnlocked || isAnubisUnlocked -> {
                if (isAr) "افتح الصناديق وحل ألغاز المعابد داخل الغرف المظلمة."
                else "Open urns and solve ancient temple puzzles inside dark chambers."
            }
            else -> {
                if (isAr) "ابحث في الواحة والكثبان الرملية عن المفاتيح والجرار الأثرية."
                else "Search Palm Oasis & Sand Dunes for urns and key altars."
            }
        }
    }

    Surface(
        color = DesertObsidian.copy(alpha = 0.88f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (pulseAlpha > 0f) OasisTeal else DesertGold.copy(alpha = 0.8f)
        ),
        modifier = modifier
            .widthIn(max = 240.dp)
            .testTag("floating_objective_widget")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            // Header Row (Toggle & Main Goal)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .testTag("objective_expand_toggle"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(OasisTeal.copy(alpha = 0.25f))
                            .border(1.dp, OasisTeal, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎯", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column {
                        Text(
                            text = if (isAr) "الهدف الحالي" else "CURRENT OBJECTIVE",
                            color = OasisTeal,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = mainTitle,
                            color = DesertGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Toggle Objective View",
                    tint = DesertGold,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Expanded Progress Breakdown Notes
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Divider(color = Color.White.copy(alpha = 0.15f), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = subNote,
                        color = Color.LightGray,
                        fontSize = 10.sp,
                        lineHeight = 12.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    // 1. Relic Keys Counter Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔑 ", fontSize = 11.sp)
                            Text(
                                text = if (isAr) "المفاتيح:" else "Relic Keys:",
                                color = DesertSand,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "$keysCollectedCount / 4",
                            color = if (keysCollectedCount >= 4) OasisTeal else DesertGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    LinearProgressIndicator(
                        progress = { (keysCollectedCount / 4f).coerceIn(0f, 1f) },
                        color = OasisTeal,
                        trackColor = Color.DarkGray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. Temples Entrance Tracker
                    Text(
                        text = if (isAr) "مداخل المعابد والمقابر:" else "Temple Entrances:",
                        color = DesertSand,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Horus Temple
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "• معبد حورس" else "• Horus Temple",
                            color = Color.White,
                            fontSize = 10.sp
                        )
                        Text(
                            text = if (isHorusUnlocked) {
                                if (isAr) "مفتوح ✅" else "Unlocked ✅"
                            } else {
                                if (isAr) "مغلق 🔒" else "Locked 🔒"
                            },
                            color = if (isHorusUnlocked) OasisTeal else Color.Gray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Anubis Temple
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "• معبد أنوبيس" else "• Anubis Vault",
                            color = Color.White,
                            fontSize = 10.sp
                        )
                        Text(
                            text = if (isAnubisUnlocked) {
                                if (isAr) "مفتوح ✅" else "Unlocked ✅"
                            } else {
                                if (isAr) "مغلق 🔒" else "Locked 🔒"
                            },
                            color = if (isAnubisUnlocked) OasisTeal else Color.Gray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // 3. Current Region Badge Note
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            tint = DesertGold,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAr) "المنطقة: $currentBiomeAr" else "Zone: $currentBiomeEn",
                            color = DesertGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
