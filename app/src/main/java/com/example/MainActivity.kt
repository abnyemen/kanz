package com.example

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.game.GameViewModel
import com.example.ui.DesertGameScreen
import com.example.ui.theme.DesertObsidian
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Enable high refresh rate on supported devices (90Hz, 120Hz, 144Hz)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) display else windowManager.defaultDisplay
                display?.supportedModes?.maxByOrNull { it.refreshRate }?.let { maxMode ->
                    val lp = window.attributes
                    lp.preferredDisplayModeId = maxMode.modeId
                    window.attributes = lp
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DesertObsidian
                ) {
                    DesertGameScreen(viewModel = viewModel)
                }
            }
        }
    }
}

