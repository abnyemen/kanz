package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.game.AppLanguage
import com.example.game.LootChestState
import com.example.game.LootRewardItem
import com.example.ui.theme.DesertCrimson
import com.example.ui.theme.DesertGold
import com.example.ui.theme.DesertObsidian
import com.example.ui.theme.DesertSand
import com.example.ui.theme.OasisTeal

@Composable
fun LootChestDialog(
    language: AppLanguage,
    chestState: LootChestState,
    onClaimLoot: () -> Unit,
    onClose: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC

    val infiniteTransition = rememberInfiniteTransition(label = "chest_glow_transition")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "chest_glow_scale"
    )

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, DesertGold, RoundedCornerShape(24.dp))
                .testTag("loot_chest_dialog"),
            color = DesertObsidian
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                DesertObsidian,
                                Color(0xFF1E1610),
                                DesertObsidian
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Header & Close Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = DesertGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, DesertGold)
                        ) {
                            Text(
                                text = when (chestState.chestType) {
                                    "gold" -> if (isAr) "👑 صندوق الفرعون الذهبي" else "👑 Pharaoh Golden Chest"
                                    "legendary" -> if (isAr) "✨ صندوق الكنز الأسطوري" else "✨ Legendary Treasure Chest"
                                    "silver" -> if (isAr) "🛡️ صندوق الاستكشاف الفضي" else "🛡️ Silver Explorer Chest"
                                    else -> if (isAr) "📦 صندوق غنائم الصحراء" else "📦 Desert Loot Crate"
                                },
                                color = DesertGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .testTag("loot_chest_close_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Animated Glowing Chest Icon
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(100.dp)
                            .scale(glowScale)
                    ) {
                        Surface(
                            modifier = Modifier.size(90.dp),
                            shape = CircleShape,
                            color = DesertGold.copy(alpha = 0.15f),
                            border = BorderStroke(2.dp, DesertGold.copy(alpha = 0.6f))
                        ) {}

                        Surface(
                            modifier = Modifier.size(72.dp),
                            shape = CircleShape,
                            color = DesertGold.copy(alpha = 0.3f)
                        ) {}

                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = "Chest",
                            tint = DesertGold,
                            modifier = Modifier.size(52.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isAr) chestState.titleAr else chestState.titleEn,
                        color = DesertGold,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = if (isAr) "حصلت على الكنوز والغنائم التالية!" else "You found the following loot rewards!",
                        color = Color.LightGray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    HorizontalDivider(color = DesertGold.copy(alpha = 0.3f), thickness = 1.dp)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Total Gold Card
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, DesertGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = DesertGold,
                                    modifier = Modifier.size(28.dp)
                                )
                                Column {
                                    Text(
                                        text = if (isAr) "قطع الذهب النادرة" else "Rare Gold Coins",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = if (isAr) "تُضاف مباشرة لرصيدك" else "Added to balance",
                                        color = Color.Gray,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Text(
                                text = "+${chestState.totalGold} 🪙",
                                color = DesertGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // List of Item Rewards
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(chestState.rewards) { reward ->
                            LootRewardItemRow(isAr = isAr, item = reward)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Claim Button
                    Button(
                        onClick = onClaimLoot,
                        colors = ButtonDefaults.buttonColors(containerColor = DesertGold, contentColor = DesertObsidian),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("claim_loot_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "جمع الكل إلى الحقيبة 🎒" else "Claim All to Backpack 🎒",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LootRewardItemRow(
    isAr: Boolean,
    item: LootRewardItem
) {
    val rarityColor = when (item.rarity.lowercase()) {
        "legendary" -> DesertGold
        "epic" -> Color(0xFF9C27B0)
        "rare" -> OasisTeal
        else -> DesertSand
    }

    Surface(
        color = Color.Black.copy(alpha = 0.4f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, rarityColor.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = rarityColor.copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = item.iconEmoji, fontSize = 18.sp)
                    }
                }

                Column {
                    Text(
                        text = if (isAr) item.nameAr else item.nameEn,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = when (item.rarity.lowercase()) {
                            "legendary" -> if (isAr) "عنصر أسطوري ✨" else "Legendary Item ✨"
                            "epic" -> if (isAr) "عنصر فاخر 💎" else "Epic Item 💎"
                            "rare" -> if (isAr) "عنصر نادر 🌟" else "Rare Item 🌟"
                            else -> if (isAr) "عنصر شائع 🛠️" else "Common Item 🛠️"
                        },
                        color = rarityColor,
                        fontSize = 11.sp
                    )
                }
            }

            Text(
                text = "x${item.quantity}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}
