package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCart
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

data class ShopItem(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val descEn: String,
    val descAr: String,
    val cost: Int,
    val currency: String = "GOLD", // "GOLD" or "DIAMONDS"
    val iconEmoji: String,
    val itemType: String
)

@Composable
fun DesertShopDialog(
    language: AppLanguage,
    playerGold: Int,
    playerDiamonds: Int,
    onBuyItem: (itemId: String, nameEn: String, nameAr: String, cost: Int, currency: String, icon: String, itemType: String) -> Unit,
    onClose: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC
    var selectedCategory by remember { mutableStateOf("ALL") } // "ALL", "GOLD", "DIAMONDS"

    val shopItems = remember {
        listOf(
            // --- GOLD ITEMS ---
            ShopItem(
                id = "potion_health",
                nameEn = "Pharaoh Healing Elixir",
                nameAr = "إكسير الشفاء الفرعوني 🧪",
                descEn = "Restores +50 Health instantly.",
                descAr = "يعيد +50 نقطة صحة فوراً.",
                cost = 25,
                currency = "GOLD",
                iconEmoji = "🧪",
                itemType = "food"
            ),
            ShopItem(
                id = "potion_water",
                nameEn = "Oasis Pure Water Flask",
                nameAr = "قارورة ماء الواحة 💧",
                descEn = "Restores +50 Hydration instantly.",
                descAr = "تعيد +50 نقطة ارتواء فوراً.",
                cost = 15,
                currency = "GOLD",
                iconEmoji = "💧",
                itemType = "water"
            ),
            ShopItem(
                id = "saddle_camel",
                nameEn = "Desert Camel Saddle",
                nameAr = "سرج الجمل الصحراوي 🐪",
                descEn = "Unlocks the Camel Mount for fast desert travel.",
                descAr = "يفتح ركوب الجمل للتنقل السريع عبر الكثبان.",
                cost = 50,
                currency = "GOLD",
                iconEmoji = "🐪",
                itemType = "mount"
            ),
            ShopItem(
                id = "bridle_horse",
                nameEn = "Royal Arabian Horse Bridle",
                nameAr = "لجام الحصان العربي الأصيل 🐎",
                descEn = "Unlocks the Royal Arabian Horse Mount.",
                descAr = "يفتح ركوب الحصان العربي السريع جداً.",
                cost = 100,
                currency = "GOLD",
                iconEmoji = "🐎",
                itemType = "mount"
            ),
            ShopItem(
                id = "desert_torch",
                nameEn = "Bedouin Night Torch",
                nameAr = "مشعل البدو الاستكشافي 🔥",
                descEn = "Illuminates dark ancient tombs.",
                descAr = "يضيء المقابر المظلمة ويوفر الحماية ليلاً.",
                cost = 30,
                currency = "GOLD",
                iconEmoji = "🔥",
                itemType = "torch"
            ),
            ShopItem(
                id = "key_fragment",
                nameEn = "Ancient Temple Key",
                nameAr = "مفتاح أثري نادِر 🔑",
                descEn = "Counts towards unlocking the Great Pyramid.",
                descAr = "يحتسب كـ مفتاح أثري لفتح بوابة الهرم الأكبر.",
                cost = 120,
                currency = "GOLD",
                iconEmoji = "🔑",
                itemType = "key"
            ),
            ShopItem(
                id = "amulet_anubis",
                nameEn = "Protection Amulet",
                nameAr = "تميمة الوقاية الفرعونية 🧿",
                descEn = "Increases resistance against desert raiders.",
                descAr = "تزيد المقاومة ضد المومياوات وقطاع الطرق.",
                cost = 60,
                currency = "GOLD",
                iconEmoji = "🧿",
                itemType = "potion"
            ),

            // --- DIAMOND PREVIOUS / VALUABLE ITEMS ---
            ShopItem(
                id = "skin_pharaoh",
                nameEn = "Golden Pharaoh Royal Skin",
                nameAr = "زي الفارس الفرعوني الذهبي 🤴",
                descEn = "Precious royal armor outfit forged in gold.",
                descAr = "ملابس فرعون ملكية فاخرة تمنح هيبة الفراعنة.",
                cost = 20,
                currency = "DIAMONDS",
                iconEmoji = "🤴",
                itemType = "skin"
            ),
            ShopItem(
                id = "skin_guardian",
                nameEn = "Oasis Temple Guardian Skin",
                nameAr = "مظهر حارس الواحة الأسطوري 🧙‍♂️",
                descEn = "Ancient mystic skin with glowing runes.",
                descAr = "ملابس أسطورية حاصدة للطاقة والطلاسم الفرعونية.",
                cost = 15,
                currency = "DIAMONDS",
                iconEmoji = "🧙‍♂️",
                itemType = "skin"
            ),
            ShopItem(
                id = "sword_golden",
                nameEn = "Tutankhamun Golden Blade",
                nameAr = "سيف توت عنخ آمون الذهبي ⚔️",
                descEn = "Legendary sword deals massive critical damage.",
                descAr = "سيف فرعوني نادِر يقطع الأعداء بلمسة واحدة.",
                cost = 10,
                currency = "DIAMONDS",
                iconEmoji = "⚔️",
                itemType = "weapon"
            ),
            ShopItem(
                id = "elixir_immortal",
                nameEn = "Elixir of the Gods",
                nameAr = "إكسير الفراعنة الشامل 🧪✨",
                descEn = "Fully restores Health, Hydration & Stamina.",
                descAr = "يعيد الصحة، الارتواء، والتحمل بالكامل فوراً.",
                cost = 3,
                currency = "DIAMONDS",
                iconEmoji = "🧪",
                itemType = "potion"
            )
        )
    }

    val filteredItems = remember(selectedCategory, shopItems) {
        when (selectedCategory) {
            "GOLD" -> shopItems.filter { it.currency == "GOLD" }
            "DIAMONDS" -> shopItems.filter { it.currency == "DIAMONDS" }
            else -> shopItems
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
                .testTag("desert_shop_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header Bar with Currencies & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = DesertGold, modifier = Modifier.size(24.dp))
                        Text(
                            text = if (isAr) "سوق البدو والمتجر 🛒" else "Bedouin Bazaar 🛒",
                            color = DesertGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }

                    // Balance Badges
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, DesertGold)
                        ) {
                            Text(
                                text = "💰 $playerGold",
                                color = DesertGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, OasisTeal)
                        ) {
                            Text(
                                text = "💎 $playerDiamonds",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Currency Category Filters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedCategory == "ALL",
                        onClick = { selectedCategory = "ALL" },
                        label = { Text(if (isAr) "الكل 📦" else "All 📦", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DesertGold,
                            selectedLabelColor = DesertObsidian
                        )
                    )

                    FilterChip(
                        selected = selectedCategory == "GOLD",
                        onClick = { selectedCategory = "GOLD" },
                        label = { Text(if (isAr) "بالذهب 🪙" else "Gold 🪙", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DesertGold,
                            selectedLabelColor = DesertObsidian
                        )
                    )

                    FilterChip(
                        selected = selectedCategory == "DIAMONDS",
                        onClick = { selectedCategory = "DIAMONDS" },
                        label = { Text(if (isAr) "بالألماس 💎" else "Diamonds 💎", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OasisTeal,
                            selectedLabelColor = Color.Black
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Grid of Shop Items
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredItems) { item ->
                        val isDiamond = item.currency == "DIAMONDS"
                        val canAfford = if (isDiamond) playerDiamonds >= item.cost else playerGold >= item.cost

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDiamond) OasisTeal.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.5f)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (canAfford) (if (isDiamond) OasisTeal else DesertGold.copy(alpha = 0.7f)) else Color.DarkGray
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (isDiamond) OasisTeal.copy(alpha = 0.25f) else DesertGold.copy(alpha = 0.15f))
                                        .border(1.dp, if (isDiamond) OasisTeal else DesertGold, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(item.iconEmoji, fontSize = 22.sp)
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = if (isAr) item.nameAr else item.nameEn,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )

                                Text(
                                    text = if (isAr) item.descAr else item.descEn,
                                    color = Color.LightGray,
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                Button(
                                    onClick = {
                                        onBuyItem(item.id, item.nameEn, item.nameAr, item.cost, item.currency, item.iconEmoji, item.itemType)
                                    },
                                    enabled = canAfford,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isDiamond) OasisTeal else DesertGold,
                                        contentColor = Color.Black,
                                        disabledContainerColor = Color.DarkGray,
                                        disabledContentColor = Color.Gray
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(34.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(if (isDiamond) "💎 ${item.cost}" else "💰 ${item.cost}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(if (isAr) "شراء" else "Buy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
