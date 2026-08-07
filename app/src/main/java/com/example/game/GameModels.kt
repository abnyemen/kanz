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
    SETTINGS,
    ACHIEVEMENTS,
    STORY_INTRO,
    GAME_OVER,
    VICTORY_SCREEN,
    PAUSE_MENU,
    LOADING_SCREEN
}

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
