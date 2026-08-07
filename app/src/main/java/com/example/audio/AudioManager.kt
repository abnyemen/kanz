package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager as SystemAudioManager
import android.media.AudioTrack
import com.example.engine.Vector3
import com.example.engine.WeatherState
import kotlinx.coroutines.*
import kotlin.math.*
import kotlin.random.Random

enum class Biome {
    OPEN_DUNES,
    OASIS,
    ANCIENT_TEMPLE,
    DESERT_VILLAGE,
    BANDIT_CAMP
}

class AudioManager(
    private val context: Context,
    private val soundEngine: SoundEngine
) {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var audioTrack: AudioTrack? = null
    private var isPlaying = false

    // Dynamic layer target & current smooth volumes (0.0 to 1.0)
    private var currentWindVol = 0.2f
    private var targetWindVol = 0.2f

    private var currentCricketVol = 0.0f
    private var targetCricketVol = 0.0f

    private var currentBirdVol = 0.0f
    private var targetBirdVol = 0.0f

    private var currentWaterVol = 0.0f
    private var targetWaterVol = 0.0f

    private var currentTempleVol = 0.0f
    private var targetTempleVol = 0.0f

    // Master settings
    private var musicVolume = 0.7f
    private var sfxVolume = 0.8f
    private var sfxEnabled = true
    private var isGamePaused = false

    // State tracking
    var currentBiome: Biome = Biome.OPEN_DUNES
        private set

    // Synthesis phase accumulators
    private var sampleRate = 22050
    private var phaseWindFilter = 0f
    private var phaseCricketTime = 0f
    private var phaseBirdTime = 0f
    private var phaseTemple1 = 0.0
    private var phaseTemple2 = 0.0
    private var phaseWaterTime = 0f
    private var globalTime = 0.0

    init {
        startAmbientLoop()
    }

    /**
     * Determines the biome based on player (X, Z) coordinates.
     */
    fun calculateBiome(x: Float, z: Float): Biome {
        // Oasis: spring water & palm trees around (-40, 50)
        if (hypot(x - (-40f), z - 50f) < 42f) return Biome.OASIS

        // Desert Village & Campfire around (0, -20)
        if (hypot(x - 0f, z - (-20f)) < 35f) return Biome.DESERT_VILLAGE

        // Bandit Camp in rocky canyons around (-70, -80)
        if (hypot(x - (-70f), z - (-80f)) < 40f) return Biome.BANDIT_CAMP

        // Ancient Temples & Pyramids: Horus (80, 110), Anubis (-90, 100), Pyramid (0, 180)
        if (hypot(x - 80f, z - 110f) < 45f ||
            hypot(x - (-90f), z - 100f) < 45f ||
            hypot(x - 0f, z - 180f) < 60f) {
            return Biome.ANCIENT_TEMPLE
        }

        return Biome.OPEN_DUNES
    }

    fun getBiomeNameEn(biome: Biome): String {
        return when (biome) {
            Biome.OPEN_DUNES -> "Deep Sand Dunes"
            Biome.OASIS -> "Oasis of Hope"
            Biome.ANCIENT_TEMPLE -> "Ancient Temple Ruins"
            Biome.DESERT_VILLAGE -> "Desert Oasis Village"
            Biome.BANDIT_CAMP -> "Bandit Canyon Stronghold"
        }
    }

    fun getBiomeNameAr(biome: Biome): String {
        return when (biome) {
            Biome.OPEN_DUNES -> "كثبان الصحراء العميقة"
            Biome.OASIS -> "واحة الأمل بالنخيل"
            Biome.ANCIENT_TEMPLE -> "أطلال المعابد القديمة"
            Biome.DESERT_VILLAGE -> "قرية الواحة الصحراوية"
            Biome.BANDIT_CAMP -> "معقل قطاع الطرق الصخري"
        }
    }

    /**
     * Main update method called periodically from game loop or frame ticker.
     */
    fun updateEnvironment(
        playerPos: Vector3,
        timeOfDayHours: Float,
        weatherState: WeatherState,
        isGamePaused: Boolean,
        musicVolume: Float,
        sfxVolume: Float,
        sfxEnabled: Boolean
    ) {
        this.isGamePaused = isGamePaused
        this.musicVolume = musicVolume
        this.sfxVolume = sfxVolume
        this.sfxEnabled = sfxEnabled

        currentBiome = calculateBiome(playerPos.x, playerPos.z)

        if (!sfxEnabled || musicVolume <= 0.01f || isGamePaused) {
            targetWindVol = 0f
            targetCricketVol = 0f
            targetBirdVol = 0f
            targetWaterVol = 0f
            targetTempleVol = 0f
            return
        }

        val isNight = timeOfDayHours < 5.0f || timeOfDayHours > 19.0f
        val isDay = timeOfDayHours in 6.0f..18.0f
        val masterVol = musicVolume.coerceIn(0f, 1f) * 0.45f

        // --- 1. WIND LAYER TARGET ---
        var windBase = when (currentBiome) {
            Biome.OPEN_DUNES -> 0.45f
            Biome.ANCIENT_TEMPLE -> 0.35f
            Biome.BANDIT_CAMP -> 0.4f
            Biome.DESERT_VILLAGE -> 0.2f
            Biome.OASIS -> 0.15f
        }
        if (weatherState == WeatherState.SANDSTORM) {
            windBase = 0.85f
        } else if (weatherState == WeatherState.WIND) {
            windBase += 0.25f
        }
        targetWindVol = (windBase * masterVol).coerceIn(0f, 1f)

        // --- 2. CRICKETS LAYER TARGET (Nighttime) ---
        var cricketBase = if (isNight) {
            when (currentBiome) {
                Biome.OASIS -> 0.7f
                Biome.DESERT_VILLAGE -> 0.5f
                Biome.OPEN_DUNES -> 0.3f
                Biome.BANDIT_CAMP -> 0.25f
                Biome.ANCIENT_TEMPLE -> 0.15f
            }
        } else 0f
        targetCricketVol = (cricketBase * masterVol).coerceIn(0f, 1f)

        // --- 3. DESERT BIRDS LAYER TARGET (Daytime) ---
        var birdBase = if (isDay) {
            when (currentBiome) {
                Biome.OASIS -> 0.75f
                Biome.DESERT_VILLAGE -> 0.5f
                Biome.OPEN_DUNES -> 0.2f
                Biome.BANDIT_CAMP -> 0.15f
                Biome.ANCIENT_TEMPLE -> 0.05f
            }
        } else 0f
        targetBirdVol = (birdBase * masterVol).coerceIn(0f, 1f)

        // --- 4. OASIS WATER SPRING TARGET ---
        var waterBase = if (currentBiome == Biome.OASIS) 0.65f else 0f
        targetWaterVol = (waterBase * masterVol).coerceIn(0f, 1f)

        // --- 5. TEMPLE MYSTICAL ECHO TARGET ---
        var templeBase = if (currentBiome == Biome.ANCIENT_TEMPLE) 0.6f else 0f
        targetTempleVol = (templeBase * masterVol).coerceIn(0f, 1f)
    }

    private fun startAmbientLoop() {
        if (isPlaying) return
        isPlaying = true

        scope.launch(Dispatchers.Default) {
            val minBufSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = maxOf(minBufSize, 4410)

            try {
                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack?.play()

                val frameSamples = 1102 // ~50ms audio chunk
                val pcmBuffer = ShortArray(frameSamples)

                while (isPlaying && isActive) {
                    // Smooth volume interpolation toward targets
                    currentWindVol += (targetWindVol - currentWindVol) * 0.08f
                    currentCricketVol += (targetCricketVol - currentCricketVol) * 0.08f
                    currentBirdVol += (targetBirdVol - currentBirdVol) * 0.08f
                    currentWaterVol += (targetWaterVol - currentWaterVol) * 0.08f
                    currentTempleVol += (targetTempleVol - currentTempleVol) * 0.08f

                    // Synthesize chunk
                    generateAmbientChunk(pcmBuffer, frameSamples)

                    // Write to stream
                    audioTrack?.write(pcmBuffer, 0, frameSamples)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun generateAmbientChunk(buffer: ShortArray, size: Int) {
        val dt = 1.0 / sampleRate

        for (i in 0 until size) {
            globalTime += dt
            var sampleSum = 0.0

            // 1. Wind synthesis (filtered lowpass noise + sinus gusting)
            if (currentWindVol > 0.005f) {
                val rawNoise = (Random.nextDouble() * 2.0 - 1.0)
                phaseWindFilter = (phaseWindFilter * 0.88f + rawNoise * 0.12f).toFloat()
                val gust = 0.65 + 0.35 * sin(globalTime * 0.7) + 0.15 * sin(globalTime * 2.3)
                sampleSum += phaseWindFilter * gust * currentWindVol
            }

            // 2. Cricket chirps (4.8 kHz pulsed tone bursts)
            if (currentCricketVol > 0.005f) {
                phaseCricketTime += dt.toFloat()
                if (phaseCricketTime > 2.2f) phaseCricketTime = 0f

                // Chirp burst in first 0.12s of cycle
                val chirpActive = phaseCricketTime < 0.12f
                if (chirpActive) {
                    val chirpSubCycle = (phaseCricketTime * 40f) % 1f
                    if (chirpSubCycle < 0.5f) {
                        val tone = sin(2.0 * Math.PI * 4800.0 * globalTime)
                        sampleSum += tone * 0.35 * currentCricketVol
                    }
                }
            }

            // 3. Desert Birds / Oasis Sparrows (periodic sweeping melody)
            if (currentBirdVol > 0.005f) {
                phaseBirdTime += dt.toFloat()
                if (phaseBirdTime > 4.5f) phaseBirdTime = 0f

                if (phaseBirdTime < 0.35f) {
                    val progress = phaseBirdTime / 0.35f
                    val freq = 1800.0 + sin(progress * Math.PI) * 800.0
                    val env = sin(progress * Math.PI)
                    val tone = sin(2.0 * Math.PI * freq * globalTime)
                    sampleSum += tone * env * 0.3 * currentBirdVol
                }
            }

            // 4. Oasis Water Spring (bandpass filtered water noise)
            if (currentWaterVol > 0.005f) {
                phaseWaterTime += dt.toFloat()
                val waterNoise = (Random.nextDouble() * 2.0 - 1.0)
                val bubbleMod = 0.5 + 0.5 * sin(2.0 * Math.PI * 8.0 * globalTime)
                sampleSum += waterNoise * bubbleMod * 0.25 * currentWaterVol
            }

            // 5. Temple Mystical Echo (110Hz + 165Hz deep resonant hum)
            if (currentTempleVol > 0.005f) {
                phaseTemple1 += 2.0 * Math.PI * 110.0 * dt
                phaseTemple2 += 2.0 * Math.PI * 165.0 * dt
                if (phaseTemple1 > 2.0 * Math.PI) phaseTemple1 -= 2.0 * Math.PI
                if (phaseTemple2 > 2.0 * Math.PI) phaseTemple2 -= 2.0 * Math.PI

                val phaser = 0.7 + 0.3 * sin(globalTime * 0.4)
                val tone1 = sin(phaseTemple1)
                val tone2 = sin(phaseTemple2) * 0.5
                sampleSum += (tone1 + tone2) * phaser * 0.3 * currentTempleVol
            }

            val finalClamped = (sampleSum * Short.MAX_VALUE).toInt().coerceIn(
                Short.MIN_VALUE.toInt(),
                Short.MAX_VALUE.toInt()
            ).toShort()

            buffer[i] = finalClamped
        }
    }

    fun release() {
        isPlaying = false
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        scope.cancel()
    }
}
