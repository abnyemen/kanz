package com.example.engine

import kotlin.math.cos
import kotlin.math.sin

enum class AnimState {
    IDLE, WALK, RUN, SPRINT, JUMP, DOUBLE_JUMP, ROLL, CLIMB, SWIM, FALL, LAND,
    CROUCH, ATTACK, BLOCK, DRINK, PUSH, OPEN_CHEST, PICK_ITEM, TORCH_HOLD,
    RIDE_CAMEL, RIDE_HORSE, VICTORY, DEFEAT, DEATH, SLEEP, SIT, WAVE, CELEBRATE
}

class CharacterPose {
    var headPitch: Float = 0f
    var headYaw: Float = 0f
    var leftArmPitch: Float = 0f
    var rightArmPitch: Float = 0f
    var leftArmRoll: Float = 0f
    var rightArmRoll: Float = 0f
    var leftLegPitch: Float = 0f
    var rightLegPitch: Float = 0f
    var bodyOffsetY: Float = 0f
    var bodyPitch: Float = 0f
    var eyeBlink: Float = 1f // 1 = open, 0 = closed
}

class SkeletalAnimator {

    private var timeSeconds: Float = 0f
    val currentPose = CharacterPose()

    fun update(animState: AnimState, deltaTime: Float, isTorchActive: Boolean) {
        timeSeconds += deltaTime

        // Blinking logic
        val blinkTime = (timeSeconds % 3.5f)
        currentPose.eyeBlink = if (blinkTime > 3.3f) 0.1f else 1.0f

        val wave = sin(timeSeconds * 6f)
        val fastWave = sin(timeSeconds * 12f)

        when (animState) {
            AnimState.IDLE -> {
                currentPose.bodyOffsetY = sin(timeSeconds * 2f) * 0.03f // Breathing motion
                currentPose.leftArmPitch = sin(timeSeconds * 2f) * 5f
                currentPose.rightArmPitch = if (isTorchActive) -60f else -sin(timeSeconds * 2f) * 5f
                currentPose.leftLegPitch = 0f
                currentPose.rightLegPitch = 0f
                currentPose.bodyPitch = 0f
            }

            AnimState.WALK -> {
                currentPose.bodyOffsetY = sin(timeSeconds * 8f).coerceAtLeast(0f) * 0.05f
                currentPose.leftArmPitch = wave * 30f
                currentPose.rightArmPitch = if (isTorchActive) -60f else -wave * 30f
                currentPose.leftLegPitch = -wave * 35f
                currentPose.rightLegPitch = wave * 35f
                currentPose.bodyPitch = 3f
            }

            AnimState.RUN -> {
                currentPose.bodyOffsetY = sin(timeSeconds * 14f).coerceAtLeast(0f) * 0.1f
                currentPose.leftArmPitch = fastWave * 55f
                currentPose.rightArmPitch = if (isTorchActive) -60f else -fastWave * 55f
                currentPose.leftLegPitch = -fastWave * 60f
                currentPose.rightLegPitch = fastWave * 60f
                currentPose.bodyPitch = 12f
            }

            AnimState.SPRINT -> {
                currentPose.bodyOffsetY = sin(timeSeconds * 18f).coerceAtLeast(0f) * 0.15f
                currentPose.leftArmPitch = fastWave * 75f
                currentPose.rightArmPitch = if (isTorchActive) -70f else -fastWave * 75f
                currentPose.leftLegPitch = -fastWave * 80f
                currentPose.rightLegPitch = fastWave * 80f
                currentPose.bodyPitch = 22f
            }

            AnimState.JUMP, AnimState.DOUBLE_JUMP -> {
                currentPose.bodyOffsetY = 0.4f
                currentPose.leftArmPitch = -120f
                currentPose.rightArmPitch = if (isTorchActive) -70f else -120f
                currentPose.leftLegPitch = 40f
                currentPose.rightLegPitch = -20f
                currentPose.bodyPitch = -10f
            }

            AnimState.ROLL -> {
                val rollProgress = (timeSeconds * 8f) % 360f
                currentPose.bodyOffsetY = -0.3f
                currentPose.bodyPitch = rollProgress
                currentPose.leftArmPitch = 90f
                currentPose.rightArmPitch = 90f
                currentPose.leftLegPitch = 60f
                currentPose.rightLegPitch = 60f
            }

            AnimState.ATTACK -> {
                val slashTime = (timeSeconds * 10f) % 1f
                currentPose.rightArmPitch = -120f + slashTime * 180f
                currentPose.rightArmRoll = -45f
                currentPose.leftArmPitch = -20f
                currentPose.bodyPitch = 15f
            }

            AnimState.BLOCK -> {
                currentPose.rightArmPitch = -80f
                currentPose.rightArmRoll = 60f
                currentPose.leftArmPitch = -40f
                currentPose.bodyPitch = -5f
            }

            AnimState.RIDE_CAMEL, AnimState.RIDE_HORSE -> {
                currentPose.bodyOffsetY = 1.2f // Seated high on saddle
                currentPose.leftLegPitch = 45f
                currentPose.rightLegPitch = 45f
                currentPose.leftArmPitch = -30f
                currentPose.rightArmPitch = if (isTorchActive) -60f else -30f
                currentPose.bodyPitch = 5f + sin(timeSeconds * 4f) * 3f
            }

            AnimState.DRINK -> {
                currentPose.rightArmPitch = -110f
                currentPose.headPitch = -25f
                currentPose.bodyOffsetY = -0.1f
            }

            AnimState.OPEN_CHEST, AnimState.PICK_ITEM -> {
                currentPose.bodyOffsetY = -0.4f
                currentPose.bodyPitch = 35f
                currentPose.leftArmPitch = -45f
                currentPose.rightArmPitch = -45f
            }

            AnimState.VICTORY, AnimState.CELEBRATE -> {
                currentPose.bodyOffsetY = sin(timeSeconds * 6f).coerceAtLeast(0f) * 0.1f
                currentPose.leftArmPitch = -150f + sin(timeSeconds * 8f) * 20f
                currentPose.rightArmPitch = -150f - sin(timeSeconds * 8f) * 20f
                currentPose.headPitch = -20f
            }

            AnimState.DEATH -> {
                currentPose.bodyOffsetY = -0.7f
                currentPose.bodyPitch = 90f
                currentPose.leftArmPitch = 0f
                currentPose.rightArmPitch = 0f
                currentPose.leftLegPitch = 0f
                currentPose.rightLegPitch = 0f
            }

            else -> {
                currentPose.bodyOffsetY = 0f
                currentPose.leftArmPitch = 0f
                currentPose.rightArmPitch = 0f
                currentPose.leftLegPitch = 0f
                currentPose.rightLegPitch = 0f
            }
        }
    }
}
