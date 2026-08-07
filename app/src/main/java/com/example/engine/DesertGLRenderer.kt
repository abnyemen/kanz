package com.example.engine

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

class DesertGLRenderer(
    private val context: Context,
    val world: Game3DWorld
) : GLSurfaceView.Renderer {

    private val shader = DesertShader()
    private val animator = SkeletalAnimator()

    // Meshes
    private lateinit var terrainMesh: MeshData
    private lateinit var pyramidMesh: MeshData
    private lateinit var boxMesh: MeshData
    private lateinit var columnMesh: MeshData
    private lateinit var playerHeadMesh: MeshData
    private lateinit var playerTorsoMesh: MeshData
    private lateinit var playerLimbMesh: MeshData
    private lateinit var camelMesh: MeshData
    private lateinit var horseMesh: MeshData
    private lateinit var mummyMesh: MeshData
    private lateinit var chestMesh: MeshData
    private lateinit var particleMesh: MeshData
    private lateinit var footprintMesh: MeshData

    // Matrices
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    // Camera parameters
    var cameraYaw = 0f
    var cameraPitch = 20f
    var cameraDistance = 7f

    private var lastTimeMs = System.currentTimeMillis()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.5f, 0.7f, 0.9f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        shader.init()

        // Generate procedural meshes
        terrainMesh = MeshGenerator.createSandTerrain(
            gridSize = 60,
            tileSize = 4.0f,
            colorSandLight = floatArrayOf(0.95f, 0.8f, 0.45f),
            colorSandDark = floatArrayOf(0.8f, 0.6f, 0.25f)
        )

        pyramidMesh = MeshGenerator.createPyramid(
            baseWidth = 50f,
            height = 35f,
            colorLeft = floatArrayOf(0.9f, 0.75f, 0.4f),
            colorRight = floatArrayOf(0.75f, 0.6f, 0.3f)
        )

        boxMesh = MeshGenerator.createBox(1f, 1f, 1f, floatArrayOf(0.85f, 0.7f, 0.4f))
        columnMesh = MeshGenerator.createCylinder(0.8f, 6f, 12, floatArrayOf(0.9f, 0.85f, 0.7f))

        // Character meshes
        playerHeadMesh = MeshGenerator.createBox(0.4f, 0.4f, 0.4f, floatArrayOf(0.9f, 0.7f, 0.5f)) // Skin
        playerTorsoMesh = MeshGenerator.createBox(0.6f, 0.8f, 0.4f, floatArrayOf(0.8f, 0.3f, 0.2f)) // Red explorer jacket
        playerLimbMesh = MeshGenerator.createBox(0.25f, 0.7f, 0.25f, floatArrayOf(0.2f, 0.2f, 0.3f)) // Denim pants

        // Mounts & Enemies
        camelMesh = MeshGenerator.createBox(1.8f, 1.4f, 2.5f, floatArrayOf(0.75f, 0.55f, 0.3f)) // Camel body
        horseMesh = MeshGenerator.createBox(1.2f, 1.3f, 2.2f, floatArrayOf(0.4f, 0.25f, 0.15f)) // Dark horse
        mummyMesh = MeshGenerator.createBox(0.5f, 1.7f, 0.4f, floatArrayOf(0.95f, 0.92f, 0.85f)) // White bandages
        chestMesh = MeshGenerator.createBox(1.0f, 0.7f, 0.7f, floatArrayOf(0.85f, 0.65f, 0.1f)) // Gold chest

        particleMesh = MeshGenerator.createBox(0.2f, 0.2f, 0.2f, floatArrayOf(1.0f, 0.9f, 0.5f))
        footprintMesh = MeshGenerator.createBox(0.22f, 0.01f, 0.38f, floatArrayOf(0.52f, 0.36f, 0.18f, 0.85f))
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val aspect = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, 60f, aspect, 0.5f, 300f)
    }

    override fun onDrawFrame(gl: GL10?) {
        val now = System.currentTimeMillis()
        val deltaTime = ((now - lastTimeMs) / 1000f).coerceIn(0.001f, 0.1f)
        lastTimeMs = now

        // Update 3D world state if not paused
        if (!world.isGamePaused) {
            world.update(deltaTime)
            animator.update(world.playerAnimState, deltaTime, world.isTorchActive)
        }

        // Clear color based on sky/fog state
        val fogColor = world.getSkyFogColor()
        GLES20.glClearColor(fogColor[0], fogColor[1], fogColor[2], 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Update Camera Matrix
        updateCameraMatrix()

        // Shader setup
        shader.use()
        val sunDir = world.getSunDirection()
        val sunColor = world.getSunColor()
        val ambientColor = world.getAmbientColor()

        GLES20.glUniform3f(shader.sunDirHandle, sunDir.x, sunDir.y, sunDir.z)
        GLES20.glUniform3f(shader.sunColorHandle, sunColor[0], sunColor[1], sunColor[2])
        GLES20.glUniform3f(shader.ambientColorHandle, ambientColor[0], ambientColor[1], ambientColor[2])
        GLES20.glUniform3f(shader.fogColorHandle, fogColor[0], fogColor[1], fogColor[2])

        val fogDensity = if (world.weatherState == WeatherState.SANDSTORM) 0.04f else 0.008f
        GLES20.glUniform1f(shader.fogDensityHandle, fogDensity)

        // Render Terrain
        renderTerrain()

        // Render Sand Footprints
        renderFootprints()

        // Render Great Pyramid & Temples
        renderArchitectures()

        // Render Entities (Camels, Horses, Mummies, Bandits, Chests)
        renderEntities()

        // Render Player Character
        renderPlayerCharacter()

        // Render Particles
        renderParticles()
    }

    private fun updateCameraMatrix() {
        val radYaw = Math.toRadians((cameraYaw + world.playerYaw).toDouble()).toFloat()
        val radPitch = Math.toRadians(cameraPitch.toDouble()).toFloat()

        val camX = world.playerPos.x + cameraDistance * sin(radYaw) * cos(radPitch)
        val camY = world.playerPos.y + cameraDistance * sin(radPitch) + 1.2f
        val camZ = world.playerPos.z + cameraDistance * cos(radYaw) * cos(radPitch)

        val targetY = world.playerPos.y + 1.2f

        Matrix.setLookAtM(
            viewMatrix, 0,
            camX, camY, camZ,
            world.playerPos.x, targetY, world.playerPos.z,
            0f, 1f, 0f
        )
    }

    private fun renderTerrain() {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0)
        terrainMesh.render(shader, mvpMatrix, modelMatrix)
    }

    private fun renderArchitectures() {
        // Great Pyramid (North)
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, 180f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0)
        pyramidMesh.render(shader, mvpMatrix, modelMatrix)

        // Temple of Horus Columns
        for (i in -2..2) {
            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, 80f + i * 5f, 0f, 110f)
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0)
            columnMesh.render(shader, mvpMatrix, modelMatrix)
        }

        // Temple of Anubis Columns
        for (i in -2..2) {
            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, -90f + i * 5f, 0f, 100f)
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0)
            columnMesh.render(shader, mvpMatrix, modelMatrix)
        }
    }

    private fun renderEntities() {
        for (entity in world.entities) {
            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, entity.position.x, entity.position.y, entity.position.z)
            Matrix.rotateM(modelMatrix, 0, entity.rotationY, 0f, 1f, 0f)

            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0)

            when (entity.type) {
                "camel" -> camelMesh.render(shader, mvpMatrix, modelMatrix)
                "horse" -> horseMesh.render(shader, mvpMatrix, modelMatrix)
                "mummy", "bandit", "boss_anubis" -> mummyMesh.render(shader, mvpMatrix, modelMatrix)
                "chest" -> chestMesh.render(shader, mvpMatrix, modelMatrix)
                else -> boxMesh.render(shader, mvpMatrix, modelMatrix)
            }
        }
    }

    private fun renderPlayerCharacter() {
        val pose = animator.currentPose

        // Player Root Matrix
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, world.playerPos.x, world.playerPos.y + pose.bodyOffsetY, world.playerPos.z)
        Matrix.rotateM(modelMatrix, 0, world.playerYaw, 0f, 1f, 0f)
        Matrix.rotateM(modelMatrix, 0, pose.bodyPitch, 1f, 0f, 0f)

        // Torso
        val torsoMatrix = modelMatrix.clone()
        Matrix.translateM(torsoMatrix, 0, 0f, 0.8f, 0f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, torsoMatrix, 0)
        playerTorsoMesh.render(shader, mvpMatrix, torsoMatrix)

        // Head
        val headMatrix = torsoMatrix.clone()
        Matrix.translateM(headMatrix, 0, 0f, 0.6f, 0f)
        Matrix.rotateM(headMatrix, 0, pose.headYaw, 0f, 1f, 0f)
        Matrix.rotateM(headMatrix, 0, pose.headPitch, 1f, 0f, 0f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, headMatrix, 0)
        playerHeadMesh.render(shader, mvpMatrix, headMatrix)

        // Left Leg
        val leftLegMatrix = modelMatrix.clone()
        Matrix.translateM(leftLegMatrix, 0, -0.2f, 0.35f, 0f)
        Matrix.rotateM(leftLegMatrix, 0, pose.leftLegPitch, 1f, 0f, 0f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, leftLegMatrix, 0)
        playerLimbMesh.render(shader, mvpMatrix, leftLegMatrix)

        // Right Leg
        val rightLegMatrix = modelMatrix.clone()
        Matrix.translateM(rightLegMatrix, 0, 0.2f, 0.35f, 0f)
        Matrix.rotateM(rightLegMatrix, 0, pose.rightLegPitch, 1f, 0f, 0f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, rightLegMatrix, 0)
        playerLimbMesh.render(shader, mvpMatrix, rightLegMatrix)

        // Arms
        val leftArmMatrix = torsoMatrix.clone()
        Matrix.translateM(leftArmMatrix, 0, -0.4f, 0.1f, 0f)
        Matrix.rotateM(leftArmMatrix, 0, pose.leftArmPitch, 1f, 0f, 0f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, leftArmMatrix, 0)
        playerLimbMesh.render(shader, mvpMatrix, leftArmMatrix)

        val rightArmMatrix = torsoMatrix.clone()
        Matrix.translateM(rightArmMatrix, 0, 0.4f, 0.1f, 0f)
        Matrix.rotateM(rightArmMatrix, 0, pose.rightArmPitch, 1f, 0f, 0f)
        Matrix.rotateM(rightArmMatrix, 0, pose.rightArmRoll, 0f, 0f, 1f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, rightArmMatrix, 0)
        playerLimbMesh.render(shader, mvpMatrix, rightArmMatrix)
    }

    private fun renderParticles() {
        for (p in world.particles) {
            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, p.position.x, p.position.y, p.position.z)
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0)
            particleMesh.render(shader, mvpMatrix, modelMatrix)
        }
    }

    private fun renderFootprints() {
        if (world.footprints.isEmpty()) return

        for (fp in world.footprints) {
            GLES20.glUniform1f(shader.alphaHandle, fp.alpha)
            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, fp.position.x, fp.position.y, fp.position.z)
            Matrix.rotateM(modelMatrix, 0, fp.yaw, 0f, 1f, 0f)

            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0)

            footprintMesh.render(shader, mvpMatrix, modelMatrix)
        }

        // Reset default alpha back to 1.0f
        GLES20.glUniform1f(shader.alphaHandle, 1.0f)
    }
}
