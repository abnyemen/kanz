package com.example.game

enum class AppLanguage {
    ARABIC, ENGLISH
}

enum class GraphicsQuality {
    LOW, MEDIUM, HIGH
}

data class Quest(
    val id: String,
    val titleEn: String,
    val titleAr: String,
    val descriptionEn: String,
    val descriptionAr: String,
    val targetType: String, // "explore", "key", "puzzle", "boss", "treasure"
    val isCompleted: Boolean = false,
    val rewardGold: Int = 100
)

enum class ActiveDialogType {
    NONE,
    WORLD_MAP,
    INVENTORY,
    QUEST_LOG,
    TEMPLE_PUZZLE,
    LOOT_CHEST,
    SETTINGS,
    ACHIEVEMENTS,
    STORY_INTRO,
    GAME_OVER,
    VICTORY_SCREEN,
    PAUSE_MENU,
    LOADING_SCREEN
}

data class LootRewardItem(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val iconEmoji: String,
    val quantity: Int,
    val rarity: String = "Common" // "Common", "Rare", "Epic", "Legendary"
)

data class LootChestState(
    val chestId: String,
    val titleEn: String,
    val titleAr: String,
    val chestType: String, // "bronze", "silver", "gold", "legendary"
    val rewards: List<LootRewardItem>,
    val totalGold: Int
)

data class TemplePuzzleState(
    val templeId: String,
    val titleEn: String,
    val titleAr: String,
    val questionEn: String,
    val questionAr: String,
    val optionsEn: List<String>,
    val optionsAr: List<String>,
    val correctAnswerIndex: Int
)
