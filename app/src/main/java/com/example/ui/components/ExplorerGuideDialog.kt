package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

data class GuideTopic(
    val titleAr: String,
    val titleEn: String,
    val iconEmoji: String,
    val descAr: String,
    val descEn: String
)

@Composable
fun ExplorerGuideDialog(
    language: AppLanguage,
    onClose: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC

    val topics = listOf(
        GuideTopic(
            titleAr = "الحركة والتحكم بالكاميرا 🕹️",
            titleEn = "Movement & Camera Orbit 🕹️",
            iconEmoji = "🕹️",
            descAr = "استخدم عصا التحكم (Joystick) للتحرك في جميع الاتجاهات. اسحب الجانب الأيمن من الشاشة للتدوير والتكبير والتصغير 360 درجة حول البطل.",
            descEn = "Use the virtual joystick to move in all directions. Drag on the right side of the screen to orbit camera and zoom in 360°."
        ),
        GuideTopic(
            titleAr = "ركوب الجمل والاندفاعة الفائقة 🐪⚡",
            titleEn = "Riding Camels & Speed Boost 🐪⚡",
            iconEmoji = "🐪",
            descAr = "اضغط زر الجمل 🐪 لامتطاء المركوب وتجاوز المساحات الصحراوية الشاسعة بسرعة مضاعفة، واضغط زر الاندفاعة لتفعيل السرعة الفائقة.",
            descEn = "Tap the Camel button 🐪 to ride mounts across vast sand dunes, and use the boost button for extreme desert sprint speed."
        ),
        GuideTopic(
            titleAr = "استدعاء الصقر الكشاف 🦅",
            titleEn = "Calling Scout Falcon Vision 🦅",
            iconEmoji = "🦅",
            descAr = "زر الصقر 🦅 يطلق صقراً صحراوياً يكتشف أماكن الكنوز والمفاتيح الأثرية القريبة ويميزها على الخريطة.",
            descEn = "The Falcon button 🦅 launches a desert hawk revealing nearby treasure chests and key altars on your radar."
        ),
        GuideTopic(
            titleAr = "حل ألغاز المعابد وجمع المفاتيح 🔑",
            titleEn = "Temple Puzzles & Civilization Keys 🔑",
            iconEmoji = "🔑",
            descAr = "اقترب من محاريب المعابد لحل الألغاز والفرعونية والإجابة على الأسئلة لجمع مفاتيح الحضارة الأربعة لفتح الهرم الأكبر.",
            descEn = "Approach ancient temple altars to solve hieroglyphic puzzles and answer riddles to claim all 4 civilization keys."
        ),
        GuideTopic(
            titleAr = "القتال وسيف الصحراء ⚔️",
            titleEn = "Combat & Weaponry ⚔️",
            iconEmoji = "⚔️",
            descAr = "استخدم زر الهجوم ⚔️ لضرب المومياوات وزعيم أنوبيس. احتفظ بمشعل البدو 🔥 لإضاءة المقابر وإبعاد الوحوش المفترسة.",
            descEn = "Use the attack button ⚔️ to slash mummies and Boss Anubis. Keep torches 🔥 lit inside dark tomb vaults."
        ),
        GuideTopic(
            titleAr = "السوق والخرائط والسفر السريع 🗺️🛒",
            titleEn = "Shop, Map & Fast Travel 🗺️🛒",
            iconEmoji = "🛒",
            descAr = "زر الخريطة 🗺️ يسمح لك بالسفر السريع إلى واحة النخيل والمعابد والمقابر فور فتحها. وزر المتجر 🛒 يتيح شراء المعدات والجرعات.",
            descEn = "The World Map 🗺️ allows instant fast travel to unlocked landmarks. Use the Shop 🛒 to buy potions and gear."
        )
    )

    Dialog(onDismissRequest = onClose) {
        Surface(
            color = DesertObsidian,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(2.dp, DesertGold),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(4.dp)
                .testTag("explorer_guide_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = DesertGold, modifier = Modifier.size(26.dp))
                        Text(
                            text = if (isAr) "دليل المستكشف ودليل اللعب 📖" else "Explorer's Handbook 📖",
                            color = DesertGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    topics.forEach { topic ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                            border = BorderStroke(1.dp, DesertGold.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(topic.iconEmoji, fontSize = 20.sp)
                                    Text(
                                        text = if (isAr) topic.titleAr else topic.titleEn,
                                        color = DesertGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = if (isAr) topic.descAr else topic.descEn,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(containerColor = DesertGold, contentColor = DesertObsidian),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAr) "فهمت! جاهز للمغامرة 🐪" else "Got It! Ready to Explore 🐪", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
