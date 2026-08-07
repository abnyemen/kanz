package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.InventoryItem
import com.example.game.AppLanguage
import com.example.game.CraftingRecipe
import com.example.game.CraftingRecipes
import com.example.ui.theme.DesertCrimson
import com.example.ui.theme.DesertGold
import com.example.ui.theme.DesertObsidian
import com.example.ui.theme.DesertSand
import com.example.ui.theme.OasisTeal

private enum class InventoryMode {
    BACKPACK, CRAFTING
}

private enum class InventoryCategoryTab {
    ALL, ARTIFACTS, FOOD_WATER, TOOLS, MATERIALS
}

@Composable
fun InventoryDialog(
    language: AppLanguage,
    itemsList: List<InventoryItem>,
    onUseOrEquip: (InventoryItem) -> Unit = {},
    onDrop: (InventoryItem) -> Unit = {},
    onCraftRecipe: (CraftingRecipe) -> Unit = {},
    onClose: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC
    var currentMode by remember { mutableStateOf(InventoryMode.BACKPACK) }
    var selectedTab by remember { mutableStateOf(InventoryCategoryTab.ALL) }

    val filteredItems = remember(itemsList, selectedTab) {
        itemsList.filter { item ->
            when (selectedTab) {
                InventoryCategoryTab.ALL -> true
                InventoryCategoryTab.ARTIFACTS -> item.itemType == "artifact" || item.itemType == "key" || item.iconName == "map" || item.iconName == "artifact"
                InventoryCategoryTab.FOOD_WATER -> item.itemType == "food" || item.itemType == "water" || item.itemType == "potion" || item.iconName == "food" || item.iconName == "water"
                InventoryCategoryTab.TOOLS -> item.itemType == "tool" || item.itemType == "weapon" || item.itemType == "torch" || item.iconName == "weapon" || item.iconName == "torch"
                InventoryCategoryTab.MATERIALS -> item.itemType == "material" || item.iconName == "material"
            }
        }
    }

    Dialog(onDismissRequest = onClose) {
        Surface(
            color = DesertObsidian,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, DesertGold),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(4.dp)
                .testTag("inventory_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (currentMode == InventoryMode.BACKPACK) Icons.Default.Backpack else Icons.Default.Build,
                            contentDescription = null,
                            tint = DesertGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentMode == InventoryMode.BACKPACK) {
                                if (isAr) "حقيبة المستكشف 🎒" else "Explorer Backpack 🎒"
                            } else {
                                if (isAr) "ورشة التصنيع الصحراوية 🛠️" else "Desert Crafting Bench 🛠️"
                            },
                            color = DesertGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("inventory_close_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DesertGold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- TOP MODE SEGMENTED TOGGLE (BACKPACK vs CRAFTING) ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E120A))
                        .border(1.dp, DesertGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = { currentMode = InventoryMode.BACKPACK },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentMode == InventoryMode.BACKPACK) DesertGold else Color.Transparent,
                            contentColor = if (currentMode == InventoryMode.BACKPACK) DesertObsidian else DesertGold
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("inventory_backpack_mode_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Backpack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "الحقيبة 🎒" else "Backpack 🎒",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = { currentMode = InventoryMode.CRAFTING },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentMode == InventoryMode.CRAFTING) DesertGold else Color.Transparent,
                            contentColor = if (currentMode == InventoryMode.CRAFTING) DesertObsidian else DesertGold
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("inventory_crafting_mode_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "التصنيع 🛠️" else "Crafting 🛠️",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // --- CONTENT BASED ON MODE ---
                if (currentMode == InventoryMode.BACKPACK) {
                    // --- TABBED CATEGORY SELECTION ---
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab.ordinal,
                        containerColor = Color(0xFF1E120A),
                        contentColor = DesertGold,
                        edgePadding = 0.dp,
                        divider = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, DesertGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .testTag("inventory_tab_row")
                    ) {
                        InventoryCategoryTab.entries.forEach { tab ->
                            val isSelected = selectedTab == tab
                            val tabTitle = when (tab) {
                                InventoryCategoryTab.ALL -> if (isAr) "الكل 📦" else "All 📦"
                                InventoryCategoryTab.ARTIFACTS -> if (isAr) "الآثار 🏺" else "Artifacts 🏺"
                                InventoryCategoryTab.FOOD_WATER -> if (isAr) "طعام وماء 💧" else "Food & Water 💧"
                                InventoryCategoryTab.TOOLS -> if (isAr) "أدوات ⚔️" else "Tools ⚔️"
                                InventoryCategoryTab.MATERIALS -> if (isAr) "خام 🧱" else "Materials 🧱"
                            }
                            Tab(
                                selected = isSelected,
                                onClick = { selectedTab = tab },
                                modifier = Modifier.testTag("inventory_tab_${tab.name.lowercase()}"),
                                text = {
                                    Text(
                                        text = tabTitle,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) DesertGold else Color.Gray
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // --- ITEM GRID LIST ---
                    if (filteredItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Inbox, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isAr) "لا توجد عناصر في هذا القسم" else "No items in this category",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(filteredItems) { item ->
                                InventoryItemCard(
                                    item = item,
                                    isAr = isAr,
                                    onUseOrEquip = { onUseOrEquip(item) },
                                    onDrop = { onDrop(item) }
                                )
                            }
                        }
                    }
                } else {
                    // --- CRAFTING BENCH VIEW ---
                    Text(
                        text = if (isAr) "دمج المواد المستخرجة من الصحراء لصناعة الأدوات البقاء:"
                        else "Combine collected desert materials to build survival essentials:",
                        color = DesertSand,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(CraftingRecipes.ALL_RECIPES) { recipe ->
                            CraftingRecipeCard(
                                recipe = recipe,
                                itemsList = itemsList,
                                isAr = isAr,
                                onCraft = { onCraftRecipe(recipe) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CraftingRecipeCard(
    recipe: CraftingRecipe,
    itemsList: List<InventoryItem>,
    isAr: Boolean,
    onCraft: () -> Unit
) {
    // Check if player has all ingredients in sufficient quantity
    val canCraft = remember(recipe, itemsList) {
        recipe.ingredients.all { ingredient ->
            val owned = itemsList.find { it.itemId == ingredient.itemId }
            owned != null && owned.quantity >= ingredient.requiredQuantity
        }
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E130B)
        ),
        border = BorderStroke(
            width = if (canCraft) 1.5.dp else 1.dp,
            color = if (canCraft) OasisTeal else DesertGold.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("craft_recipe_${recipe.recipeId}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Recipe Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (canCraft) OasisTeal.copy(alpha = 0.25f) else Color.DarkGray)
                            .border(1.dp, if (canCraft) OasisTeal else Color.Gray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val recipeIcon = when (recipe.resultIcon) {
                            "torch" -> Icons.Default.LocalFireDepartment
                            "weapon" -> Icons.Default.Handyman
                            "food" -> Icons.Default.Sanitizer
                            "tool" -> Icons.Default.Build
                            "water" -> Icons.Default.WaterDrop
                            else -> Icons.Default.Extension
                        }
                        Icon(
                            imageVector = recipeIcon,
                            contentDescription = null,
                            tint = if (canCraft) OasisTeal else Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isAr) recipe.nameAr else recipe.nameEn,
                            color = DesertGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isAr) recipe.descriptionAr else recipe.descriptionEn,
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color.DarkGray, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Ingredients Breakdown List
            Text(
                text = if (isAr) "المكونات المطلوبة:" else "Required Materials:",
                color = DesertSand,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            recipe.ingredients.forEach { ingredient ->
                val ownedCount = itemsList.find { it.itemId == ingredient.itemId }?.quantity ?: 0
                val hasEnough = ownedCount >= ingredient.requiredQuantity

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (hasEnough) "✅ " else "❌ ",
                            fontSize = 11.sp
                        )
                        Text(
                            text = if (isAr) ingredient.nameAr else ingredient.nameEn,
                            color = if (hasEnough) Color.White else Color.Gray,
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        text = "$ownedCount / ${ingredient.requiredQuantity}",
                        color = if (hasEnough) OasisTeal else DesertCrimson,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Craft Button
            Button(
                onClick = onCraft,
                enabled = canCraft,
                colors = ButtonDefaults.buttonColors(
                    containerColor = OasisTeal,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF2B221B),
                    disabledContentColor = Color.Gray
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("craft_button_${recipe.recipeId}")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (canCraft) {
                            if (isAr) "تصنيع العنصر الآن 🛠️" else "Craft Item Now 🛠️"
                        } else {
                            if (isAr) "المواد غير مكتملة ❌" else "Materials Missing ❌"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun InventoryItemCard(
    item: InventoryItem,
    isAr: Boolean,
    onUseOrEquip: () -> Unit,
    onDrop: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (item.isEquipped) Color(0xFF3B2412) else Color(0xFF22150B)
        ),
        border = BorderStroke(
            width = if (item.isEquipped) 2.dp else 1.dp,
            color = if (item.isEquipped) DesertGold else DesertSand.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("inventory_item_${item.itemId}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon + Equipped badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DesertGold.copy(alpha = 0.15f))
                    .border(1.dp, DesertGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val iconVector: ImageVector = when (item.iconName) {
                    "map" -> Icons.Default.Map
                    "artifact" -> Icons.Default.AutoAwesome
                    "water" -> Icons.Default.WaterDrop
                    "food" -> Icons.Default.Restaurant
                    "weapon" -> Icons.Default.Shield
                    "torch" -> Icons.Default.LocalFireDepartment
                    "material" -> Icons.Default.Category
                    else -> Icons.Default.Extension
                }
                Icon(
                    imageVector = iconVector,
                    contentDescription = item.nameEn,
                    tint = DesertGold,
                    modifier = Modifier.size(24.dp)
                )

                if (item.isEquipped) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(DesertGold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Item Name
            Text(
                text = if (isAr) item.nameAr else item.nameEn,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Quantity
            Text(
                text = if (isAr) "الكمية: ${item.quantity}" else "Qty: ${item.quantity}",
                color = DesertGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = if (isAr) item.descriptionAr else item.descriptionEn,
                color = Color.LightGray,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (item.itemType != "material") {
                    Button(
                        onClick = onUseOrEquip,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (item.isEquipped) OasisTeal else DesertGold,
                            contentColor = DesertObsidian
                        ),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp)
                            .testTag("item_use_button_${item.itemId}")
                    ) {
                        val btnText = if (item.isEquipped) {
                            if (isAr) "مجهز" else "Equipped"
                        } else {
                            when (item.itemType) {
                                "food" -> if (isAr) "أكل" else "Eat"
                                "water", "potion" -> if (isAr) "شرب" else "Drink"
                                else -> if (isAr) "تجهيز" else "Equip"
                            }
                        }
                        Text(btnText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = onDrop,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DesertCrimson
                    ),
                    border = BorderStroke(1.dp, DesertCrimson),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                        .testTag("item_drop_button_${item.itemId}")
                ) {
                    Text(if (isAr) "إسقاط" else "Drop", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
