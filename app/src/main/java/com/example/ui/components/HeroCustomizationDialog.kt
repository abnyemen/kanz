package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
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
import com.example.ui.theme.DesertGold
import com.example.ui.theme.DesertObsidian
import com.example.ui.theme.OasisTeal

data class HeroSkinItem(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val emoji: String,
    val descEn: String,
    val descAr: String
)

data class MountOptionItem(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val emoji: String,
    val speedLabelEn: String,
    val speedLabelAr: String
)

@Composable
fun HeroCustomizationDialog(
    language: AppLanguage,
    currentSkin: String,
    currentMount: String,
    unlockedMounts: Set<String>,
    onSelectSkin: (String) -> Unit,
    onSelectMount: (String) -> Unit,
    onClose: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC

    val skins = remember {
        listOf(
            HeroSkinItem("nomad", "Bedouin Explorer", "رحالة البدو المغامر 👤", "🧕", "Agile desert survivalist", "مستكشف الصحراء الماهر والسريع"),
            HeroSkinItem("pharaoh", "Golden Pharaoh Knight", "الفارس الفرعوني الذهبي 👑", "🤴", "Legendary royal armor", "درع ملكي ذهبي مقاوم للصدمات"),
            HeroSkinItem("guardian", "Oasis Temple Guardian", "حارس الواحة الأسطوري 🛡️", "🧙‍♂️", "Ancient mystic defender", "حارس المعابد الأثرية والطلاسم")
        )
    }

    val mounts = remember {
        listOf(
            MountOptionItem("camel", "Desert Camel", "الجمل العربي 🐪", "🐪", "Fast Dunes Trot", "سريع على الكثبان الرملية"),
            MountOptionItem("horse", "Royal Stallion", "الحصان الملكي الأصيل 🐎", "🐎", "Ultra Sprint Speed", "سرعة ركض فائقة للغاية"),
            MountOptionItem("none", "On Foot", "ترجل (مشياً) 🚶", "🚶", "Standard Speed", "سرعة مشي عادية")
        )
    }

    Dialog(onDismissRequest = onClose) {
        Surface(
            color = DesertObsidian,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(2.dp, DesertGold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .testTag("hero_customization_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = DesertGold, modifier = Modifier.size(26.dp))
                        Text(
                            text = if (isAr) "تخصيص البطل والركوبة 👤🐪" else "Hero & Mount Specs 👤🐪",
                            color = DesertGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 1: Hero Skins
                Text(
                    text = if (isAr) "اختر مظهر الشخصية:" else "Choose Hero Outfit:",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    skins.forEach { skin ->
                        val isSelected = currentSkin == skin.id

                        Card(
                            onClick = { onSelectSkin(skin.id) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) DesertGold.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.5f)
                            ),
                            border = BorderStroke(1.dp, if (isSelected) DesertGold else Color.DarkGray),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(DesertGold.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(skin.emoji, fontSize = 22.sp)
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isAr) skin.nameAr else skin.nameEn,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = if (isAr) skin.descAr else skin.descEn,
                                        color = Color.Gray,
                                        fontSize = 11.sp
                                    )
                                }

                                if (isSelected) {
                                    Text("✅", fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2: Mount Selection
                Text(
                    text = if (isAr) "اختر الركوبة للتنقل السريع:" else "Choose Mount:",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    mounts.forEach { mount ->
                        val isSelected = currentMount == mount.id
                        val isUnlocked = unlockedMounts.contains(mount.id) || mount.id == "none"

                        Card(
                            onClick = {
                                if (isUnlocked) onSelectMount(mount.id)
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) OasisTeal.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.5f)
                            ),
                            border = BorderStroke(1.dp, if (isSelected) OasisTeal else if (isUnlocked) Color.DarkGray else Color.Red.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(mount.emoji, fontSize = 24.sp)

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isAr) mount.nameAr else mount.nameEn,
                                        color = if (isUnlocked) Color.White else Color.Gray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = if (isUnlocked) (if (isAr) mount.speedLabelAr else mount.speedLabelEn) else (if (isAr) "مقفل (يشترى من المتجر 🛒)" else "Locked (Buy in Shop 🛒)"),
                                        color = if (isUnlocked) OasisTeal else Color.Red,
                                        fontSize = 11.sp
                                    )
                                }

                                if (isSelected) {
                                    Text("✅", fontSize = 16.sp)
                                } else if (!isUnlocked) {
                                    Text("🔒", fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(containerColor = DesertGold, contentColor = DesertObsidian),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAr) "تأكيد وتجهيز ⚔️" else "Confirm & Equip ⚔️", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
