package com.example.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.HapticEngine
import com.example.audio.SoundEngine
import com.example.data.*
import com.example.engine.AnimState
import com.example.engine.Game3DWorld
import com.example.engine.Vector3
import com.example.engine.WorldEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import com.example.audio.AudioManager
import com.example.audio.Biome
import kotlin.math.sin

data class GameUiState(
    val language: AppLanguage = AppLanguage.ARABIC,
    val activeDialog: ActiveDialogType = ActiveDialogType.NONE,
    val currentQuest: Quest = Quest(
        id = "quest_1_landing",
        titleEn = "The Lost Desert Map",
        titleAr = "خريطة الصحراء المفقودة",
        descriptionEn = "Explore the Palm Oasis to locate the first temple key altar.",
        descriptionAr = "استكشف واحة النخيل للعثور على محراب المفتاح الأول.",
        targetType = "explore"
    ),
    val keysCollectedCount: Int = 0,
    val goldCoins: Int = 50,
    val health: Int = 100,
    val hydration: Int = 100,
    val stamina: Int = 100,
    val isTorchLit: Boolean = false,
    val currentMount: String = "none", // "none", "camel", "horse"
    val nearbyInteractNameEn: String? = null,
    val nearbyInteractNameAr: String? = null,
    val currentPuzzle: TemplePuzzleState? = null,
    val currentLootChest: LootChestState? = null,
    val isGameCompleted: Boolean = false,
    val toastMessage: String? = null,
    val graphicsQuality: GraphicsQuality = GraphicsQuality.HIGH,
    val sfxEnabled: Boolean = true,
    val sfxVolume: Float = 0.8f,
    val musicVolume: Float = 0.7f,
    val currentBiomeEn: String = "Deep Sand Dunes",
    val currentBiomeAr: String = "كثبان الصحراء العميقة",
    val controlsScale: Float = 1.0f,
    val joystickOnRight: Boolean = false,
    val joystickSizeDp: Float = 65f,
    val actionButtonsScale: Float = 1.0f,
    val controlsBottomPaddingDp: Float = 12f,
    val controlsSidePaddingDp: Float = 12f,
    val showTutorialOverlay: Boolean = true,
    val isFalconActive: Boolean = false,
    val falconCooldownRemaining: Int = 0,
    val mountSpeedBoostActive: Boolean = false,
    val activeBuffNameEn: String? = null,
    val activeBuffNameAr: String? = null
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository
    val soundEngine: SoundEngine = SoundEngine(application)
    val feedbackManager: FeedbackManager = FeedbackManager(application)
    val audioManager: AudioManager = AudioManager(application, soundEngine)
    val world: Game3DWorld = Game3DWorld()

    private var lastPlayerHealth: Int = 100

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    val inventory = MutableStateFlow<List<InventoryItem>>(emptyList())
    val achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val unlockedTemples = MutableStateFlow<List<UnlockedTemple>>(emptyList())

    private var autoSaveJob: Job? = null

    init {
        val database = AppDatabase.getDatabase(application)
        repository = GameRepository(database.gameDao())

        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
            repository.inventory.collect { items ->
                inventory.value = items
            }
        }

        viewModelScope.launch {
            repository.achievements.collect { achs ->
                achievements.value = achs
            }
        }

        viewModelScope.launch {
            repository.temples.collect { tmps ->
                unlockedTemples.value = tmps
            }
        }

        viewModelScope.launch {
            repository.gameSave.collect { save ->
                if (save != null) {
                    world.playerPos.set(save.posX, save.posY, save.posZ)
                    world.playerYaw = save.yaw
                    world.health = save.health
                    world.hydration = save.hydration
                    world.stamina = save.stamina
                    world.goldCoins = save.goldCoins
                    world.keysCollected = save.keysCollectedCount
                    world.isTorchActive = save.isTorchLit
                    world.currentMount = save.currentMount
                    world.timeOfDayHours = save.timeOfDayHours

                    val lang = if (save.language == "en") AppLanguage.ENGLISH else AppLanguage.ARABIC
                    _uiState.value = _uiState.value.copy(
                        language = lang,
                        keysCollectedCount = save.keysCollectedCount,
                        goldCoins = save.goldCoins,
                        health = save.health,
                        hydration = save.hydration,
                        stamina = save.stamina,
                        isTorchLit = save.isTorchLit,
                        currentMount = save.currentMount,
                        isGameCompleted = save.isGameCompleted
                    )
                }
            }
        }

        startGameLoop()
        startAutoSave()
    }

    private fun startGameLoop() {
        viewModelScope.launch {
            while (true) {
                delay(100)
                checkNearbyInteractions()
                updateStatusBars()

                // Dynamically update background audio environment & biome
                audioManager.updateEnvironment(
                    playerPos = world.playerPos,
                    timeOfDayHours = world.timeOfDayHours,
                    weatherState = world.weatherState,
                    isGamePaused = world.isGamePaused,
                    musicVolume = _uiState.value.musicVolume,
                    sfxVolume = _uiState.value.sfxVolume,
                    sfxEnabled = _uiState.value.sfxEnabled
                )

                val biome = audioManager.currentBiome
                val bEn = audioManager.getBiomeNameEn(biome)
                val bAr = audioManager.getBiomeNameAr(biome)
                if (_uiState.value.currentBiomeEn != bEn) {
                    _uiState.value = _uiState.value.copy(
                        currentBiomeEn = bEn,
                        currentBiomeAr = bAr
                    )
                }
            }
        }
    }

    private fun startAutoSave() {
        autoSaveJob = viewModelScope.launch {
            while (true) {
                delay(15000) // Auto-save every 15s
                saveGameState()
            }
        }
    }

    private fun updateStatusBars() {
        if (world.health < lastPlayerHealth) {
            feedbackManager.triggerPlayerDamage()
        }
        lastPlayerHealth = world.health

        _uiState.value = _uiState.value.copy(
            health = world.health,
            hydration = world.hydration,
            stamina = world.stamina,
            goldCoins = world.goldCoins,
            keysCollectedCount = world.keysCollected
        )
    }

    fun onMoveInput(dx: Float, dy: Float) {
        if (dy == 0f && dx == 0f) {
            world.playerAnimState = AnimState.IDLE
            return
        }

        val moveSpeed = when {
            _uiState.value.mountSpeedBoostActive -> 16.0f
            world.currentMount != "none" -> 9.0f
            world.playerAnimState == AnimState.SPRINT -> 6.5f
            else -> 3.8f
        }

        val radYaw = Math.toRadians((world.playerYaw).toDouble()).toFloat()
        val forwardX = -sin(radYaw)
        val forwardZ = -cos(radYaw)
        val rightX = cos(radYaw)
        val rightZ = -sin(radYaw)

        val moveX = forwardX * dy + rightX * dx
        val moveZ = forwardZ * dy + rightZ * dx

        world.playerPos.x += moveX * 0.05f * moveSpeed
        world.playerPos.z += moveZ * 0.05f * moveSpeed

        val targetYaw = Math.toDegrees(atan2(-moveX.toDouble(), -moveZ.toDouble())).toFloat()
        world.playerYaw = targetYaw

        world.playerAnimState = if (world.currentMount != "none") {
            AnimState.RIDE_CAMEL
        } else if (moveSpeed > 5f) {
            AnimState.RUN
        } else {
            AnimState.WALK
        }

        if (Math.random() < 0.2) {
            soundEngine.playFootstep()
        }
    }

    fun onCameraDrag(deltaYaw: Float, deltaPitch: Float) {
        // Updated camera angles handled by GLRenderer
    }

    fun performAttack() {
        if (world.stamina < 10) return
        world.stamina = (world.stamina - 15).coerceAtLeast(0)
        world.playerAnimState = AnimState.ATTACK
        soundEngine.playSwordSwing()
        feedbackManager.triggerAction()

        viewModelScope.launch {
            delay(200)
            // Check enemies within range
            for (entity in world.entities) {
                if ((entity.type == "mummy" || entity.type == "bandit" || entity.type == "boss_anubis") && entity.health > 0) {
                    val dist = entity.position.distanceTo(world.playerPos)
                    if (dist < 3.2f) {
                        entity.health = (entity.health - 35).coerceAtLeast(0)
                        soundEngine.playSwordHit()
                        feedbackManager.triggerHit(isCritical = entity.type == "boss_anubis")
                        if (entity.health == 0) {
                            entity.animState = AnimState.DEATH
                            world.goldCoins += 40
                            feedbackManager.triggerCollect(symbol = "🪙")
                            showToast(
                                if (_uiState.value.language == AppLanguage.ARABIC) "هزمت ${entity.nameAr}! +40 ذهب" else "Defeated ${entity.nameEn}! +40 Gold"
                            )
                        }
                    }
                }
            }
            delay(400)
            world.playerAnimState = AnimState.IDLE
        }
    }

    fun performJump() {
        if (world.stamina < 15) return
        world.stamina = (world.stamina - 15).coerceAtLeast(0)
        world.playerAnimState = AnimState.JUMP
        feedbackManager.triggerAction()
        viewModelScope.launch {
            delay(500)
            world.playerAnimState = AnimState.IDLE
        }
    }

    fun performRoll() {
        if (world.stamina < 20) return
        world.stamina = (world.stamina - 20).coerceAtLeast(0)
        world.playerAnimState = AnimState.ROLL
        feedbackManager.triggerAction()
        viewModelScope.launch {
            delay(600)
            world.playerAnimState = AnimState.IDLE
        }
    }

    fun toggleTorch() {
        world.isTorchActive = !world.isTorchActive
        _uiState.value = _uiState.value.copy(isTorchLit = world.isTorchActive)
        feedbackManager.triggerAction()
        showToast(
            if (world.isTorchActive) {
                if (_uiState.value.language == AppLanguage.ARABIC) "تم إشعال الشعلة 🔥" else "Torch Lit 🔥"
            } else {
                if (_uiState.value.language == AppLanguage.ARABIC) "تم إطفاء الشعلة" else "Torch Extinguished"
            }
        )
    }

    fun drinkWater() {
        world.hydration = (world.hydration + 40).coerceAtMost(100)
        world.playerAnimState = AnimState.DRINK
        feedbackManager.triggerCollect(symbol = "💧")
        showToast(if (_uiState.value.language == AppLanguage.ARABIC) "شربت ماء نقي 💧 (+40 ارتواء)" else "Drank pure water 💧 (+40 Hydration)")
        viewModelScope.launch {
            delay(800)
            world.playerAnimState = AnimState.IDLE
        }
    }

    fun triggerFalconCall() {
        if (_uiState.value.falconCooldownRemaining > 0) {
            showToast(if (_uiState.value.language == AppLanguage.ARABIC) "الصقر يرتاح حالياً 🦅 (يرجى الانتظار)" else "Falcon is resting 🦅 (Cooldown active)")
            return
        }

        soundEngine.playEagleScreech()
        feedbackManager.triggerCollect(symbol = "🦅")
        _uiState.value = _uiState.value.copy(
            isFalconActive = true,
            falconCooldownRemaining = 25
        )
        showToast(if (_uiState.value.language == AppLanguage.ARABIC) "انطلق الصقر الصحراوي الكشاف! 🦅 تم كشف الكنوز القريبة" else "Falcon Scout Launched! 🦅 Highlighting nearby treasures")

        viewModelScope.launch {
            for (i in 25 downTo 0) {
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    isFalconActive = i > 15,
                    falconCooldownRemaining = i
                )
            }
        }
    }

    fun triggerCamelSprint() {
        if (world.currentMount == "none") {
            showToast(if (_uiState.value.language == AppLanguage.ARABIC) "امتطِ جملاً أولاً لتشغيل الاندفاعة! 🐪" else "Mount a camel first to sprint! 🐪")
            return
        }
        if (world.stamina < 20) {
            showToast(if (_uiState.value.language == AppLanguage.ARABIC) "التحمل غير كافٍ للاندفاعة!" else "Not enough stamina for boost!")
            return
        }

        world.stamina = (world.stamina - 20).coerceAtLeast(0)
        soundEngine.playCamelGrunt()
        feedbackManager.triggerAction()
        _uiState.value = _uiState.value.copy(mountSpeedBoostActive = true)
        showToast(if (_uiState.value.language == AppLanguage.ARABIC) "سرعة الجمل الفائقة متقبلة! 🐪⚡" else "Camel Speed Boost active! 🐪⚡")

        viewModelScope.launch {
            delay(4500)
            _uiState.value = _uiState.value.copy(mountSpeedBoostActive = false)
        }
    }

    private fun checkNearbyInteractions() {
        var nearbyEn: String? = null
        var nearbyAr: String? = null

        for (entity in world.entities) {
            if (entity.isInteractive) {
                val dist = entity.position.distanceTo(world.playerPos)
                if (dist < 3.5f) {
                    nearbyEn = entity.nameEn
                    nearbyAr = entity.nameAr
                    break
                }
            }
        }

        _uiState.value = _uiState.value.copy(
            nearbyInteractNameEn = nearbyEn,
            nearbyInteractNameAr = nearbyAr
        )
    }

    fun interactWithNearby() {
        for (entity in world.entities) {
            if (entity.isInteractive && entity.position.distanceTo(world.playerPos) < 3.5f) {
                when (entity.type) {
                    "camel" -> {
                        feedbackManager.triggerAction()
                        if (world.currentMount == "camel") {
                            world.currentMount = "none"
                            showToast(if (_uiState.value.language == AppLanguage.ARABIC) "نزلت من الجمل" else "Dismounted Camel")
                        } else {
                            world.currentMount = "camel"
                            soundEngine.playCamelGrunt()
                            showToast(if (_uiState.value.language == AppLanguage.ARABIC) "ركبت الجمل 🐪" else "Mounted Camel 🐪")
                            viewModelScope.launch { repository.unlockAchievement("camel_rider") }
                        }
                    }

                    "horse" -> {
                        feedbackManager.triggerAction()
                        if (world.currentMount == "horse") {
                            world.currentMount = "none"
                        } else {
                            world.currentMount = "horse"
                            showToast(if (_uiState.value.language == AppLanguage.ARABIC) "ركبت الحصان 🐎" else "Mounted Horse 🐎")
                        }
                    }

                    "chest" -> {
                        if (!entity.isTriggered) {
                            openLootChest(entity)
                        } else {
                            showToast(if (_uiState.value.language == AppLanguage.ARABIC) "هذا الصندوق مفتوح بالفعل" else "This chest is already opened")
                        }
                    }

                    "key_altar" -> {
                        feedbackManager.triggerAction()
                        openTemplePuzzle(entity.id)
                    }

                    "temple_door" -> {
                        if (world.keysCollected >= 4) {
                            soundEngine.playDoorRumble()
                            soundEngine.playVictoryFanfare()
                            feedbackManager.triggerCollect(symbol = "🔑")
                            _uiState.value = _uiState.value.copy(
                                isGameCompleted = true,
                                activeDialog = ActiveDialogType.VICTORY_SCREEN
                            )
                            viewModelScope.launch { repository.unlockAchievement("desert_treasure") }
                        } else {
                            feedbackManager.triggerAction()
                            showToast(
                                if (_uiState.value.language == AppLanguage.ARABIC)
                                    "البوابة مغلقة! تحتاج إلى 4 مفاتيح حضارة (${world.keysCollected}/4)"
                                else
                                    "Gate Locked! Requires 4 Civilization Keys (${world.keysCollected}/4)"
                            )
                        }
                    }

                    "water_well" -> {
                        drinkWater()
                    }
                }
                break
            }
        }
    }

    private fun openLootChest(entity: WorldEntity) {
        soundEngine.playChestOpen()
        feedbackManager.triggerCollect(symbol = "📦")

        val chestType = when (entity.id) {
            "final_chest" -> "legendary"
            "horus_chest", "pharaoh_gold_crate" -> "gold"
            "anubis_relic_chest", "oasis_crate" -> "silver"
            else -> "bronze"
        }

        val totalGold = when (chestType) {
            "legendary" -> 500
            "gold" -> 300
            "silver" -> 180
            else -> 100
        }

        val rewards = mutableListOf<LootRewardItem>()
        when (chestType) {
            "legendary" -> {
                rewards.add(LootRewardItem("pharaoh_scepter", "Golden Scepter of Tut", "صولجان توت عنخ آمون الذهبي", "👑", 1, "Legendary"))
                rewards.add(LootRewardItem("ancient_elixir", "Elixir of Eternal Water", "إكسير الحياة والماء الخالد", "🧪", 2, "Epic"))
                rewards.add(LootRewardItem("flint_rocks", "High-Grade Flint Stones", "حجارة صوان عالية الجودة", "🪨", 5, "Rare"))
            }
            "gold" -> {
                rewards.add(LootRewardItem("horus_pendant", "Pendant of Horus", "قلادة حورس المقدسة", "📿", 1, "Epic"))
                rewards.add(LootRewardItem("desert_herbs", "Healing Desert Herbs", "أعشاب شافيه وصحية", "🌿", 3, "Rare"))
                rewards.add(LootRewardItem("dry_branches", "Hardened Palm Wood", "خشب وجريد النخيل الصلب", "🪵", 4, "Common"))
            }
            "silver" -> {
                rewards.add(LootRewardItem("water_canteen", "Refilled Water Canteen", "قارورة ماء نقية مليئة", "💧", 2, "Rare"))
                rewards.add(LootRewardItem("desert_dates", "Fresh Oasis Dates", "تمور الواحة الطازجة", "🌴", 3, "Common"))
                rewards.add(LootRewardItem("flint_rocks", "Flint Rocks", "حجارة الصوان", "🪨", 3, "Common"))
            }
            else -> {
                rewards.add(LootRewardItem("dry_branches", "Dry Palm Wood", "أغصان وجريد النخيل", "🪵", 3, "Common"))
                rewards.add(LootRewardItem("desert_plants", "Desert Fiber", "ألياف صحراوية", "🌿", 2, "Common"))
            }
        }

        val lootChestState = LootChestState(
            chestId = entity.id,
            titleEn = entity.nameEn,
            titleAr = entity.nameAr,
            chestType = chestType,
            rewards = rewards,
            totalGold = totalGold
        )

        _uiState.value = _uiState.value.copy(
            currentLootChest = lootChestState,
            activeDialog = ActiveDialogType.LOOT_CHEST
        )
    }

    fun claimLoot() {
        val chest = _uiState.value.currentLootChest ?: return
        
        // Mark entity as triggered in world
        val entity = world.entities.find { it.id == chest.chestId }
        entity?.isTriggered = true

        // Award Gold
        world.goldCoins += chest.totalGold

        // Award Items into repository/inventory
        viewModelScope.launch {
            val currentInv = inventory.value
            for (reward in chest.rewards) {
                val existing = currentInv.find { it.itemId == reward.id }
                val newQty = (existing?.quantity ?: 0) + reward.quantity
                val itemType = when {
                    reward.id.contains("water") || reward.id.contains("elixir") -> "water"
                    reward.id.contains("dates") || reward.id.contains("herbs") -> "food"
                    reward.id.contains("scepter") || reward.id.contains("pendant") -> "artifact"
                    else -> "material"
                }
                repository.addItem(
                    InventoryItem(
                        itemId = reward.id,
                        nameEn = reward.nameEn,
                        nameAr = reward.nameAr,
                        itemType = itemType,
                        quantity = newQty,
                        descriptionEn = "Acquired from loot chest: ${chest.titleEn}",
                        descriptionAr = "تم الحصول عليه من صندوق اللوت: ${chest.titleAr}",
                        iconName = if (itemType == "artifact") "artifact" else "material"
                    )
                )
            }
        }

        soundEngine.playPickupChime()
        feedbackManager.triggerCollect(symbol = "🪙")

        val isAr = _uiState.value.language == AppLanguage.ARABIC
        showToast(if (isAr) "تم إضافة ${chest.totalGold} ذهب 🪙 والغنائم إلى حقيبتك! 🎒" else "Added ${chest.totalGold} Gold 🪙 & items to backpack! 🎒")

        _uiState.value = _uiState.value.copy(
            currentLootChest = null,
            activeDialog = ActiveDialogType.NONE
        )
    }

    private fun openTemplePuzzle(altarId: String) {
        val puzzle = when (altarId) {
            "horus_altar" -> TemplePuzzleState(
                templeId = "horus",
                titleEn = "Temple of Horus Puzzle",
                titleAr = "لغز معبد حورس",
                questionEn = "Which symbol aligns with the rising sun of Horus?",
                questionAr = "أي رمز يتطابق مع شروق شمس حورس؟",
                optionsEn = listOf("Scarab Wing", "Falcon Eye", "Anubis Jackal", "Lotus Flower"),
                optionsAr = listOf("جناح الجعل", "عين الصقر", "ابن آوى أنوبيس", "زهرة اللوتس"),
                correctAnswerIndex = 1
            )
            else -> TemplePuzzleState(
                templeId = "anubis",
                titleEn = "Temple of Anubis Seal",
                titleAr = "ختَم معبد أنوبيس",
                questionEn = "Select the sacred balance offering of Anubis:",
                questionAr = "اختر القربان المقدس لميزان أنوبيس:",
                optionsEn = listOf("Gold Coin", "Golden Feather", "Water Flask", "Bronze Spear"),
                optionsAr = listOf("عملة ذهبية", "ريشة ناعمة", "قارورة ماء", "رمح برونزي"),
                correctAnswerIndex = 1
            )
        }

        _uiState.value = _uiState.value.copy(
            currentPuzzle = puzzle,
            activeDialog = ActiveDialogType.TEMPLE_PUZZLE
        )
    }

    fun solvePuzzle(selectedIndex: Int) {
        val puzzle = _uiState.value.currentPuzzle ?: return
        if (selectedIndex == puzzle.correctAnswerIndex) {
            soundEngine.playPickupChime()
            feedbackManager.triggerCollect(symbol = "🔑")
            world.keysCollected += 1
            _uiState.value = _uiState.value.copy(
                keysCollectedCount = world.keysCollected,
                activeDialog = ActiveDialogType.NONE,
                currentPuzzle = null
            )
            showToast(
                if (_uiState.value.language == AppLanguage.ARABIC)
                    "إجابة صحيحة! حصلت على مفتاح الحضارة القديمة🔑 (${world.keysCollected}/4)"
                else
                    "Correct! Acquired Ancient Civilization Key 🔑 (${world.keysCollected}/4)"
            )
            viewModelScope.launch {
                val templeId = "temple_${puzzle.templeId}"
                repository.saveTemple(
                    UnlockedTemple(
                        templeId = templeId,
                        nameEn = if (puzzle.templeId == "horus") "Temple of Horus" else "Temple of Anubis",
                        nameAr = if (puzzle.templeId == "horus") "معبد حورس" else "معبد أنوبيس",
                        isUnlocked = true,
                        isPuzzleSolved = true
                    )
                )
                repository.unlockAchievement(if (puzzle.templeId == "horus") "horus_key" else "anubis_slayer")
                saveGameState()
            }
        } else {
            feedbackManager.triggerPlayerDamage()
            showToast(if (_uiState.value.language == AppLanguage.ARABIC) "إجابة خاطئة! حاول مجدداً" else "Incorrect! Try again")
        }
    }

    fun setDialog(dialog: ActiveDialogType) {
        world.isGamePaused = (dialog == ActiveDialogType.PAUSE_MENU)
        _uiState.value = _uiState.value.copy(activeDialog = dialog)
    }

    fun pauseGame() {
        world.isGamePaused = true
        feedbackManager.triggerAction()
        _uiState.value = _uiState.value.copy(activeDialog = ActiveDialogType.PAUSE_MENU)
    }

    fun resumeGame() {
        world.isGamePaused = false
        feedbackManager.triggerAction()
        _uiState.value = _uiState.value.copy(activeDialog = ActiveDialogType.NONE)
    }

    fun performQuickSave() {
        saveGameState()
        soundEngine.playPickupChime()
        feedbackManager.triggerCollect(symbol = "💾")
        showToast(
            if (_uiState.value.language == AppLanguage.ARABIC)
                "تم حفظ التقدم السريع بنجاح! 💾"
            else
                "Game Progress Quick-Saved! 💾"
        )
    }

    fun toggleLanguage() {
        val newLang = if (_uiState.value.language == AppLanguage.ARABIC) AppLanguage.ENGLISH else AppLanguage.ARABIC
        _uiState.value = _uiState.value.copy(language = newLang)
        saveGameState()
    }

    fun setGraphicsQuality(quality: GraphicsQuality) {
        world.graphicsQuality = quality
        _uiState.value = _uiState.value.copy(graphicsQuality = quality)
    }

    fun setSfxEnabled(enabled: Boolean) {
        soundEngine.setSoundEnabled(enabled)
        feedbackManager.hapticsEnabled = enabled
        _uiState.value = _uiState.value.copy(sfxEnabled = enabled)
    }

    fun setSfxVolume(volume: Float) {
        _uiState.value = _uiState.value.copy(sfxVolume = volume)
    }

    fun setMusicVolume(volume: Float) {
        _uiState.value = _uiState.value.copy(musicVolume = volume)
    }

    fun setControlsScale(scale: Float) {
        _uiState.value = _uiState.value.copy(controlsScale = scale.coerceIn(0.7f, 1.5f))
    }

    fun setJoystickOnRight(onRight: Boolean) {
        _uiState.value = _uiState.value.copy(joystickOnRight = onRight)
    }

    fun setJoystickSize(sizeDp: Float) {
        _uiState.value = _uiState.value.copy(joystickSizeDp = sizeDp.coerceIn(45f, 95f))
    }

    fun setActionButtonsScale(scale: Float) {
        _uiState.value = _uiState.value.copy(actionButtonsScale = scale.coerceIn(0.7f, 1.5f))
    }

    fun setControlsBottomPadding(paddingDp: Float) {
        _uiState.value = _uiState.value.copy(controlsBottomPaddingDp = paddingDp.coerceIn(4f, 40f))
    }

    fun setControlsSidePadding(paddingDp: Float) {
        _uiState.value = _uiState.value.copy(controlsSidePaddingDp = paddingDp.coerceIn(4f, 40f))
    }

    fun resetControlsToDefault() {
        _uiState.value = _uiState.value.copy(
            controlsScale = 1.0f,
            joystickOnRight = false,
            joystickSizeDp = 65f,
            actionButtonsScale = 1.0f,
            controlsBottomPaddingDp = 12f,
            controlsSidePaddingDp = 12f
        )
    }

    fun dismissTutorial() {
        _uiState.value = _uiState.value.copy(showTutorialOverlay = false)
    }

    fun showTutorial() {
        _uiState.value = _uiState.value.copy(showTutorialOverlay = true)
    }

    fun useOrEquipItem(item: InventoryItem) {
        val isAr = _uiState.value.language == AppLanguage.ARABIC
        viewModelScope.launch {
            when (item.itemType) {
                "food" -> {
                    world.health = (world.health + 25).coerceAtMost(100)
                    world.stamina = (world.stamina + 20).coerceAtMost(100)
                    _uiState.value = _uiState.value.copy(health = world.health, stamina = world.stamina)
                    feedbackManager.triggerCollect(symbol = "❤️")
                    val newQty = item.quantity - 1
                    if (newQty <= 0) {
                        repository.removeItem(item.itemId)
                    } else {
                        repository.addItem(item.copy(quantity = newQty))
                    }
                    showToast(if (isAr) "تناولت ${item.nameAr} (+25 صحة ❤️)" else "Ate ${item.nameEn} (+25 HP ❤️)")
                }
                "water", "potion" -> {
                    world.hydration = (world.hydration + 40).coerceAtMost(100)
                    world.health = (world.health + 15).coerceAtMost(100)
                    _uiState.value = _uiState.value.copy(hydration = world.hydration, health = world.health)
                    feedbackManager.triggerCollect(symbol = "💧")
                    val newQty = item.quantity - 1
                    if (newQty <= 0) {
                        repository.removeItem(item.itemId)
                    } else {
                        repository.addItem(item.copy(quantity = newQty))
                    }
                    showToast(if (isAr) "شربت ${item.nameAr} (+40 ارتواء 💧)" else "Drank ${item.nameEn} (+40 Water 💧)")
                }
                "weapon", "tool", "torch" -> {
                    val nextEquippedState = !item.isEquipped
                    if (item.itemId == "desert_torch" || item.iconName == "torch") {
                        world.isTorchActive = nextEquippedState
                        _uiState.value = _uiState.value.copy(isTorchLit = world.isTorchActive)
                    }
                    repository.addItem(item.copy(isEquipped = nextEquippedState))
                    feedbackManager.triggerAction()
                    val actionName = if (nextEquippedState) {
                        if (isAr) "جهزت ${item.nameAr} ⚔️" else "Equipped ${item.nameEn} ⚔️"
                    } else {
                        if (isAr) "خلعت ${item.nameAr}" else "Unequipped ${item.nameEn}"
                    }
                    showToast(actionName)
                }
                "artifact", "key" -> {
                    val nextEquippedState = !item.isEquipped
                    repository.addItem(item.copy(isEquipped = nextEquippedState))
                    feedbackManager.triggerAction()
                    val msg = if (nextEquippedState) {
                        if (isAr) "جهزت الأثر: ${item.nameAr} 🏺" else "Equipped Artifact: ${item.nameEn} 🏺"
                    } else {
                        if (isAr) "أزلت الأثر: ${item.nameAr}" else "Unequipped Artifact: ${item.nameEn}"
                    }
                    showToast(msg)
                }
                else -> {
                    val nextEquippedState = !item.isEquipped
                    repository.addItem(item.copy(isEquipped = nextEquippedState))
                    feedbackManager.triggerAction()
                    showToast(if (isAr) "استخدمت ${item.nameAr}" else "Used ${item.nameEn}")
                }
            }
        }
    }

    fun craftItem(recipe: CraftingRecipe) {
        val isAr = _uiState.value.language == AppLanguage.ARABIC
        val currentInventory = inventory.value

        // 1. Verify player has all required ingredients
        for (ingredient in recipe.ingredients) {
            val owned = currentInventory.find { it.itemId == ingredient.itemId }
            if (owned == null || owned.quantity < ingredient.requiredQuantity) {
                val missingName = if (isAr) ingredient.nameAr else ingredient.nameEn
                showToast(
                    if (isAr) "مواد غير كافية! تحتاج ${ingredient.requiredQuantity}x $missingName"
                    else "Not enough materials! Need ${ingredient.requiredQuantity}x $missingName"
                )
                feedbackManager.triggerPlayerDamage()
                return
            }
        }

        // 2. Consume ingredients and award result item
        viewModelScope.launch {
            for (ingredient in recipe.ingredients) {
                val owned = currentInventory.find { it.itemId == ingredient.itemId }
                if (owned != null) {
                    val newQty = owned.quantity - ingredient.requiredQuantity
                    if (newQty <= 0) {
                        repository.removeItem(owned.itemId)
                    } else {
                        repository.addItem(owned.copy(quantity = newQty))
                    }
                }
            }

            // 3. Add crafted item
            val existingResult = currentInventory.find { it.itemId == recipe.resultItemId }
            if (existingResult != null) {
                repository.addItem(existingResult.copy(quantity = existingResult.quantity + recipe.resultQuantity))
            } else {
                repository.addItem(
                    InventoryItem(
                        itemId = recipe.resultItemId,
                        nameEn = recipe.nameEn,
                        nameAr = recipe.nameAr,
                        itemType = recipe.resultType,
                        quantity = recipe.resultQuantity,
                        descriptionEn = recipe.descriptionEn,
                        descriptionAr = recipe.descriptionAr,
                        iconName = recipe.resultIcon,
                        isEquipped = false
                    )
                )
            }

            soundEngine.playPickupChime()
            feedbackManager.triggerCollect(symbol = "🛠️")
            showToast(
                if (isAr) "تم تصنيع: ${recipe.nameAr} 🛠️" else "Crafted: ${recipe.nameEn} 🛠️"
            )
        }
    }

    fun dropItem(item: InventoryItem) {
        val isAr = _uiState.value.language == AppLanguage.ARABIC
        viewModelScope.launch {
            if (item.isEquipped && (item.itemId == "desert_torch" || item.iconName == "torch")) {
                world.isTorchActive = false
                _uiState.value = _uiState.value.copy(isTorchLit = false)
            }
            val newQty = item.quantity - 1
            if (newQty <= 0) {
                repository.removeItem(item.itemId)
            } else {
                repository.addItem(item.copy(quantity = newQty, isEquipped = false))
            }
            feedbackManager.triggerAction()
            showToast(if (isAr) "أسقطت ${item.nameAr} 🗑️" else "Dropped ${item.nameEn} 🗑️")
        }
    }

    fun showToast(msg: String) {
        _uiState.value = _uiState.value.copy(toastMessage = msg)
        viewModelScope.launch {
            delay(2500)
            _uiState.value = _uiState.value.copy(toastMessage = null)
        }
    }

    fun saveGameState() {
        viewModelScope.launch {
            val save = GameSave(
                posX = world.playerPos.x,
                posY = world.playerPos.y,
                posZ = world.playerPos.z,
                yaw = world.playerYaw,
                health = world.health,
                hydration = world.hydration,
                stamina = world.stamina,
                goldCoins = world.goldCoins,
                keysCollectedCount = world.keysCollected,
                isTorchLit = world.isTorchActive,
                currentMount = world.currentMount,
                timeOfDayHours = world.timeOfDayHours,
                language = if (_uiState.value.language == AppLanguage.ENGLISH) "en" else "ar",
                isGameCompleted = _uiState.value.isGameCompleted
            )
            repository.saveGame(save)
        }
    }

    fun resetGame() {
        viewModelScope.launch {
            repository.resetAllData()
            _uiState.value = GameUiState()
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioManager.release()
        soundEngine.release()
        autoSaveJob?.cancel()
    }
}
