package com.example.game

import com.example.data.InventoryItem

data class CraftingIngredient(
    val itemId: String,
    val nameEn: String,
    val nameAr: String,
    val requiredQuantity: Int,
    val iconName: String
)

data class CraftingRecipe(
    val recipeId: String,
    val resultItemId: String,
    val nameEn: String,
    val nameAr: String,
    val descriptionEn: String,
    val descriptionAr: String,
    val resultType: String, // "torch", "weapon", "tool", "food", "water", "material"
    val resultIcon: String,
    val resultQuantity: Int = 1,
    val ingredients: List<CraftingIngredient>
)

object CraftingRecipes {
    val ALL_RECIPES = listOf(
        CraftingRecipe(
            recipeId = "craft_torch",
            resultItemId = "desert_torch",
            nameEn = "Bedouin Exploration Torch",
            nameAr = "مشعل البدو الاستكشافي 🔥",
            descriptionEn = "Provides bright illumination inside dark vaults and scares wild desert beasts.",
            descriptionAr = "يوفر إضاءة نارية مشرقة داخل المقابر المظلمة ويوفر الحماية في الليل.",
            resultType = "torch",
            resultIcon = "torch",
            resultQuantity = 1,
            ingredients = listOf(
                CraftingIngredient("desert_plants", "Desert Herbs & Fibers", "أعشاب وألياف الصحراء", 2, "material"),
                CraftingIngredient("flint_rocks", "Flint & Desert Rocks", "حجارة الصوان والجرانيت", 1, "material")
            )
        ),
        CraftingRecipe(
            recipeId = "craft_flint_knife",
            resultItemId = "flint_knife",
            nameEn = "Flint Survival Dagger",
            nameAr = "خنجر الصوان البدائي 🔪",
            descriptionEn = "A sharp hand-crafted flint blade for quick combat and harvesting.",
            descriptionAr = "خنجر صوان حاد مصنع يدوياً هجومي ومناسب للقطع والاستكشاف.",
            resultType = "weapon",
            resultIcon = "weapon",
            resultQuantity = 1,
            ingredients = listOf(
                CraftingIngredient("flint_rocks", "Flint & Desert Rocks", "حجارة الصوان والجرانيت", 3, "material"),
                CraftingIngredient("desert_plants", "Desert Herbs & Fibers", "أعشاب وألياف الصحراء", 2, "material")
            )
        ),
        CraftingRecipe(
            recipeId = "craft_herbal_poultice",
            resultItemId = "herbal_poultice",
            nameEn = "Herbal Healing Poultice",
            nameAr = "ضمادة الأعشاب الشافية 🌿",
            descriptionEn = "A soothing desert balm that restores +45 Health and cures fatigue.",
            descriptionAr = "ضمادة أعشاب صحراوية ملطفة تعيد (+45) من الصحة والنشاط.",
            resultType = "food",
            resultIcon = "food",
            resultQuantity = 1,
            ingredients = listOf(
                CraftingIngredient("desert_plants", "Desert Herbs & Fibers", "أعشاب وألياف الصحراء", 3, "material"),
                CraftingIngredient("water_flask", "Fresh Water Flask", "قارورة ماء عذب", 1, "water")
            )
        ),
        CraftingRecipe(
            recipeId = "craft_stone_hammer",
            resultItemId = "stone_hammer",
            nameEn = "Desert Stone Hammer",
            nameAr = "مطرقة الصحراء الحجرية 🔨",
            descriptionEn = "A heavy stone tool for smashing ancient urns and opening sealed tomb doors.",
            descriptionAr = "أداة ثقيلة تحطم الجرار الأثرية وتفتح الأبواب المغلقة.",
            resultType = "tool",
            resultIcon = "tool",
            resultQuantity = 1,
            ingredients = listOf(
                CraftingIngredient("flint_rocks", "Flint & Desert Rocks", "حجارة الصوان والجرانيت", 4, "material"),
                CraftingIngredient("dry_branches", "Dry Palm Wood", "خشب وجريد النخيل", 2, "material")
            )
        ),
        CraftingRecipe(
            recipeId = "craft_water_canteen",
            resultItemId = "water_flask",
            nameEn = "Filtered Water Canteen",
            nameAr = "مطارة ماء نقي 💧",
            descriptionEn = "A desert canteen filled with purified oasis water (+40 Hydration).",
            descriptionAr = "مطارة مياه مصفاة بالرمال تحمي من ظمأ الصحراء (+40 ارتواء).",
            resultType = "water",
            resultIcon = "water",
            resultQuantity = 1,
            ingredients = listOf(
                CraftingIngredient("desert_plants", "Desert Herbs & Fibers", "أعشاب وألياف الصحراء", 2, "material"),
                CraftingIngredient("flint_rocks", "Flint & Desert Rocks", "حجارة الصوان والجرانيت", 2, "material")
            )
        )
    )
}
