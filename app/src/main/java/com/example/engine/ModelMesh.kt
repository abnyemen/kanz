package com.example.engine

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MeshData(
    val vertexBuffer: FloatBuffer,
    val normalBuffer: FloatBuffer,
    val colorBuffer: FloatBuffer,
    val indexBuffer: ShortBuffer,
    val indexCount: Int
) {
    fun render(shader: DesertShader, mvpMatrix: FloatArray, modelMatrix: FloatArray) {
        GLES20.glUniformMatrix4fv(shader.mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(shader.modelMatrixHandle, 1, false, modelMatrix, 0)

        vertexBuffer.position(0)
        GLES20.glVertexAttribPointer(shader.positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glEnableVertexAttribArray(shader.positionHandle)

        normalBuffer.position(0)
        GLES20.glVertexAttribPointer(shader.normalHandle, 3, GLES20.GL_FLOAT, false, 0, normalBuffer)
        GLES20.glEnableVertexAttribArray(shader.normalHandle)

        colorBuffer.position(0)
        GLES20.glVertexAttribPointer(shader.colorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer)
        GLES20.glEnableVertexAttribArray(shader.colorHandle)

        indexBuffer.position(0)
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, indexBuffer)
    }
}

object MeshGenerator {

    fun createBox(w: Float, h: Float, d: Float, color: FloatArray): MeshData {
        val hw = w / 2f
        val hh = h / 2f
        val hd = d / 2f

        val vertices = floatArrayOf(
            // Front
            -hw, -hh,  hd,   hw, -hh,  hd,   hw,  hh,  hd,  -hw,  hh,  hd,
            // Back
            -hw, -hh, -hd,  -hw,  hh, -hd,   hw,  hh, -hd,   hw, -hh, -hd,
            // Top
            -hw,  hh, -hd,  -hw,  hh,  hd,   hw,  hh,  hd,   hw,  hh, -hd,
            // Bottom
            -hw, -hh, -hd,   hw, -hh, -hd,   hw, -hh,  hd,  -hw, -hh,  hd,
            // Right
             hw, -hh, -hd,   hw,  hh, -hd,   hw,  hh,  hd,   hw, -hh,  hd,
            // Left
            -hw, -hh, -hd,  -hw, -hh,  hd,  -hw,  hh,  hd,  -hw,  hh, -hd
        )

        val normals = floatArrayOf(
            0f, 0f, 1f,  0f, 0f, 1f,  0f, 0f, 1f,  0f, 0f, 1f,
            0f, 0f,-1f,  0f, 0f,-1f,  0f, 0f,-1f,  0f, 0f,-1f,
            0f, 1f, 0f,  0f, 1f, 0f,  0f, 1f, 0f,  0f, 1f, 0f,
            0f,-1f, 0f,  0f,-1f, 0f,  0f,-1f, 0f,  0f,-1f, 0f,
            1f, 0f, 0f,  1f, 0f, 0f,  1f, 0f, 0f,  1f, 0f, 0f,
           -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f
        )

        val colors = FloatArray(24 * 4)
        for (i in 0 until 24) {
            colors[i * 4] = color[0]
            colors[i * 4 + 1] = color[1]
            colors[i * 4 + 2] = color[2]
            colors[i * 4 + 3] = if (color.size > 3) color[3] else 1f
        }

        val indices = shortArrayOf(
            0,1,2, 0,2,3,
            4,5,6, 4,6,7,
            8,9,10, 8,10,11,
            12,13,14, 12,14,15,
            16,17,18, 16,18,19,
            20,21,22, 20,22,23
        )

        return buildBuffers(vertices, normals, colors, indices)
    }

    fun createPyramid(baseWidth: Float, height: Float, colorLeft: FloatArray, colorRight: FloatArray): MeshData {
        val hw = baseWidth / 2f
        val vertices = floatArrayOf(
            // Front face
            0f, height, 0f,  -hw, 0f, hw,   hw, 0f, hw,
            // Right face
            0f, height, 0f,   hw, 0f, hw,   hw, 0f, -hw,
            // Back face
            0f, height, 0f,   hw, 0f, -hw, -hw, 0f, -hw,
            // Left face
            0f, height, 0f,  -hw, 0f, -hw, -hw, 0f, hw,
            // Base
            -hw, 0f, -hw,  hw, 0f, -hw,  hw, 0f, hw,  -hw, 0f, hw
        )

        val normals = floatArrayOf(
            0f, 0.7f, 0.7f,  0f, 0.7f, 0.7f,  0f, 0.7f, 0.7f,
            0.7f, 0.7f, 0f,  0.7f, 0.7f, 0f,  0.7f, 0.7f, 0f,
            0f, 0.7f,-0.7f,  0f, 0.7f,-0.7f,  0f, 0.7f,-0.7f,
           -0.7f, 0.7f, 0f, -0.7f, 0.7f, 0f, -0.7f, 0.7f, 0f,
            0f, -1f, 0f,     0f, -1f, 0f,     0f, -1f, 0f,     0f, -1f, 0f
        )

        val colors = FloatArray(16 * 4)
        for (i in 0 until 12) {
            val c = if (i in 3..5) colorRight else colorLeft
            colors[i * 4] = c[0]
            colors[i * 4 + 1] = c[1]
            colors[i * 4 + 2] = c[2]
            colors[i * 4 + 3] = 1f
        }
        for (i in 12 until 16) {
            colors[i * 4] = colorRight[0] * 0.7f
            colors[i * 4 + 1] = colorRight[1] * 0.7f
            colors[i * 4 + 2] = colorRight[2] * 0.7f
            colors[i * 4 + 3] = 1f
        }

        val indices = shortArrayOf(
            0,1,2,  3,4,5,  6,7,8,  9,10,11,
            12,13,14, 12,14,15
        )

        return buildBuffers(vertices, normals, colors, indices)
    }

    fun createCylinder(radius: Float, height: Float, segments: Int, color: FloatArray): MeshData {
        val vertices = mutableListOf<Float>()
        val normals = mutableListOf<Float>()
        val colors = mutableListOf<Float>()
        val indices = mutableListOf<Short>()

        // Side vertices
        for (i in 0..segments) {
            val angle = (2.0 * Math.PI * i / segments).toFloat()
            val cosA = cos(angle)
            val sinA = sin(angle)

            val x = radius * cosA
            val z = radius * sinA

            // Bottom vertex
            vertices.addAll(listOf(x, 0f, z))
            normals.addAll(listOf(cosA, 0f, sinA))
            colors.addAll(color.toList())

            // Top vertex
            vertices.addAll(listOf(x, height, z))
            normals.addAll(listOf(cosA, 0f, sinA))
            colors.addAll(color.toList())
        }

        val baseIndex = 0
        for (i in 0 until segments) {
            val current = (baseIndex + i * 2).toShort()
            val next = (baseIndex + (i + 1) * 2).toShort()

            indices.add(current)
            indices.add((current + 1).toShort())
            indices.add(next)

            indices.add(next)
            indices.add((current + 1).toShort())
            indices.add((next + 1).toShort())
        }

        return buildBuffers(
            vertices.toFloatArray(),
            normals.toFloatArray(),
            colors.toFloatArray(),
            indices.toShortArray()
        )
    }

    fun createSandTerrain(gridSize: Int, tileSize: Float, colorSandLight: FloatArray, colorSandDark: FloatArray): MeshData {
        val vertices = mutableListOf<Float>()
        val normals = mutableListOf<Float>()
        val colors = mutableListOf<Float>()
        val indices = mutableListOf<Short>()

        val halfSize = (gridSize * tileSize) / 2f

        for (z in 0..gridSize) {
            for (x in 0..gridSize) {
                val worldX = x * tileSize - halfSize
                val worldZ = z * tileSize - halfSize

                // Procedural Sand Dunes equation
                val h1 = sin(worldX * 0.05f) * cos(worldZ * 0.05f) * 3.5f
                val h2 = sin(worldX * 0.12f + worldZ * 0.08f) * 1.5f
                val height = (h1 + h2).coerceAtLeast(-1.5f)

                vertices.add(worldX)
                vertices.add(height)
                vertices.add(worldZ)

                // Height-based normal approximation
                val nx = -0.1f * cos(worldX * 0.05f)
                val ny = 1.0f
                val nz = -0.1f * sin(worldZ * 0.05f)
                val len = sqrt(nx * nx + ny * ny + nz * nz)
                normals.addAll(listOf(nx / len, ny / len, nz / len))

                // Sand color variation based on dune height
                val factor = ((height + 2f) / 5f).coerceIn(0f, 1f)
                val r = colorSandDark[0] + (colorSandLight[0] - colorSandDark[0]) * factor
                val g = colorSandDark[1] + (colorSandLight[1] - colorSandDark[1]) * factor
                val b = colorSandDark[2] + (colorSandLight[2] - colorSandDark[2]) * factor
                colors.addAll(listOf(r, g, b, 1f))
            }
        }

        for (z in 0 until gridSize) {
            for (x in 0 until gridSize) {
                val topLeft = (z * (gridSize + 1) + x).toShort()
                val topRight = (topLeft + 1).toShort()
                val bottomLeft = ((z + 1) * (gridSize + 1) + x).toShort()
                val bottomRight = (bottomLeft + 1).toShort()

                indices.add(topLeft)
                indices.add(bottomLeft)
                indices.add(topRight)

                indices.add(topRight)
                indices.add(bottomLeft)
                indices.add(bottomRight)
            }
        }

        return buildBuffers(
            vertices.toFloatArray(),
            normals.toFloatArray(),
            colors.toFloatArray(),
            indices.toShortArray()
        )
    }

    fun createFootprint(): MeshData {
        // Flat indented impression in sand (darker packed sand color)
        return createBox(0.18f, 0.005f, 0.38f, floatArrayOf(0.48f, 0.32f, 0.15f, 0.9f))
    }

    private fun buildBuffers(
        vertices: FloatArray,
        normals: FloatArray,
        colors: FloatArray,
        indices: ShortArray
    ): MeshData {
        val vbb = ByteBuffer.allocateDirect(vertices.size * 4).order(ByteOrder.nativeOrder())
        val vertexBuffer = vbb.asFloatBuffer().apply { put(vertices); position(0) }

        val nbb = ByteBuffer.allocateDirect(normals.size * 4).order(ByteOrder.nativeOrder())
        val normalBuffer = nbb.asFloatBuffer().apply { put(normals); position(0) }

        val cbb = ByteBuffer.allocateDirect(colors.size * 4).order(ByteOrder.nativeOrder())
        val colorBuffer = cbb.asFloatBuffer().apply { put(colors); position(0) }

        val ibb = ByteBuffer.allocateDirect(indices.size * 2).order(ByteOrder.nativeOrder())
        val indexBuffer = ibb.asShortBuffer().apply { put(indices); position(0) }

        return MeshData(vertexBuffer, normalBuffer, colorBuffer, indexBuffer, indices.size)
    }
}
