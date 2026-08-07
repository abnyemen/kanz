package com.example.data

import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameDao: GameDao) {

    val gameSave: Flow<GameSave?> = gameDao.getGameSaveFlow()
    val inventory: Flow<List<InventoryItem>> = gameDao.getInventoryFlow()
    val temples: Flow<List<UnlockedTemple>> = gameDao.getTemplesFlow()
    val achievements: Flow<List<Achievement>> = gameDao.getAchievementsFlow()

    suspend fun getGameSave(): GameSave? = gameDao.getGameSave()

    suspend fun saveGame(save: GameSave) {
        gameDao.saveGame(save)
    }

    suspend fun addItem(item: InventoryItem) {
        gameDao.insertItem(item)
    }

    suspend fun removeItem(itemId: String) {
        gameDao.deleteItem(itemId)
    }

    suspend fun saveTemple(temple: UnlockedTemple) {
        gameDao.insertTemple(temple)
    }

    suspend fun unlockAchievement(id: String) {
        val currentAchievements = listOf(
            Achievement("first_steps", "First Explorer", "مستكشف مبتدئ", "Began the desert journey", "بدأت رحلة الصحراء", true, System.currentTimeMillis()),
            Achievement("oasis_finder", "Oasis Sanctuary", "واحة الأمان", "Discovered the Palm Oasis", "اكتشفت واحة النخيل", true, System.currentTimeMillis()),
            Achievement("horus_key", "Eye of Horus", "عين حورس", "Unlocked Horus Key", "حصلت على مفتاح حورس", true, System.currentTimeMillis()),
            Achievement("anubis_slayer", "Guardian Slayer", "قاهر الحارس", "Defeated Anubis Boss", "هزمت حارس أنوبيس", true, System.currentTimeMillis()),
            Achievement("camel_rider", "Desert Nomad", "رحالة الصحراء", "Rode a desert camel", "ركبت الجمل في الصحراء", true, System.currentTimeMillis()),
            Achievement("sandstorm_survivor", "Storm Walker", "مواجه العواصف", "Survived a severe sandstorm", "نجوت من عاصفة رملية شديدة", true, System.currentTimeMillis()),
            Achievement("desert_treasure", "Legendary Master", "أسطورة كنز الصحراء", "Unlocked the Great Pyramid Treasure!", "فتحت كنز الهرم الأكبر الأسطوري!", true, System.currentTimeMillis())
        )
        val target = currentAchievements.find { it.id == id }
        if (target != null) {
            gameDao.insertAchievement(target)
        }
    }

    suspend fun initializeDefaultDataIfEmpty() {
        if (gameDao.getGameSave() == null) {
            val defaultSave = GameSave()
            gameDao.saveGame(defaultSave)

            // Initial starter inventory
            gameDao.insertItem(
                InventoryItem(
                    itemId = "mythic_map",
                    nameEn = "Ancient Desert Map",
                    nameAr = "خريطة الصحراء الأسطورية",
                    itemType = "artifact",
                    quantity = 1,
                    descriptionEn = "A weathered parchment leading to 4 ancient civilization keys.",
                    descriptionAr = "رق قديم يشير إلى مواقع مفاتيح الحضارة الأربعة.",
                    iconName = "map"
                )
            )
            gameDao.insertItem(
                InventoryItem(
                    itemId = "pharaoh_scarab",
                    nameEn = "Golden Scarab Relic",
                    nameAr = "قلادة الجعل الذهبي",
                    itemType = "artifact",
                    quantity = 1,
                    descriptionEn = "A sacred artifact engraved with ancient protective hieroglyphs.",
                    descriptionAr = "أثر أسطوري مقدس منقوش عليه هيروغليفية الوقاية.",
                    iconName = "artifact"
                )
            )
            gameDao.insertItem(
                InventoryItem(
                    itemId = "water_flask",
                    nameEn = "Fresh Water Flask",
                    nameAr = "قارورة ماء عذب",
                    itemType = "water",
                    quantity = 3,
                    descriptionEn = "Restores hydration (+40 Water) in the scorching heat.",
                    descriptionAr = "تعيد مستوى الارتواء (+40 ماء) في لهيب الصحراء.",
                    iconName = "water"
                )
            )
            gameDao.insertItem(
                InventoryItem(
                    itemId = "desert_dates",
                    nameEn = "Oasis Dates",
                    nameAr = "تمور الواحة",
                    itemType = "food",
                    quantity = 5,
                    descriptionEn = "Sweet nourishing dates that restore health (+25 HP) and stamina (+20 Stamina).",
                    descriptionAr = "تمور غنية تعيد الصحة (+25 HP) والتحمل (+20).",
                    iconName = "food"
                )
            )
            gameDao.insertItem(
                InventoryItem(
                    itemId = "bronze_scimitar",
                    nameEn = "Bronze Scimitar",
                    nameAr = "سيف السيميتار البرونزي",
                    itemType = "tool",
                    quantity = 1,
                    descriptionEn = "A sharp curved blade forged for combat against desert tomb guardians.",
                    descriptionAr = "سيف حاد مجهز للقتال ضد حراس المقابر العتيدة.",
                    iconName = "weapon",
                    isEquipped = true
                )
            )
            gameDao.insertItem(
                InventoryItem(
                    itemId = "desert_torch",
                    nameEn = "Bedouin Exploration Torch",
                    nameAr = "مشعل البدو الاستكشافي",
                    itemType = "tool",
                    quantity = 1,
                    descriptionEn = "Provides bright fire illumination inside dark pyramid vaults.",
                    descriptionAr = "يوفر إضاءة نارية مشرقة داخل دهاليز الأهرامات.",
                    iconName = "torch",
                    isEquipped = false
                )
            )

            gameDao.insertItem(
                InventoryItem(
                    itemId = "flint_rocks",
                    nameEn = "Flint & Desert Rocks",
                    nameAr = "حجارة الصوان والجرانيت",
                    itemType = "material",
                    quantity = 6,
                    descriptionEn = "Hard desert stones useful for crafting flint blades, tools, and igniting fire.",
                    descriptionAr = "حجارة صوان صحراوية صلبة لتصنيع الأدوات والأسلحة والشعلات.",
                    iconName = "material"
                )
            )
            gameDao.insertItem(
                InventoryItem(
                    itemId = "desert_plants",
                    nameEn = "Desert Herbs & Fibers",
                    nameAr = "أعشاب وألياف الصحراء",
                    itemType = "material",
                    quantity = 6,
                    descriptionEn = "Tough desert plant fibers and medicinal herbs gathered from oasis vegetation.",
                    descriptionAr = "ألياف ونباتات صحراوية وطبية تستخدم كوقود وللتضميد والتصنيع.",
                    iconName = "material"
                )
            )
            gameDao.insertItem(
                InventoryItem(
                    itemId = "dry_branches",
                    nameEn = "Dry Palm Wood",
                    nameAr = "خشب وجريد النخيل",
                    itemType = "material",
                    quantity = 4,
                    descriptionEn = "Sturdy dry palm wood used as handles for tools and torches.",
                    descriptionAr = "أغصان وجريد خشب صحراوي متين لمقابض الأدوات والمشاعل.",
                    iconName = "material"
                )
            )

            // Temples
            gameDao.insertTemple(UnlockedTemple("temple_horus", "Temple of Horus", "معبد حورس", isUnlocked = false))
            gameDao.insertTemple(UnlockedTemple("temple_anubis", "Temple of Anubis", "معبد أنوبيس", isUnlocked = false))
            gameDao.insertTemple(UnlockedTemple("bandit_fort", "Bandit Stronghold", "معقل قطاع الطرق", isUnlocked = false))
            gameDao.insertTemple(UnlockedTemple("great_pyramid", "The Great Pyramid", "الهرم الأكبر", isUnlocked = false))

            // Initial achievement
            gameDao.insertAchievement(
                Achievement("first_steps", "First Explorer", "مستكشف مبتدئ", "Began the desert journey", "بدأت رحلة الصحراء", true, System.currentTimeMillis())
            )
        }
    }

    suspend fun resetAllData() {
        gameDao.resetGameSave()
        gameDao.resetInventory()
        gameDao.resetTemples()
        initializeDefaultDataIfEmpty()
    }
}
