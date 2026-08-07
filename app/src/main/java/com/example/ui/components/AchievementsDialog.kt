package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
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
import androidx.compose.ui.window.Dialog
import com.example.data.Achievement
import com.example.game.AppLanguage
import com.example.ui.theme.DesertGold
import com.example.ui.theme.DesertObsidian
import com.example.ui.theme.OasisTeal

@Composable
fun AchievementsDialog(
    language: AppLanguage,
    goldCoins: Int,
    diamonds: Int,
    achievementsList: List<Achievement>,
    claimedAchievementIds: Set<String>,
    onClaimReward: (Achievement) -> Unit,
    onClose: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC

    // Default achievement catalog if database list is empty
    val displayAchievements = remember(achievementsList) {
        if (achievementsList.isNotEmpty()) {
            achievementsList
        } else {
            listOf(
                Achievement("first_steps", "First Explorer", "مستكشف مبتدئ 🐪", "Began the desert journey", "بدأت رحلة الصحراء واستكشاف الواحة", "EASY", 300, 2, true),
                Achievement("oasis_finder", "Oasis Sanctuary", "واحة الأمان 🌴", "Discovered the Palm Oasis", "اكتشفت واحة النخيل والمياه العذبة", "EASY", 350, 3, true),
                Achievement("camel_rider", "Desert Nomad", "رحالة الجمال 🐪", "Rode a desert camel across dunes", "امتطيت الجمل للتنقل السريع", "EASY", 250, 2, true),
                Achievement("sandstorm_survivor", "Storm Walker", "مواجه العواصف 🌪️", "Survived a severe desert sandstorm", "نجوت من عاصفة رملية شديدة", "MEDIUM", 450, 5, false),
                Achievement("horus_key", "Eye of Horus", "عين حورس 👁️", "Unlocked the first Temple Key", "حصلت على مفتاح معبد حورس الأثري", "MEDIUM", 500, 5, false),
                Achievement("anubis_slayer", "Guardian Slayer", "قاهر حارس أنوبيس ⚔️", "Defeated the Anubis Tomb Boss", "هزمت حارس المقابر الأسطوري في قتال عنيف", "HARD", 800, 20, false),
                Achievement("pharaoh_treasure", "Royal Pharaoh Relic", "جامع الكنوز الفرعونية 🏺", "Opened 5 Ancient Loot Chests", "فتحت 5 صناديق كنوز فرعونية أثرية", "HARD", 1000, 20, false),
                Achievement("desert_treasure", "Pyramid Master", "أسطورة كنز الهرم 👑", "Unlocked the Great Pyramid Vault!", "فتحت غرفة كنز الهرم الأكبر الأسطوري!", "LEGENDARY", 2000, 20, false)
            )
        }
    }

    Dialog(onDismissRequest = onClose) {
        Surface(
            color = DesertObsidian,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(2.dp, DesertGold),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(4.dp)
                .testTag("achievements_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header Bar with Trophy Icon & Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(DesertGold.copy(alpha = 0.2f))
                                .border(1.dp, DesertGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = DesertGold, modifier = Modifier.size(22.dp))
                        }

                        Column {
                            Text(
                                text = if (isAr) "سجل الإنجازات والمكافآت 🏆" else "Achievements & Rewards 🏆",
                                color = DesertGold,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = if (isAr) "احصل على الألماس 💎 والذهب 🪙 بحسب صعوبة الإنجاز" else "Earn Diamonds 💎 & Gold 🪙 based on challenge level",
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Currency Counter Cards Bar (Gold & Diamonds)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Gold Card
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, DesertGold.copy(alpha = 0.8f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(if (isAr) "العملات الذهبية" else "Gold Coins", color = Color.Gray, fontSize = 10.sp)
                                Text("$goldCoins 🪙", color = DesertGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }

                    // Diamonds Card
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, OasisTeal),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(if (isAr) "الألماس الأسطوري" else "Diamonds", color = OasisTeal, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("$diamonds 💎", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Achievements List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayAchievements, key = { it.id }) { achievement ->
                        val isClaimed = claimedAchievementIds.contains(achievement.id)
                        val isUnlocked = achievement.isUnlocked
                        val isHard = achievement.difficulty == "HARD" || achievement.difficulty == "LEGENDARY"

                        val cardBorderColor = remember(isClaimed, isUnlocked, isHard) {
                            when {
                                isClaimed -> Color.DarkGray
                                isUnlocked && isHard -> OasisTeal
                                isUnlocked -> DesertGold
                                else -> Color.Gray.copy(alpha = 0.4f)
                            }
                        }

                        val cardBgColor = remember(isClaimed, isUnlocked, isHard) {
                            when {
                                isClaimed -> Color.Black.copy(alpha = 0.3f)
                                isUnlocked && isHard -> OasisTeal.copy(alpha = 0.12f)
                                isUnlocked -> DesertGold.copy(alpha = 0.12f)
                                else -> Color.Black.copy(alpha = 0.5f)
                            }
                        }

                        Surface(
                            color = cardBgColor,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, cardBorderColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Achievement Icon & Details
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isHard) OasisTeal.copy(alpha = 0.2f) else DesertGold.copy(alpha = 0.2f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (isHard) "💎" else if (isUnlocked) "🏆" else "🔒",
                                            fontSize = 20.sp
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = if (isAr) achievement.titleAr else achievement.titleEn,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )

                                            // Difficulty Badge
                                            Surface(
                                                color = if (isHard) Color(0xFFD32F2F) else Color(0xFF388E3C),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = when (achievement.difficulty) {
                                                        "HARD", "LEGENDARY" -> if (isAr) "صعب ⚡" else "Hard ⚡"
                                                        "MEDIUM" -> if (isAr) "متوسط 🌟" else "Medium 🌟"
                                                        else -> if (isAr) "سهل ✨" else "Easy ✨"
                                                    },
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = if (isAr) achievement.descriptionAr else achievement.descriptionEn,
                                            color = Color.LightGray,
                                            fontSize = 11.sp
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Reward Preview
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (isAr) "المكافأة:" else "Reward:",
                                                color = Color.Gray,
                                                fontSize = 10.sp
                                            )
                                            if (achievement.rewardDiamonds > 0) {
                                                Text(
                                                    text = "+${achievement.rewardDiamonds} ألماس 💎",
                                                    color = OasisTeal,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                )
                                            }
                                            if (achievement.rewardGold > 0) {
                                                Text(
                                                    text = "+${achievement.rewardGold} ذهب 🪙",
                                                    color = DesertGold,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Claim / Status Button
                                if (isClaimed) {
                                    Surface(
                                        color = Color.Gray.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = if (isAr) "تم الاستلام ✅" else "Claimed ✅",
                                            color = Color.Gray,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        )
                                    }
                                } else if (isUnlocked) {
                                    Button(
                                        onClick = { onClaimReward(achievement) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isHard) OasisTeal else DesertGold,
                                            contentColor = Color.Black
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (isAr) "استلام 🎁" else "Claim 🎁",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 12.sp
                                        )
                                    }
                                } else {
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color.DarkGray)
                                    ) {
                                        Text(
                                            text = if (isAr) "مقفل 🔒" else "Locked 🔒",
                                            color = Color.Gray,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(containerColor = DesertGold, contentColor = DesertObsidian),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAr) "إغلاق والعودة ⚔️" else "Close & Return ⚔️", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
