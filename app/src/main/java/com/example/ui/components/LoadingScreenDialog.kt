package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.AppLanguage
import com.example.ui.theme.DesertGold
import com.example.ui.theme.DesertObsidian
import com.example.ui.theme.OasisTeal
import kotlinx.coroutines.delay

@Composable
fun LoadingScreenDialog(
    language: AppLanguage,
    onLoadingComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC

    var progress by remember { mutableFloatStateOf(0f) }

    // Progress animation loop from 0% to 100%
    LaunchedEffect(Unit) {
        progress = 0f
        while (progress < 1.0f) {
            delay(40)
            progress += 0.015f
        }
        progress = 1.0f
    }

    // Rotating sun disc animation
    val infiniteTransition = rememberInfiniteTransition(label = "loading_rotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Desert Lore tips
    val tipsAr = remember {
        listOf(
            "نصيحة: استخدم الشعلة ليلاً للتخلص من المومياوات والوحوش الصحراوية.",
            "نصيحة: اشرب من الواحة أو قارورة الماء بانتظام لتجنب الجفاف.",
            "نصيحة: اعثر على جميع المفاتيح الأربعة لفتح بوابة المعبد الكبير.",
            "نصيحة: يمكنك امتلاء الذهب من الصناديق القديمة المنتشرة بين الكثبان.",
            "نصيحة: اصنع الأدوات والمشاعل من الصوان والنباتات الصحراوية."
        )
    }

    val tipsEn = remember {
        listOf(
            "Tip: Use the torch at night to scare away mummies and desert beasts.",
            "Tip: Drink regularly from oasis wells or water flasks to prevent dehydration.",
            "Tip: Find all 4 Civilization Keys to unlock the Great Temple Gate.",
            "Tip: Collect gold coins from ancient chests hidden among the dunes.",
            "Tip: Craft essential tools and torches using flint and desert herbs."
        )
    }

    val currentTipIndex = remember { (0 until tipsAr.size).random() }
    val currentTip = if (isAr) tipsAr[currentTipIndex] else tipsEn[currentTipIndex]

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF120B04),
                        DesertObsidian,
                        Color(0xFF1E1108)
                    )
                )
            )
            .testTag("loading_screen_dialog")
    ) {
        // --- 1. PYRAMID AND DUNE CANVAS ARTWORK ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Glowing Sun / Sun Disc behind Pyramid
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        DesertGold.copy(alpha = 0.5f),
                        DesertGold.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.38f),
                    radius = w * 0.4f
                ),
                center = Offset(w * 0.5f, h * 0.38f),
                radius = w * 0.4f
            )

            // Great Central Pyramid
            val pyramidPath = Path().apply {
                moveTo(w * 0.5f, h * 0.22f) // Apex
                lineTo(w * 0.15f, h * 0.52f) // Bottom Left
                lineTo(w * 0.85f, h * 0.52f) // Bottom Right
                close()
            }
            drawPath(
                path = pyramidPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DesertGold,
                        Color(0xFF8B6B23),
                        Color(0xFF3D2C0C)
                    ),
                    startY = h * 0.22f,
                    endY = h * 0.52f
                )
            )

            // Pyramid Shadow Side
            val shadowSidePath = Path().apply {
                moveTo(w * 0.5f, h * 0.22f)
                lineTo(w * 0.5f, h * 0.52f)
                lineTo(w * 0.85f, h * 0.52f)
                close()
            }
            drawPath(
                path = shadowSidePath,
                color = Color.Black.copy(alpha = 0.35f)
            )

            // Golden Capstone Glow (Pyramid Top)
            drawCircle(
                color = Color.White.copy(alpha = pulseAlpha),
                radius = 12f,
                center = Offset(w * 0.5f, h * 0.22f)
            )

            // Sand Dune Base Wave
            val dunePath = Path().apply {
                moveTo(0f, h * 0.48f)
                cubicTo(w * 0.25f, h * 0.44f, w * 0.75f, h * 0.54f, w, h * 0.49f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = dunePath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2C1E0D),
                        DesertObsidian
                    )
                )
            )
        }

        // --- 2. LOADING UI CONTENT ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // Header Title & Logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Surface(
                    color = DesertGold.copy(alpha = 0.15f),
                    shape = CircleShape,
                    border = BorderStroke(2.dp, DesertGold),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .rotate(rotationAngle),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Brightness7,
                            contentDescription = null,
                            tint = DesertGold,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Text(
                    text = if (isAr) "أسرار الصحراء القديمة" else "SECRETS OF THE ANCIENT DESERT",
                    color = DesertGold,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isAr) "جاري تحميل العالم ثلاثي الأبعاد..." else "Loading 3D World...",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Bottom Progress Bar & Lore Box
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp)
            ) {

                // Game Lore Tip Card
                Surface(
                    color = DesertObsidian.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, DesertGold.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = DesertGold,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = currentTip,
                            color = Color.White,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Percentage Readout
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "اكتمال التحميل" else "Loading Progress",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        color = DesertGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // Progress Bar Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(Color.Black.copy(alpha = 0.8f))
                        .border(1.5.dp, DesertGold, RoundedCornerShape(9.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF8B6B23),
                                        DesertGold,
                                        OasisTeal
                                    )
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Enter Game Button when ready
                Button(
                    onClick = onLoadingComplete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DesertGold,
                        contentColor = DesertObsidian
                    ),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(2.dp, Color.White),
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(50.dp)
                        .testTag("enter_game_loading_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                        Text(
                            text = if (progress >= 1f) {
                                if (isAr) "إبدأ المغامرة الآن 🗡️" else "Start Adventure Now 🗡️"
                            } else {
                                if (isAr) "تخطي التحميل ⚡" else "Skip Loading ⚡"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
