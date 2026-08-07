package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.*
import kotlin.math.sin

class SoundEngine(private val context: Context) {

    private var soundEnabled = true
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun setSoundEnabled(enabled: Boolean) {
        soundEnabled = enabled
    }

    fun playFootstep() {
        if (!soundEnabled) return
        scope.launch {
            generateTone(frequency = 120.0, durationMs = 60, volume = 0.15f, noise = 0.4f)
        }
    }

    fun playSwordSwing() {
        if (!soundEnabled) return
        scope.launch {
            // Whoosh sound sweep
            val sampleRate = 22050
            val numSamples = sampleRate * 180 / 1000
            val samples = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val freq = 400.0 - (t * 1800.0).coerceAtMost(300.0)
                val envelope = sin(Math.PI * (i.toDouble() / numSamples))
                val noise = (Math.random() * 2 - 1) * 0.3
                val sampleVal = (sin(2.0 * Math.PI * freq * t) + noise) * envelope * 0.3 * Short.MAX_VALUE
                samples[i] = sampleVal.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playRawPcm(samples, sampleRate)
        }
    }

    fun playSwordHit() {
        if (!soundEnabled) return
        scope.launch {
            // Metallic clash
            generateTone(frequency = 880.0, durationMs = 120, volume = 0.4f, noise = 0.6f)
        }
    }

    fun playChestOpen() {
        if (!soundEnabled) return
        scope.launch {
            // Arpeggio chime (C5, E5, G5, C6)
            val notes = listOf(523.25, 659.25, 783.99, 1046.50)
            for (note in notes) {
                generateTone(frequency = note, durationMs = 100, volume = 0.35f)
                delay(80)
            }
        }
    }

    fun playPickupChime() {
        if (!soundEnabled) return
        scope.launch {
            generateTone(frequency = 659.25, durationMs = 80, volume = 0.3f)
            delay(60)
            generateTone(frequency = 1046.50, durationMs = 140, volume = 0.35f)
        }
    }

    fun playCamelGrunt() {
        if (!soundEnabled) return
        scope.launch {
            generateTone(frequency = 90.0, durationMs = 250, volume = 0.4f, noise = 0.5f)
        }
    }

    fun playDoorRumble() {
        if (!soundEnabled) return
        scope.launch {
            generateTone(frequency = 65.0, durationMs = 600, volume = 0.5f, noise = 0.7f)
        }
    }

    fun playVictoryFanfare() {
        if (!soundEnabled) return
        scope.launch {
            val melody = listOf(
                523.25 to 150L, // C5
                659.25 to 150L, // E5
                783.99 to 150L, // G5
                1046.50 to 400L // C6
            )
            for ((freq, dur) in melody) {
                generateTone(frequency = freq, durationMs = dur.toInt(), volume = 0.4f)
                delay(dur)
            }
        }
    }

    private fun generateTone(frequency: Double, durationMs: Int, volume: Float = 0.3f, noise: Float = 0f) {
        val sampleRate = 22050
        val numSamples = sampleRate * durationMs / 1000
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val env = sin(Math.PI * (i.toDouble() / numSamples))
            val pureWave = sin(2.0 * Math.PI * frequency * t)
            val noiseWave = (Math.random() * 2 - 1) * noise
            val signal = (pureWave * (1f - noise) + noiseWave) * env * volume * Short.MAX_VALUE
            samples[i] = signal.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playRawPcm(samples, sampleRate)
    }

    private fun playRawPcm(samples: ShortArray, sampleRate: Int) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(samples.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(samples, 0, samples.size)
            audioTrack.play()
            scope.launch {
                delay((samples.size * 1000L / sampleRate) + 100)
                audioTrack.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        scope.cancel()
    }
}
