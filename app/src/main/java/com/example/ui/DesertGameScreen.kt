package com.example.ui

import android.opengl.GLSurfaceView
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.engine.DesertGLRenderer
import com.example.game.ActiveDialogType
import com.example.game.GameViewModel
import com.example.ui.components.*
import com.example.ui.theme.DesertGold
import com.example.ui.theme.DesertObsidian

@Composable
fun DesertGameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val inventoryList by viewModel.inventory.collectAsStateWithLifecycle()
    val achievementsList by viewModel.achievements.collectAsStateWithLifecycle()
    val unlockedTemplesList by viewModel.unlockedTemples.collectAsStateWithLifecycle()
    val feedbackState by viewModel.feedbackManager.feedbackState.collectAsStateWithLifecycle()

    var glSurfaceView by remember { mutableStateOf<GLSurfaceView?>(null) }
    var renderer by remember { mutableStateOf<DesertGLRenderer?>(null) }

    // Frame ticker for FeedbackManager screen shake & particle physics
    LaunchedEffect(Unit) {
        var lastTime = System.nanoTime()
        while (true) {
            withFrameNanos { frameTime ->
                val dt = ((frameTime - lastTime) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastTime = frameTime
                viewModel.feedbackManager.update(dt)
            }
        }
    }

    // Toast message trigger
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    val isInLobby = remember(uiState.activeDialog) {
        uiState.activeDialog == ActiveDialogType.STORY_INTRO ||
                uiState.activeDialog == ActiveDialogType.DESERT_SHOP ||
                uiState.activeDialog == ActiveDialogType.HERO_CUSTOMIZATION ||
                uiState.activeDialog == ActiveDialogType.EXPLORER_GUIDE ||
                uiState.activeDialog == ActiveDialogType.ACHIEVEMENTS
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .offset(x = feedbackState.shakeOffsetX.dp, y = feedbackState.shakeOffsetY.dp)
            .testTag("desert_game_screen")
    ) {

        // --- 1. 3D GLSURFACEVIEW ---
        AndroidView(
            factory = { ctx ->
                GLSurfaceView(ctx).apply {
                    setEGLContextClientVersion(2)
                    val r = DesertGLRenderer(ctx, viewModel.world)
                    renderer = r
                    setRenderer(r)
                    renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                    glSurfaceView = this
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    // Right-side camera orbit drag gesture
                    detectTransformGestures { _, pan, zoom, _ ->
                        renderer?.let { r ->
                            r.cameraYaw += pan.x * 0.3f
                            r.cameraPitch = (r.cameraPitch - pan.y * 0.3f).coerceIn(5f, 65f)
                            if (zoom != 1f) {
                                r.cameraDistance = (r.cameraDistance / zoom).coerceIn(3f, 15f)
                            }
                        }
                    }
                }
        )

        // --- 2. MINIMAP OVERLAY (ONLY IN GAMEPLAY) ---
        if (!isInLobby && uiState.activeDialog != ActiveDialogType.LOADING_SCREEN) {
            MiniMap(
                playerPos = viewModel.world.playerPos,
                playerYaw = viewModel.world.playerYaw,
                entities = viewModel.world.entities,
                onClickMap = { viewModel.setDialog(ActiveDialogType.WORLD_MAP) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 70.dp, end = 12.dp)
            )
        }

        // --- 3. GAME HUD OVERLAY (ONLY IN GAMEPLAY) ---
        if (!isInLobby && uiState.activeDialog != ActiveDialogType.LOADING_SCREEN) {
            GameHudOverlay(
                uiState = uiState,
                unlockedTemples = unlockedTemplesList,
                onMoveInput = { dx, dy -> viewModel.onMoveInput(dx, dy) },
                onAttack = { viewModel.performAttack() },
                onJump = { viewModel.performJump() },
                onRoll = { viewModel.performRoll() },
                onToggleTorch = { viewModel.toggleTorch() },
                onDrinkWater = { viewModel.drinkWater() },
                onInteract = { viewModel.interactWithNearby() },
                onOpenDialog = { dialog -> viewModel.setDialog(dialog) },
                onToggleLanguage = { viewModel.toggleLanguage() },
                onFalconCall = { viewModel.triggerFalconCall() },
                onCamelBoost = { viewModel.triggerCamelSprint() }
            )
        }

        // --- 4. FULL-SCREEN LOBBY SCREEN (WHEN IN LOBBY STATE) ---
        if (isInLobby) {
            MainLobbyScreen(
                language = uiState.language,
                goldCoins = uiState.goldCoins,
                diamonds = uiState.diamonds,
                keysCount = uiState.keysCollectedCount,
                selectedHeroSkin = uiState.selectedHeroSkin,
                currentMount = uiState.currentMount,
                onStartJourney = { viewModel.setDialog(ActiveDialogType.LOADING_SCREEN) },
                onOpenShop = { viewModel.setDialog(ActiveDialogType.DESERT_SHOP) },
                onOpenCustomization = { viewModel.setDialog(ActiveDialogType.HERO_CUSTOMIZATION) },
                onOpenWorldMap = { viewModel.setDialog(ActiveDialogType.WORLD_MAP) },
                onOpenAchievements = { viewModel.setDialog(ActiveDialogType.ACHIEVEMENTS) },
                onOpenSettings = { viewModel.setDialog(ActiveDialogType.SETTINGS) },
                onOpenGuide = { viewModel.setDialog(ActiveDialogType.EXPLORER_GUIDE) },
                onToggleLanguage = { viewModel.toggleLanguage() }
            )
        }

        // --- 5. ACTIVE DIALOG OVERLAYS OVER GAME OR LOBBY ---
        when (uiState.activeDialog) {
            ActiveDialogType.WORLD_MAP -> {
                WorldMapDialog(
                    language = uiState.language,
                    onClose = {
                        viewModel.setDialog(if (isInLobby) ActiveDialogType.STORY_INTRO else ActiveDialogType.NONE)
                    },
                    onFastTravel = { px, pz ->
                        viewModel.world.playerPos.set(px, 0.5f, pz)
                    }
                )
            }

            ActiveDialogType.INVENTORY -> {
                InventoryDialog(
                    language = uiState.language,
                    itemsList = inventoryList,
                    onUseOrEquip = { item -> viewModel.useOrEquipItem(item) },
                    onDrop = { item -> viewModel.dropItem(item) },
                    onCraftRecipe = { recipe -> viewModel.craftItem(recipe) },
                    onClose = { viewModel.setDialog(ActiveDialogType.NONE) }
                )
            }

            ActiveDialogType.TEMPLE_PUZZLE -> {
                uiState.currentPuzzle?.let { puzzle ->
                    TemplePuzzleDialog(
                        language = uiState.language,
                        puzzle = puzzle,
                        onSolve = { index -> viewModel.solvePuzzle(index) },
                        onClose = { viewModel.setDialog(ActiveDialogType.NONE) }
                    )
                }
            }

            ActiveDialogType.LOOT_CHEST -> {
                uiState.currentLootChest?.let { chest ->
                    LootChestDialog(
                        language = uiState.language,
                        chestState = chest,
                        onClaimLoot = { viewModel.claimLoot() },
                        onClose = { viewModel.setDialog(ActiveDialogType.NONE) }
                    )
                }
            }

            ActiveDialogType.SETTINGS -> {
                SettingsDialog(
                    language = uiState.language,
                    graphicsQuality = uiState.graphicsQuality,
                    onSelectGraphicsQuality = { quality -> viewModel.setGraphicsQuality(quality) },
                    fps = viewModel.world.currentFps,
                    refreshRateHz = viewModel.world.deviceMaxHz,
                    sfxEnabled = uiState.sfxEnabled,
                    onToggleSfx = { enabled -> viewModel.setSfxEnabled(enabled) },
                    sfxVolume = uiState.sfxVolume,
                    onChangeSfxVolume = { vol -> viewModel.setSfxVolume(vol) },
                    musicVolume = uiState.musicVolume,
                    onChangeMusicVolume = { vol -> viewModel.setMusicVolume(vol) },
                    controlsScale = uiState.controlsScale,
                    onChangeControlsScale = { scale -> viewModel.setControlsScale(scale) },
                    joystickOnRight = uiState.joystickOnRight,
                    onToggleJoystickOnRight = { onRight -> viewModel.setJoystickOnRight(onRight) },
                    joystickSizeDp = uiState.joystickSizeDp,
                    onChangeJoystickSize = { size -> viewModel.setJoystickSize(size) },
                    actionButtonsScale = uiState.actionButtonsScale,
                    onChangeActionButtonsScale = { scale -> viewModel.setActionButtonsScale(scale) },
                    controlsBottomPaddingDp = uiState.controlsBottomPaddingDp,
                    onChangeControlsBottomPadding = { pad -> viewModel.setControlsBottomPadding(pad) },
                    controlsSidePaddingDp = uiState.controlsSidePaddingDp,
                    onChangeControlsSidePadding = { pad -> viewModel.setControlsSidePadding(pad) },
                    onResetControls = { viewModel.resetControlsToDefault() },
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onResetGame = { viewModel.resetGame() },
                    onClose = {
                        viewModel.setDialog(if (isInLobby) ActiveDialogType.STORY_INTRO else ActiveDialogType.NONE)
                    }
                )
            }

            ActiveDialogType.DESERT_SHOP -> {
                DesertShopDialog(
                    language = uiState.language,
                    playerGold = uiState.goldCoins,
                    playerDiamonds = uiState.diamonds,
                    onBuyItem = { id, nameEn, nameAr, cost, currency, icon, type ->
                        viewModel.buyShopItem(id, nameEn, nameAr, cost, currency, icon, type)
                    },
                    onClose = { viewModel.setDialog(ActiveDialogType.STORY_INTRO) }
                )
            }

            ActiveDialogType.ACHIEVEMENTS -> {
                AchievementsDialog(
                    language = uiState.language,
                    goldCoins = uiState.goldCoins,
                    diamonds = uiState.diamonds,
                    achievementsList = achievementsList,
                    claimedAchievementIds = uiState.claimedAchievementIds,
                    onClaimReward = { achievement -> viewModel.claimAchievementReward(achievement) },
                    onClose = { viewModel.setDialog(ActiveDialogType.STORY_INTRO) }
                )
            }

            ActiveDialogType.HERO_CUSTOMIZATION -> {
                HeroCustomizationDialog(
                    language = uiState.language,
                    currentSkin = uiState.selectedHeroSkin,
                    currentMount = uiState.currentMount,
                    unlockedMounts = uiState.unlockedMounts,
                    onSelectSkin = { skinId -> viewModel.selectHeroSkin(skinId) },
                    onSelectMount = { mountId -> viewModel.selectMount(mountId) },
                    onClose = { viewModel.setDialog(ActiveDialogType.STORY_INTRO) }
                )
            }

            ActiveDialogType.EXPLORER_GUIDE -> {
                ExplorerGuideDialog(
                    language = uiState.language,
                    onClose = { viewModel.setDialog(ActiveDialogType.STORY_INTRO) }
                )
            }

            ActiveDialogType.VICTORY_SCREEN -> {
                VictoryDialog(
                    language = uiState.language,
                    onContinueFreeRoam = { viewModel.setDialog(ActiveDialogType.NONE) }
                )
            }

            ActiveDialogType.LOADING_SCREEN -> {
                LoadingScreenDialog(
                    language = uiState.language,
                    onLoadingComplete = { viewModel.setDialog(ActiveDialogType.NONE) }
                )
            }

            ActiveDialogType.PAUSE_MENU -> {
                PauseMenuDialog(
                    uiState = uiState,
                    timeOfDayHours = viewModel.world.timeOfDayHours,
                    onResume = { viewModel.resumeGame() },
                    onQuickSave = { viewModel.performQuickSave() },
                    onOpenSettings = { viewModel.setDialog(ActiveDialogType.SETTINGS) },
                    onOpenStoryIntro = { viewModel.setDialog(ActiveDialogType.STORY_INTRO) },
                    onOpenLoadingScreen = { viewModel.setDialog(ActiveDialogType.LOADING_SCREEN) },
                    onShowTutorial = { viewModel.showTutorial() },
                    onBackToMainMenu = {
                        viewModel.resetGame()
                        viewModel.setDialog(ActiveDialogType.STORY_INTRO)
                    },
                    onToggleLanguage = { viewModel.toggleLanguage() }
                )
            }

            else -> {}
        }

        // --- 5. GESTURE-BASED TUTORIAL OVERLAY ---
        AnimatedVisibility(
            visible = uiState.showTutorialOverlay && uiState.activeDialog == ActiveDialogType.NONE,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            TutorialOverlay(
                language = uiState.language,
                joystickOnRight = uiState.joystickOnRight,
                onDismiss = { viewModel.dismissTutorial() }
            )
        }

        // --- 6. SCREEN-SPACE PARTICLE EFFECTS OVERLAY ---
        if (feedbackState.particles.isNotEmpty()) {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                feedbackState.particles.forEach { particle ->
                    val alpha = particle.alpha.coerceIn(0f, 1f)
                    when (particle.shapeType) {
                        com.example.game.ParticleShape.CIRCLE -> {
                            drawCircle(
                                color = particle.color.copy(alpha = alpha),
                                radius = particle.size,
                                center = androidx.compose.ui.geometry.Offset(particle.x, particle.y)
                            )
                        }
                        com.example.game.ParticleShape.SPARK -> {
                            drawLine(
                                color = particle.color.copy(alpha = alpha),
                                start = androidx.compose.ui.geometry.Offset(particle.x, particle.y),
                                end = androidx.compose.ui.geometry.Offset(
                                    particle.x + particle.vx * 0.04f,
                                    particle.y + particle.vy * 0.04f
                                ),
                                strokeWidth = particle.size * 0.6f
                            )
                        }
                        com.example.game.ParticleShape.STAR -> {
                            drawCircle(
                                color = Color.White.copy(alpha = alpha),
                                radius = particle.size * 0.5f,
                                center = androidx.compose.ui.geometry.Offset(particle.x, particle.y)
                            )
                            drawCircle(
                                color = particle.color.copy(alpha = alpha),
                                radius = particle.size,
                                center = androidx.compose.ui.geometry.Offset(particle.x, particle.y)
                            )
                        }
                        com.example.game.ParticleShape.TEXT -> {
                            drawCircle(
                                color = particle.color.copy(alpha = alpha),
                                radius = particle.size,
                                center = androidx.compose.ui.geometry.Offset(particle.x, particle.y)
                            )
                        }
                    }
                }
            }
        }
    }
}
