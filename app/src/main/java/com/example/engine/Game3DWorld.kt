package com.example.engine

import kotlin.math.*

enum class WeatherState {
    CLEAR, SANDSTORM, FOG, WIND
}

data class WorldEntity(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    var position: Vector3,
    var rotationY: Float = 0f,
    val type: String, // "camel", "horse", "mummy", "bandit", "boss_anubis", "chest", "key_altar", "temple_door", "bonfire", "water_well"
    var health: Int = 100,
    var maxHealth: Int = 100,
    var isInteractive: Boolean = true,
    var isTriggered: Boolean = false,
    var animState: AnimState = AnimState.IDLE
)

data class Particle(
    val position: Vector3,
    val velocity: Vector3,
    var life: Float,
    val maxLife: Float,
    val color: FloatArray
)

data class Footprint(
    val position: Vector3,
    val yaw: Float,
    val isLeft: Boolean,
    var alpha: Float = 0.88f,
    var life: Float = 0f,
    val maxLife: Float = 16f
)

class Game3DWorld {

    // World bounds: 500m x 500m seamless desert
    val worldRadius = 250f

    // Player status
    val playerPos = Vector3(0f, 0.5f, 0f)
    var playerYaw = 0f
    var playerAnimState = AnimState.IDLE
    var health = 100
    var hydration = 100
    var stamina = 100
    var goldCoins = 50
    var keysCollected = 0
    var isTorchActive = false
    var currentMount: String = "none" // "none", "camel", "horse"
    var activeMountEntity: WorldEntity? = null

    // Time of day (0.0 = midnight, 6.0 = dawn, 12.0 = noon, 18.0 = sunset)
    var timeOfDayHours = 10.0f
    var isTimePaused = false
    var isGamePaused = false

    // Weather
    var weatherState = WeatherState.CLEAR
    var sandstormTimer = 0f

    // World Entities (Mounts, Enemies, Chests, Temples)
    val entities = mutableListOf<WorldEntity>()

    // Particles (Sandstorm, Campfire, Torches)
    val particles = mutableListOf<Particle>()

    // Dynamic Footprints in Sand
    val footprints = mutableListOf<Footprint>()
    private val lastFootprintPos = Vector3(0f, -999f, 0f)
    private var isNextFootLeft = true

    init {
        populateWorld()
    }

    private fun populateWorld() {
        entities.clear()

        // 1. Mounts: Camels & Horses near Oasis and Village
        entities.add(WorldEntity("camel_1", "Desert Camel", "جمل الصحراء", Vector3(15f, 0.8f, 20f), 45f, "camel"))
        entities.add(WorldEntity("camel_2", "Oasis Camel", "جمل الواحة", Vector3(-45f, 0.8f, 60f), 120f, "camel"))
        entities.add(WorldEntity("horse_1", "Arabian Stallion", "الحصان العربي", Vector3(25f, 0.8f, -30f), 0f, "horse"))

        // 2. Temples & Pyramids Interactive Altars / Doors
        // Temple of Horus (North East)
        entities.add(WorldEntity("horus_altar", "Horus Key Altar", "محراب مفتاح حورس", Vector3(80f, 1.0f, 110f), 0f, "key_altar"))
        entities.add(WorldEntity("horus_chest", "Horus Treasure Chest", "صندوق معبد حورس الذهبي", Vector3(85f, 1.0f, 115f), 180f, "chest"))

        // Temple of Anubis (North West)
        entities.add(WorldEntity("anubis_altar", "Anubis Seal Altar", "محراب ختَم أنوبيس", Vector3(-90f, 1.0f, 100f), 0f, "key_altar"))
        entities.add(WorldEntity("anubis_relic_chest", "Ancient Temple Relic Crate", "صندوق الآثار القديمة", Vector3(-85f, 1.0f, 110f), 45f, "chest"))
        entities.add(WorldEntity("boss_anubis", "Anubis Guardian", "حارس أنوبيس", Vector3(-95f, 1.2f, 105f), 0f, "boss_anubis", health = 300, maxHealth = 300))

        // Oasis & Dunes Supply Loot Crates
        entities.add(WorldEntity("oasis_crate", "Oasis Supply Crate", "صندوق إمدادات الواحة", Vector3(-35f, 0.8f, 55f), 30f, "chest"))
        entities.add(WorldEntity("dunes_crate", "Dune Explorer Loot Crate", "صندوق مستكشف الكثبان", Vector3(45f, 0.8f, 30f), 110f, "chest"))
        entities.add(WorldEntity("pharaoh_gold_crate", "Pharaonic Gold Crate", "صندوق ذهب الفراعنة", Vector3(10f, 0.8f, -40f), 200f, "chest"))

        // Bandit Stronghold (South West)
        entities.add(WorldEntity("bandit_chest", "Bandit Loot Chest", "صندوق غنائم قطاع الطرق", Vector3(-70f, 0.8f, -80f), 90f, "chest"))
        entities.add(WorldEntity("bandit_1", "Bandit Raider", "مغير قطاع الطرق", Vector3(-65f, 0.8f, -75f), 0f, "bandit", health = 80))
        entities.add(WorldEntity("bandit_2", "Bandit Archer", "رَّامي قطاع الطرق", Vector3(-75f, 0.8f, -85f), 45f, "bandit", health = 80))

        // The Great Pyramid (North Far Center)
        entities.add(WorldEntity("pyramid_door", "Pyramid Sealed Gate", "بوابة الهرم الأكبر", Vector3(0f, 1.5f, 180f), 0f, "temple_door"))
        entities.add(WorldEntity("final_chest", "Legendary Desert Treasure", "كنز الصحراء الأسطوري", Vector3(0f, 1.5f, 210f), 0f, "chest"))

        // Campfires & Wells
        entities.add(WorldEntity("bonfire_village", "Village Campfire", "شعلة القرية", Vector3(0f, 0.5f, -20f), 0f, "bonfire"))
        entities.add(WorldEntity("water_well_1", "Oasis Water Spring", "عين ماء الواحة", Vector3(-40f, 0.5f, 50f), 0f, "water_well"))
    }

    fun update(deltaTime: Float) {
        if (isGamePaused) return

        // Time cycle
        if (!isTimePaused) {
            timeOfDayHours += deltaTime * 0.05f // 1 real second = 3 min in-game
            if (timeOfDayHours >= 24.0f) {
                timeOfDayHours = 0.0f
            }
        }

        // Random Weather Trigger (Clear, Wind, Sandstorm, Fog)
        sandstormTimer += deltaTime
        if (sandstormTimer > 35f) {
            sandstormTimer = 0f
            val rand = Math.random()
            weatherState = when {
                rand < 0.40 -> WeatherState.CLEAR
                rand < 0.72 -> WeatherState.WIND
                rand < 0.88 -> WeatherState.SANDSTORM
                else -> WeatherState.FOG
            }
        }

        // Hydration drain in hot desert noon
        if (timeOfDayHours in 11.0f..15.0f && currentMount == "none") {
            hydration = (hydration - (deltaTime * 0.4f).toInt()).coerceAtLeast(0)
        }

        // Night Mummies Spawn
        val isNight = timeOfDayHours < 5.0f || timeOfDayHours > 19.0f
        if (isNight) {
            updateNightEnemies(deltaTime)
        }

        // Particles update
        updateParticles(deltaTime)

        // Dynamic Footprints update
        updateFootprints(deltaTime)
    }

    private fun updateFootprints(deltaTime: Float) {
        // 1. Spawn new footprint when player moves
        if (playerAnimState == AnimState.WALK || playerAnimState == AnimState.RUN) {
            val dist = playerPos.distanceTo(lastFootprintPos)
            val stepDistThreshold = if (playerAnimState == AnimState.RUN) 0.9f else 0.65f

            if (dist >= stepDistThreshold || lastFootprintPos.y < -100f) {
                lastFootprintPos.set(playerPos.x, playerPos.y, playerPos.z)

                // Calculate perpendicular offset for left/right foot
                val radYaw = Math.toRadians(playerYaw.toDouble()).toFloat()
                val sideSign = if (isNextFootLeft) -1f else 1f
                val sideOffset = 0.16f * sideSign

                val fpX = playerPos.x + cos(radYaw) * sideOffset
                val fpZ = playerPos.z - sin(radYaw) * sideOffset

                val footprint = Footprint(
                    position = Vector3(fpX, 0.02f, fpZ),
                    yaw = playerYaw,
                    isLeft = isNextFootLeft,
                    alpha = 1.0f
                )

                footprints.add(footprint)
                isNextFootLeft = !isNextFootLeft

                // Keep maximum active footprints to optimize performance
                if (footprints.size > 100) {
                    footprints.removeAt(0)
                }
            }
        }

        // 2. Fade footprints over time based on wind / weather
        val fadeRate = when (weatherState) {
            WeatherState.SANDSTORM -> 0.40f // Rapidly blown away by sandstorm
            WeatherState.WIND -> 0.18f      // Moderate wind erosion
            WeatherState.FOG -> 0.06f
            WeatherState.CLEAR -> 0.04f     // Slow natural settling
        }

        val iterator = footprints.iterator()
        while (iterator.hasNext()) {
            val fp = iterator.next()
            fp.alpha -= fadeRate * deltaTime
            if (fp.alpha <= 0f) {
                iterator.remove()
            }
        }
    }

    private fun updateNightEnemies(deltaTime: Float) {
        // AI logic for Mummies and Bandits
        for (entity in entities) {
            if (entity.type == "mummy" || entity.type == "bandit" || entity.type == "boss_anubis") {
                if (entity.health > 0) {
                    val dist = entity.position.distanceTo(playerPos)
                    if (dist < 18f) {
                        // Chase player
                        entity.animState = AnimState.RUN
                        val dir = playerPos.sub(entity.position).normalize()
                        entity.position.x += dir.x * deltaTime * 2.2f
                        entity.position.z += dir.z * deltaTime * 2.2f
                        entity.rotationY = Math.toDegrees(atan2(dir.x.toDouble(), dir.z.toDouble())).toFloat()

                        if (dist < 2.2f) {
                            entity.animState = AnimState.ATTACK
                            // Damage player slightly
                            health = (health - (deltaTime * 8f).toInt()).coerceAtLeast(0)
                        }
                    } else {
                        entity.animState = AnimState.WALK
                    }
                }
            }
        }
    }

    private fun updateParticles(deltaTime: Float) {
        // Sandstorm particles
        if (weatherState == WeatherState.SANDSTORM && particles.size < 120) {
            for (i in 0..3) {
                val p = Particle(
                    position = Vector3(
                        playerPos.x + (Math.random() * 40 - 20).toFloat(),
                        playerPos.y + (Math.random() * 8 + 0.5f).toFloat(),
                        playerPos.z + (Math.random() * 40 - 20).toFloat()
                    ),
                    velocity = Vector3(-6f - (Math.random() * 4).toFloat(), (Math.random() * 1 - 0.5f).toFloat(), -3f),
                    life = 0f,
                    maxLife = 2.5f,
                    color = floatArrayOf(0.9f, 0.75f, 0.45f, 0.6f)
                )
                particles.add(p)
            }
        }

        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val p = iterator.next()
            p.life += deltaTime
            p.position.addLocal(p.velocity.scale(deltaTime))
            if (p.life >= p.maxLife) {
                iterator.remove()
            }
        }
    }

    fun getSunDirection(): Vector3 {
        val angle = ((timeOfDayHours - 6.0f) / 12.0f) * Math.PI.toFloat()
        val y = sin(angle)
        val x = cos(angle)
        return Vector3(x, y, 0.5f).normalize()
    }

    fun getSunColor(): FloatArray {
        return when {
            timeOfDayHours in 6f..7f || timeOfDayHours in 17f..18f -> floatArrayOf(1.0f, 0.5f, 0.2f) // Dawn/Dusk Orange
            timeOfDayHours in 7f..17f -> floatArrayOf(1.0f, 0.95f, 0.8f) // Day Golden White
            else -> floatArrayOf(0.2f, 0.3f, 0.6f) // Night Blue Moon
        }
    }

    fun getAmbientColor(): FloatArray {
        return when {
            timeOfDayHours in 6f..18f -> floatArrayOf(0.4f, 0.35f, 0.3f)
            else -> floatArrayOf(0.08f, 0.1f, 0.2f) // Night dark
        }
    }

    fun getSkyFogColor(): FloatArray {
        if (weatherState == WeatherState.SANDSTORM) {
            return floatArrayOf(0.85f, 0.65f, 0.35f) // Dust yellow fog
        }
        return when {
            timeOfDayHours in 6f..7f || timeOfDayHours in 17f..18f -> floatArrayOf(0.9f, 0.45f, 0.25f)
            timeOfDayHours in 7f..17f -> floatArrayOf(0.4f, 0.7f, 0.95f) // Clear desert sky blue
            else -> floatArrayOf(0.05f, 0.05f, 0.15f) // Night sky
        }
    }
}
