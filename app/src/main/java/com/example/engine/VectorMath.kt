package com.example.engine

import android.opengl.Matrix
import kotlin.math.*

data class Vector3(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f) {
    fun set(nx: Float, ny: Float, nz: Float): Vector3 {
        x = nx; y = ny; z = nz
        return this
    }

    fun set(v: Vector3): Vector3 {
        x = v.x; y = v.y; z = v.z
        return this
    }

    fun add(v: Vector3): Vector3 = Vector3(x + v.x, y + v.y, z + v.z)
    fun addLocal(v: Vector3): Vector3 {
        x += v.x; y += v.y; z += v.z
        return this
    }

    fun sub(v: Vector3): Vector3 = Vector3(x - v.x, y - v.y, z - v.z)
    fun subLocal(v: Vector3): Vector3 {
        x -= v.x; y -= v.y; z -= v.z
        return this
    }

    fun scale(s: Float): Vector3 = Vector3(x * s, y * s, z * s)
    fun scaleLocal(s: Float): Vector3 {
        x *= s; y *= s; z *= s
        return this
    }

    fun length(): Float = sqrt(x * x + y * y + z * z)
    fun lengthSquared(): Float = x * x + y * y + z * z

    fun normalize(): Vector3 {
        val len = length()
        if (len > 0.00001f) {
            x /= len; y /= len; z /= len
        }
        return this
    }

    fun dot(v: Vector3): Float = x * v.x + y * v.y + z * v.z

    fun cross(v: Vector3): Vector3 = Vector3(
        y * v.z - z * v.y,
        z * v.x - x * v.z,
        x * v.y - y * v.x
    )

    fun distanceTo(v: Vector3): Float = sqrt((x - v.x) * (x - v.x) + (y - v.y) * (y - v.y) + (z - v.z) * (z - v.z))
    fun distanceSquaredTo(v: Vector3): Float = (x - v.x) * (x - v.x) + (y - v.y) * (y - v.y) + (z - v.z) * (z - v.z)

    fun lerp(target: Vector3, alpha: Float): Vector3 {
        return Vector3(
            x + (target.x - x) * alpha,
            y + (target.y - y) * alpha,
            z + (target.z - z) * alpha
        )
    }
}

class Matrix4x4 {
    val values = FloatArray(16)

    init {
        Matrix.setIdentityM(values, 0)
    }

    fun setIdentity() {
        Matrix.setIdentityM(values, 0)
    }

    fun translate(x: Float, y: Float, z: Float) {
        Matrix.translateM(values, 0, x, y, z)
    }

    fun rotate(angleDeg: Float, x: Float, y: Float, z: Float) {
        Matrix.rotateM(values, 0, angleDeg, x, y, z)
    }

    fun scale(x: Float, y: Float, z: Float) {
        Matrix.scaleM(values, 0, x, y, z)
    }

    fun multiply(other: Matrix4x4, result: Matrix4x4) {
        Matrix.multiplyMM(result.values, 0, this.values, 0, other.values, 0)
    }
}

data class BoundingBox(
    val min: Vector3 = Vector3(-0.5f, 0f, -0.5f),
    val max: Vector3 = Vector3(0.5f, 1.8f, 0.5f)
) {
    fun intersects(otherMin: Vector3, otherMax: Vector3, pos: Vector3, otherPos: Vector3): Boolean {
        val aMinX = pos.x + min.x
        val aMaxX = pos.x + max.x
        val aMinZ = pos.z + min.z
        val aMaxZ = pos.z + max.z

        val bMinX = otherPos.x + otherMin.x
        val bMaxX = otherPos.x + otherMax.x
        val bMinZ = otherPos.z + otherMin.z
        val bMaxZ = otherPos.z + otherMax.z

        return (aMinX <= bMaxX && aMaxX >= bMinX) &&
                (aMinZ <= bMaxZ && aMaxZ >= bMinZ)
    }
}
