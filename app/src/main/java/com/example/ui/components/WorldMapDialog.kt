package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.game.AppLanguage
import com.example.ui.theme.DesertGold
import com.example.ui.theme.DesertObsidian
import com.example.ui.theme.DesertSand

data class MapLocation(
    val nameEn: String,
    val nameAr: String,
    val descriptionEn: String,
    val descriptionAr: String,
    val posX: Float,
    val posZ: Float
)

val DesertLocations = listOf(
    MapLocation("Palm Oasis", "واحة النخيل", "Lush oasis spring with shade and fresh water", "نبع واحة غناء مع مائي نقي وظلال نخيل", -40f, 50f),
    MapLocation("Temple of Horus", "معبد حورس", "Ancient temple dedicated to Horus the Falcon deity", "معبد أثري عتيق لمحراب صقر حورس", 80f, 110f),
    MapLocation("Temple of Anubis", "معبد أنوبيس", "Guardians' tomb with sacred balances", "مقبرة الحراس ومحراب ميزان أنوبيس", -90f, 100f),
    MapLocation("Bandit Stronghold", "معقل قطاع الطرق", "Desert raiders camp hidden in sandstone canyons", "مخيم قطاع الطرق بين أودية الصخور", -70f, -80f),
    MapLocation("The Great Pyramid", "الهرم الأكبر", "Monumental tomb holding the Legendary Desert Treasure", "الصرح العظيم المشتمل على كنز الصحراء الأسطوري", 0f, 180f)
)

@Composable
fun WorldMapDialog(
    language: AppLanguage,
    onClose: () -> Unit,
    onFastTravel: (posX: Float, posZ: Float) -> Unit
) {
    val isAr = language == AppLanguage.ARABIC

    Dialog(onDismissRequest = onClose) {
        Surface(
            color = DesertObsidian,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, DesertGold),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(8.dp)
                .testTag("world_map_dialog")
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
                    Text(
                        text = if (isAr) "خريطة الصحراء الكبرى 🗺️" else "Great Desert Map 🗺️",
                        color = DesertGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DesertGold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Location list grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(DesertLocations) { loc ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2B1B0E)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DesertSand),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isAr) loc.nameAr else loc.nameEn,
                                        color = DesertGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = if (isAr) loc.descriptionAr else loc.descriptionEn,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 12.sp
                                    )
                                }
                                Button(
                                    onClick = {
                                        onFastTravel(loc.posX, loc.posZ)
                                        onClose()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DesertGold, contentColor = DesertObsidian),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isAr) "انتقال" else "Travel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
