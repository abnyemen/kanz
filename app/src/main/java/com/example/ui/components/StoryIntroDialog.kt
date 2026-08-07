package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun StoryIntroDialog(
    language: AppLanguage,
    onStartJourney: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC

    Dialog(onDismissRequest = onStartJourney) {
        Surface(
            color = DesertObsidian,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, DesertGold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("story_intro_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isAr) "كنز الصحراء 🏺" else "Desert Treasure 🏺",
                    color = DesertGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isAr)
                        "تبدأ رحلتك الأسطورية بوصولك إلى صحراء الفراعنة القديمة بعد حصولك على خريطة أثرية مفقودة تشير إلى كنز ثمني مدفون منذ آلاف السنين.\n\nاستكشف المعابد، اجمع مفاتيح الحضارة الأربعة، واجه المومياوات وقطاع الطرق، وافتح الهرم الأكبر للوصول إلى الكنز النهائي!"
                    else
                        "Your legendary journey begins upon arriving in the ancient Pharaohs' desert, guided by a lost mythical map pointing to a priceless treasure buried thousands of years ago.\n\nExplore temples, collect the 4 civilization keys, defeat mummies and raiders, and unlock the Great Pyramid to claim the ultimate treasure!",
                    color = Color.White,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onStartJourney,
                    colors = ButtonDefaults.buttonColors(containerColor = DesertGold, contentColor = DesertObsidian),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isAr) "ابدأ المغامرة الآن 🐪" else "Start Journey Now 🐪",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
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
