package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.game.AppLanguage
import com.example.game.TemplePuzzleState
import com.example.ui.theme.DesertGold
import com.example.ui.theme.DesertObsidian
import com.example.ui.theme.OasisTeal

@Composable
fun TemplePuzzleDialog(
    language: AppLanguage,
    puzzle: TemplePuzzleState,
    onSolve: (selectedIndex: Int) -> Unit,
    onClose: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC
    var selectedOption by remember { mutableIntStateOf(-1) }

    val targetRotation = when (selectedOption) {
        0 -> 0f
        1 -> 90f
        2 -> 180f
        3 -> 270f
        else -> 0f
    }

    val animatedRotation by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = tween(durationMillis = 500),
        label = "scarab_dial_rotation"
    )

    Dialog(onDismissRequest = onClose) {
        Surface(
            color = DesertObsidian,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(2.dp, DesertGold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("temple_puzzle_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Ancient Hieroglyph Emblem
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = DesertGold, modifier = Modifier.size(24.dp))
                    Text(
                        text = if (isAr) puzzle.titleAr else puzzle.titleEn,
                        color = DesertGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = DesertGold, modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Interactive Rotating Golden Scarab Seal
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .border(2.dp, DesertGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(animatedRotation),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "𓃣 𓆣 𓋹 a",
                            fontSize = 20.sp,
                            color = DesertGold,
                            textAlign = TextAlign.Center
                        )
                    }
                    Text(
                        text = "𓆣",
                        fontSize = 32.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isAr) puzzle.questionAr else puzzle.questionEn,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                val options = if (isAr) puzzle.optionsAr else puzzle.optionsEn
                options.forEachIndexed { index, optionText ->
                    OutlinedButton(
                        onClick = { selectedOption = index },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selectedOption == index) DesertGold.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.4f),
                            contentColor = if (selectedOption == index) DesertGold else Color.White
                        ),
                        border = BorderStroke(
                            width = if (selectedOption == index) 2.dp else 1.dp,
                            color = if (selectedOption == index) DesertGold else Color.DarkGray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("puzzle_option_$index")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (index) {
                                    0 -> "𓏺 $optionText"
                                    1 -> "𓏻 $optionText"
                                    2 -> "𓏼 $optionText"
                                    else -> "𓏽 $optionText"
                                },
                                fontSize = 14.sp,
                                fontWeight = if (selectedOption == index) FontWeight.Bold else FontWeight.Normal
                            )
                            if (selectedOption == index) {
                                Text("✨", fontSize = 14.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onClose) {
                        Text(if (isAr) "إلغاء" else "Cancel", color = Color.Gray)
                    }

                    Button(
                        onClick = {
                            if (selectedOption != -1) {
                                onSolve(selectedOption)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DesertGold, contentColor = DesertObsidian),
                        shape = RoundedCornerShape(10.dp),
                        enabled = selectedOption != -1
                    ) {
                        Text(if (isAr) "حل لغز الفراعنة 🔑" else "Solve Temple Mystery 🔑", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
