package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.game.AppLanguage
import com.example.game.TemplePuzzleState
import com.example.ui.theme.DesertGold
import com.example.ui.theme.DesertObsidian

@Composable
fun TemplePuzzleDialog(
    language: AppLanguage,
    puzzle: TemplePuzzleState,
    onSolve: (selectedIndex: Int) -> Unit,
    onClose: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC
    var selectedOption by remember { mutableIntStateOf(-1) }

    Dialog(onDismissRequest = onClose) {
        Surface(
            color = DesertObsidian,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, DesertGold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("temple_puzzle_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isAr) puzzle.titleAr else puzzle.titleEn,
                    color = DesertGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isAr) puzzle.questionAr else puzzle.questionEn,
                    color = Color.White,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                val options = if (isAr) puzzle.optionsAr else puzzle.optionsEn
                options.forEachIndexed { index, optionText ->
                    OutlinedButton(
                        onClick = { selectedOption = index },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selectedOption == index) DesertGold.copy(alpha = 0.2f) else Color.Transparent,
                            contentColor = if (selectedOption == index) DesertGold else Color.White
                        ),
                        border = BorderStroke(
                            width = if (selectedOption == index) 2.dp else 1.dp,
                            color = if (selectedOption == index) DesertGold else Color.Gray
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("puzzle_option_$index")
                    ) {
                        Text(text = optionText, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
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
                        shape = RoundedCornerShape(8.dp),
                        enabled = selectedOption != -1
                    ) {
                        Text(if (isAr) "تأكيد الإجابة 🔑" else "Confirm Key 🔑", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
