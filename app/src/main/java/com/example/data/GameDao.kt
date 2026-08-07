package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("SELECT * FROM game_save WHERE id = 1")
    fun getGameSaveFlow(): Flow<GameSave?>

    @Query("SELECT * FROM game_save WHERE id = 1")
    suspend fun getGameSave(): GameSave?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGame(save: GameSave)

    @Query("SELECT * FROM inventory_items")
    fun getInventoryFlow(): Flow<List<InventoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: InventoryItem)

    @Query("DELETE FROM inventory_items WHERE itemId = :itemId")
    suspend fun deleteItem(itemId: String)

    @Query("SELECT * FROM unlocked_temples")
    fun getTemplesFlow(): Flow<List<UnlockedTemple>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemple(temple: UnlockedTemple)

    @Query("SELECT * FROM achievements")
    fun getAchievementsFlow(): Flow<List<Achievement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement)

    @Query("DELETE FROM game_save")
    suspend fun resetGameSave()

    @Query("DELETE FROM inventory_items")
    suspend fun resetInventory()

    @Query("DELETE FROM unlocked_temples")
    suspend fun resetTemples()
}
