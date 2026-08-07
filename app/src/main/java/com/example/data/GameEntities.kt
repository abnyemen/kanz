package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_save")
data class GameSave(
    @PrimaryKey val id: Int = 1,
    val posX: Float = 0f,
    val posY: Float = 0.5f,
    val posZ: Float = 0f,
    val yaw: Float = 0f,
    val pitch: Float = 15f,
    val health: Int = 100,
    val maxHealth: Int = 100,
    val hydration: Int = 100,
    val stamina: Int = 100,
    val goldCoins: Int = 50,
    val diamonds: Int = 10,
    val activeWeapon: String = "Bronze Sword",
    val isTorchLit: Boolean = false,
    val currentMount: String = "none", // "none", "camel", "horse"
    val activeQuestId: String = "quest_1_landing",
    val keysCollectedCount: Int = 0,
    val timeOfDayHours: Float = 10.0f, // 10:00 AM start
    val playTimeSeconds: Long = 0,
    val language: String = "ar", // "ar" or "en"
    val graphicQuality: String = "high", // "low", "medium", "high"
    val soundEnabled: Boolean = true,
    val isGameCompleted: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey val itemId: String,
    val nameEn: String,
    val nameAr: String,
    val itemType: String, // "key", "artifact", "weapon", "potion", "food", "water", "tool", "torch"
    val quantity: Int = 1,
    val descriptionEn: String,
    val descriptionAr: String,
    val iconName: String,
    val isEquipped: Boolean = false
)

@Entity(tableName = "unlocked_temples")
data class UnlockedTemple(
    @PrimaryKey val templeId: String,
    val nameEn: String,
    val nameAr: String,
    val isUnlocked: Boolean = false,
    val isBossDefeated: Boolean = false,
    val isPuzzleSolved: Boolean = false
)

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String,
    val titleEn: String,
    val titleAr: String,
    val descriptionEn: String,
    val descriptionAr: String,
    val difficulty: String = "EASY", // "EASY", "MEDIUM", "HARD", "LEGENDARY"
    val rewardGold: Int = 100,
    val rewardDiamonds: Int = 2,
    val isUnlocked: Boolean = false,
    val unlockedTime: Long = 0
)
