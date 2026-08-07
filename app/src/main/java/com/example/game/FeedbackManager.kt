package com.example.game

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.audio.HapticEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class ScreenParticle(
    val id: Long = Random.nextLong(),
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    var size: Float,
    var alpha: Float = 1.0f,
    var life: Float = 0f,
    val maxLife: Float = 0.4f,
    val shapeType: ParticleShape = ParticleShape.CIRCLE,
    val textSymbol: String? = null
)

enum class ParticleShape {
    CIRCLE, SPARK, STAR, TEXT
}

data class FeedbackState(
    val shakeOffsetX: Float = 0f,
    val shakeOffsetY: Float = 0f,
    val particles: List<ScreenParticle> = emptyList()
)

class FeedbackManager(context: Context) {

    val hapticEngine: HapticEngine = HapticEngine(context)

    var hapticsEnabled: Boolean
        get() = hapticEngine.hapticsEnabled
        set(value) {
            hapticEngine.hapticsEnabled = value
        }

    private val _feedbackState = MutableStateFlow(FeedbackState())
    val feedbackState: StateFlow<FeedbackState> = _feedbackState.asStateFlow()

    private var currentShakeIntensity = 0f
    private var shakeDurationTimer = 0f
    private val activeParticles = mutableListOf<ScreenParticle>()

    /**
     * Updates ongoing screen shakes and screen-space particles.
     */
    fun update(deltaTime: Float) {
        var updatedShakeX = 0f
        var updatedShakeY = 0f

        if (shakeDurationTimer > 0f) {
            shakeDurationTimer -= deltaTime
            if (shakeDurationTimer <= 0f) {
                currentShakeIntensity = 0f
            } else {
                val factor = shakeDurationTimer.coerceAtLeast(0f) * currentShakeIntensity
                updatedShakeX = (Random.nextFloat() * 2f - 1f) * factor
                updatedShakeY = (Random.nextFloat() * 2f - 1f) * factor
            }
        }

        // Update particles
        if (activeParticles.isNotEmpty()) {
            val iterator = activeParticles.iterator()
            while (iterator.hasNext()) {
                val p = iterator.next()
                p.life += deltaTime
                if (p.life >= p.maxLife) {
                    iterator.remove()
                } else {
                    p.x += p.vx * deltaTime
                    p.y += p.vy * deltaTime
                    p.vy += 300f * deltaTime // subtle gravity
                    p.alpha = (1f - (p.life / p.maxLife)).coerceIn(0f, 1f)
                }
            }
        }

        _feedbackState.value = FeedbackState(
            shakeOffsetX = updatedShakeX,
            shakeOffsetY = updatedShakeY,
            particles = activeParticles.toList()
        )
    }

    /**
     * Triggered when hitting an object or enemy.
     */
    fun triggerHit(
        centerOffset: Offset = Offset(500f, 800f),
        isCritical: Boolean = false
    ) {
        if (hapticsEnabled) {
            hapticEngine.vibrateAttack()
        }

        // Screen shake
        currentShakeIntensity = if (isCritical) 18f else 10f
        shakeDurationTimer = if (isCritical) 0.25f else 0.15f

        // Screen-space impact particles (sparks / hit bursts)
        val particleCount = if (isCritical) 18 else 10
        val baseColor = if (isCritical) Color(0xFFFFD700) else Color(0xFFFF5722)

        for (i in 0 until particleCount) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 400f + 150f
            activeParticles.add(
                ScreenParticle(
                    x = centerOffset.x,
                    y = centerOffset.y,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    color = if (Random.nextBoolean()) baseColor else Color.Yellow,
                    size = Random.nextFloat() * 10f + 6f,
                    maxLife = Random.nextFloat() * 0.3f + 0.2f,
                    shapeType = if (Random.nextBoolean()) ParticleShape.SPARK else ParticleShape.CIRCLE
                )
            )
        }
    }

    /**
     * Triggered when player takes damage.
     */
    fun triggerPlayerDamage() {
        if (hapticsEnabled) {
            hapticEngine.vibrateDamage()
        }

        // Heavy red screen shake
        currentShakeIntensity = 22f
        shakeDurationTimer = 0.3f

        // Red vignette / impact particle bursts around screen edges
        for (i in 0 until 16) {
            val rx = Random.nextFloat() * 1000f
            val ry = Random.nextFloat() * 1500f
            activeParticles.add(
                ScreenParticle(
                    x = rx,
                    y = ry,
                    vx = (Random.nextFloat() - 0.5f) * 200f,
                    vy = -Random.nextFloat() * 150f,
                    color = Color(0xFFD32F2F),
                    size = Random.nextFloat() * 16f + 8f,
                    maxLife = 0.4f,
                    shapeType = ParticleShape.CIRCLE
                )
            )
        }
    }

    /**
     * Triggered when collecting items, coins, chests or keys.
     */
    fun triggerCollect(
        centerOffset: Offset = Offset(500f, 500f),
        symbol: String = "✨"
    ) {
        if (hapticsEnabled) {
            hapticEngine.vibrateCollect()
        }

        // Mild screen pulse
        currentShakeIntensity = 4f
        shakeDurationTimer = 0.12f

        // Golden sparkles & text symbols rising up
        for (i in 0 until 12) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 200f + 80f
            activeParticles.add(
                ScreenParticle(
                    x = centerOffset.x,
                    y = centerOffset.y,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed - 100f, // bias upwards
                    color = Color(0xFFFFD700),
                    size = Random.nextFloat() * 12f + 6f,
                    maxLife = 0.5f,
                    shapeType = if (i % 3 == 0) ParticleShape.STAR else ParticleShape.CIRCLE,
                    textSymbol = if (i == 0) symbol else null
                )
            )
        }
    }

    /**
     * Triggered during actions like jump, roll, torch toggle, mounted riding.
     */
    fun triggerAction() {
        if (hapticsEnabled) {
            hapticEngine.vibrateInteract()
        }

        currentShakeIntensity = 3f
        shakeDurationTimer = 0.08f
    }
}
